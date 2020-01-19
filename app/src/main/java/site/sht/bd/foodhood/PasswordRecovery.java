package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import site.sht.bd.foodhood.R;

public class PasswordRecovery extends AppCompatActivity {

    private final String TAG = "recovery_email";

    TextView errorMessageTextView;
    EditText emailEditText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.INVISIBLE);

        emailEditText = findViewById(R.id.email);
        sendButton = findViewById(R.id.recovery_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "sent");
                                Intent intent = new Intent(PasswordRecovery.this, SignInAfterPasswordRecovery.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("instance", e.getClass().toString());
                                Log.i(TAG, e.getMessage());
                                String message = "";
                                if(e instanceof FirebaseAuthInvalidCredentialsException){
                                    message = e.getMessage();
                                }else{
                                    // FirebaseAuthInvalidUserException
                                    message = "No account with email " + email + " exists";
                                }
                                errorMessageTextView.setText(message);
                                errorMessageTextView.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

    }
}
