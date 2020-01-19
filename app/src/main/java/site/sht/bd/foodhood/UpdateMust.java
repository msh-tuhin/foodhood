package site.sht.bd.foodhood;

import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.SourceUpdateMustPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

public class UpdateMust extends AppCompatActivity {

    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int entryPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_must);

        textView = findViewById(R.id.textView);

        entryPoint = getIntent().getIntExtra("entry_point", SourceUpdateMustPage.UPDATE_MUST);
        if(entryPoint==SourceUpdateMustPage.MAINTENANCE_BREAK){
            textView.setText("Maintenance break! Sorry for the inconveniance.");
        }
    }

    @Override
    protected void onStart() {
        if(entryPoint==SourceUpdateMustPage.UPDATE_MUST){
            checkUpdateMust(this);
        }else {
            checkMaintenanceBreak(this);
        }
        super.onStart();
    }

    private void checkUpdateMust(final Context context){
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("version_name", versionName);
        final String[] versions = versionName.split("\\.");
        Log.i("version_major", versions[0]);
        Log.i("version_minor", versions[1]);
        Log.i("version_fix", versions[2]);
        db.collection("public").document("mv")
                .get()
                .addOnSuccessListener(UpdateMust.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Long majorMinimum = documentSnapshot.getLong("major");
                            if(Long.valueOf(versions[0])<majorMinimum){
                                return;
                            }
                            Long minorMinimum = documentSnapshot.getLong("minor");
                            if(Long.valueOf(versions[1])<minorMinimum){
                                return;
                            }
                            Long fixMinimum = documentSnapshot.getLong("fix");
                            if(Long.valueOf(versions[2])<fixMinimum){
                                return;
                            }
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
    }

    private void checkMaintenanceBreak(final Context context){
        db.collection("public").document("mb")
                .get()
                .addOnSuccessListener(UpdateMust.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            boolean isMaintenanceBreak = documentSnapshot.getBoolean("a");
                            if(!isMaintenanceBreak){
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    }
                });
    }
}
