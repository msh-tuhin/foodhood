package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestAccountSetup extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_account_setup);

        addNameAndEmailToDB(mAuth.getCurrentUser());

        Map<String, Object> map = new HashMap<>();
        map.put("a", new ArrayList<String>());
        final String currentUserId = mAuth.getCurrentUser().getUid();

        WriteBatch batch = db.batch();
        batch.set(db.collection("dishes").document(currentUserId), map,
                SetOptions.merge());
        batch.set(db.collection("followers").document(currentUserId), map,
                SetOptions.merge());
        batch.set(db.collection("feedbacks_list").document(currentUserId), map,
                SetOptions.merge());
        batch.set(db.collection("own_activities").document(currentUserId),
                new HashMap<>(), SetOptions.merge());
        batch.set(db.collection("notifications").document(currentUserId),
                new HashMap<>(), SetOptions.merge());

        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> confirmationMap = new HashMap<>();
                        if(task.isSuccessful()){
                            confirmationMap.put("a", true);
                        }else{
                            confirmationMap.put("a", false);
                        }
                        db.collection("profile_created").document(currentUserId)
                                .set(confirmationMap);
                        Intent intent = new Intent(RestAccountSetup.this, RestaurantHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

    private void addNameAndEmailToDB(FirebaseUser user){
        if(user==null) return;

        String name = user.getDisplayName();
        String emailString = user.getEmail();

        Map<String, String> restVital = new HashMap<>();
        if(name!=null){
            restVital.put("n", name);
        }
        if(emailString!=null){
            restVital.put("e", emailString);
        }

        FirebaseFirestore.getInstance()
                .collection("rest_vital").document(user.getUid())
                .set(restVital, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
