package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.AccountTypes;
import myapp.utils.OrphanUtilityMethods;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            chooseAndLaunchHome(user);
//            if(user.isEmailVerified()){
//                Log.i("email", "verified");
//                chooseAndLaunchHome(user);
//            }else{
//                Log.i("email", "not verified");
//                sendEmailVerification(user, user.getEmail());
//            }
        }
    }

    private void chooseAndLaunchHome(FirebaseUser user){
        int accountType = OrphanUtilityMethods.getAccountType(this);
        if(accountType == AccountTypes.UNSET){
            getAccountTypeFromDB(user);
        }else if(accountType == AccountTypes.PERSON){
            chooseHomeOrProfileCreation(user, AccountTypes.PERSON);
        }else{
            chooseHomeOrProfileCreation(user, AccountTypes.RESTAURANT);
        }
    }

    private void getAccountTypeFromDB(final FirebaseUser user){
        FirebaseFirestore.getInstance()
                .collection("email_type")
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i("account_type_SP", "being added");
                        SharedPreferences sPref = getSharedPreferences(getString(R.string.account_type),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sPref.edit();
                        if(documentSnapshot.exists()){
                            Boolean forPerson = documentSnapshot.getBoolean("forPerson");
                            Intent intent;
                            if(forPerson){
                                editor.putInt(user.getEmail(), AccountTypes.PERSON);
                                editor.apply();
                                chooseHomeOrProfileCreation(user, AccountTypes.PERSON);
                            }else{
                                editor.putInt(user.getEmail(), AccountTypes.RESTAURANT);
                                editor.apply();
                                chooseHomeOrProfileCreation(user, AccountTypes.RESTAURANT);
                            }
                        }
                    }
                });
    }

    private void sendEmailVerification(final FirebaseUser user, final String emailString){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SplashScreen.this, EmailVerification.class);
                        intent.putExtra("email", emailString);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }
                });
    }

    private void chooseHomeOrProfileCreation(FirebaseUser user, final int type){
        String uid = user.getUid();
        FirebaseFirestore.getInstance().collection("profile_created")
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            boolean isProfileCreated = documentSnapshot.getBoolean("a");
                            if(isProfileCreated){
                                Intent intent;
                                if(type==AccountTypes.PERSON){
                                    intent = new Intent(SplashScreen.this, home.class);
                                }else{
                                    intent = new Intent(SplashScreen.this, RestaurantHome.class);
                                }
                                startActivity(intent);
                                SplashScreen.this.finish();
                            }else{
                                Intent intent;
                                if(type==AccountTypes.PERSON){
                                    intent = new Intent(SplashScreen.this, SetProfilePicture.class);
                                }else{
                                    intent = new Intent(SplashScreen.this, RestAccountSetup.class);
                                }
                                startActivity(intent);
                                SplashScreen.this.finish();
                            }
                        }else{
                            Intent intent;
                            if(type==AccountTypes.PERSON){
                                intent = new Intent(SplashScreen.this, SetProfilePicture.class);
                            }else{
                                intent = new Intent(SplashScreen.this, RestAccountSetup.class);
                            }
                            startActivity(intent);
                            SplashScreen.this.finish();
                        }
                    }
                });
    }
}
