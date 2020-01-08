package myapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.tuhin.myapplication.BuildConfig;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.UpdateMust;
import com.example.tuhin.myapplication.home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class OrphanUtilityMethods {

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static String getCurrentUserName(Context context){
        SharedPreferences sPref = context.getSharedPreferences(
                context.getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        Log.i("current_user_name", sPref.getString("name", ""));
        return sPref.getString("name", "");
    }

    public static int getAccountType(Context context){
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sPref = context.getSharedPreferences(
                context.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return sPref.getInt(user.getEmail(), AccountTypes.UNSET);
    }

    public static void sendFollowingNotification(final Context context,
                                                 final String personOrRestLink,
                                                 final boolean isPerson){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String collectionName = isPerson ? "person_vital": "rest_vital";

        final Map<String, Object> notification = new HashMap<>();
        notification.put("t", NotificationTypes.NOTIF_FOLLOW);
        notification.put("ts", new Date());

        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);
        String name = OrphanUtilityMethods.getCurrentUserName(context);
        if(name!=null && !name.equals("")){
            who.put("n", name);
            notification.put("w", who);
            addFollowNotificatioToDB(context, notification, personOrRestLink);
        }else{
            // fail-safe in case current name is not set in shared preferences
            // might not be needed(not sure yet)
            // could also delegate this work to a cloud function
            db.collection(collectionName)
                    .document(currentUserLink)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                try{
                                    who.put("n", documentSnapshot.getString("n"));
                                    notification.put("w", who);
                                    addFollowNotificatioToDB(context, notification, personOrRestLink);
                                }catch (Exception e){
                                    Log.e("error", e.getMessage());
                                }
                            }
                        }
                    });
        }


    }

    public static void addFollowNotificatioToDB(Context context, Map<String, Object> notification, String personLink){
        db.collection("notifications")
                .document(personLink)
                .collection("n")
                .add(notification)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.i("notification", "added successfully");
                        }else{
                            Log.i("notification", "adding failed");
                        }
                    }
                });
    }

    public static void checkUpdateMust(final Context context){
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("version_name", versionName);
        final String[] versions = versionName.split("\\.");
        Log.i("version_major", versions[0]);
        Log.i("version_minor", versions[1]);
        Log.i("version_fix", versions[2]);
        db.collection("public").document("mv")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Long majorMinimum = documentSnapshot.getLong("major");
                            if(Long.valueOf(versions[0])<majorMinimum){
                                startUpdateMustActivity(context, SourceUpdateMustPage.UPDATE_MUST);
                                return;
                            }
                            Long minorMinimum = documentSnapshot.getLong("minor");
                            if(Long.valueOf(versions[1])<minorMinimum){
                                startUpdateMustActivity(context, SourceUpdateMustPage.UPDATE_MUST);
                                return;
                            }
                            Long fixMinimum = documentSnapshot.getLong("fix");
                            if(Long.valueOf(versions[2])<fixMinimum){
                                startUpdateMustActivity(context, SourceUpdateMustPage.UPDATE_MUST);
                            }
                        }
                    }
                });
    }

    private static void checkUpdateOptional(final Context context){
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("version_name", versionName);
        final String[] versions = versionName.split("\\.");
        db.collection("public").document("cv")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Long majorCurrent = documentSnapshot.getLong("major");
                            if(Long.valueOf(versions[0])<majorCurrent){
                                showUpdateDialog(context);
                                return;
                            }
                            Long minorCurrent = documentSnapshot.getLong("minor");
                            if(Long.valueOf(versions[1])<minorCurrent){
                                showUpdateDialog(context);
                                return;
                            }
                            Long fixCurrent = documentSnapshot.getLong("fix");
                            if(Long.valueOf(versions[2])<fixCurrent){
                                showUpdateDialog(context);
                            }
                        }
                    }
                });
    }

    public static void checkMaintenanceBreak(final Context context){
        db.collection("public").document("mb")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            boolean isMaintenanceBreak = documentSnapshot.getBoolean("a");
                            if(isMaintenanceBreak){
                                startUpdateMustActivity(context, SourceUpdateMustPage.MAINTENANCE_BREAK);
                            }
                        }
                    }
                });
    }

    public static void shouldCheckUpdateOptional(Context context){
        SharedPreferences sPref = context.getSharedPreferences(
                context.getString(R.string.misc_spref),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        long lastTime = sPref.getLong("update_last_checked", 0L);
        Date now = new Date();
        long diffInMillis = Math.abs(now.getTime() - lastTime);
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        // check update availability once every 2 days
        if(diffInDays>=2){
            editor.putLong("update_last_checked", now.getTime());
            editor.apply();
            checkUpdateOptional(context);
        }
    }

    private static void startUpdateMustActivity(Context context, int source){
        Intent intent = new Intent(context, UpdateMust.class);
        intent.putExtra("entry_point", source);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void showUpdateDialog(Context context){
        String message = "An update is available in the playstore.";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }
}
