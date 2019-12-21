package myapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.tuhin.myapplication.R;
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

import androidx.annotation.NonNull;

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
}
