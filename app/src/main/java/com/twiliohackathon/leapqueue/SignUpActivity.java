package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("ConstantConditions")
public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    public TextInputLayout emailId, password, conf_password, fName, lName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.emailId = findViewById(R.id.edit_email);
        this.password = findViewById(R.id.edit_password);
        this.conf_password = findViewById(R.id.confirm_password);
        this.fName = findViewById(R.id.edit_first_name);
        this.lName = findViewById(R.id.edit_last_name);

        findViewById(R.id.submit_signup_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getEditText().getText().toString(),
                        pwd = password.getEditText().getText().toString(),
                        conf = conf_password.getEditText().getText().toString();

                final String first = fName.getEditText().getText().toString(),
                        last = lName.getEditText().getText().toString();

                if (email.isEmpty() && pwd.isEmpty()) {
                    emailId.setError("An email is required");
                    password.setError("A password is required");
                    emailId.requestFocus();
                    password.requestFocus();
                    conf_password.requestFocus();
                }  else if (email.isEmpty()) {
                    emailId.setError("An email is required");
                    emailId.requestFocus();
                }  else if (pwd.isEmpty()) {
                    password.setError("A password is required");
                    password.requestFocus();
                    conf_password.requestFocus();
                }  else if (pwd.compareTo(conf) != 0) {
                    conf_password.setError("Password must match above field");
                    conf_password.requestFocus();
                }  else if (first.isEmpty() && last.isEmpty()) {
                    fName.setError("You need to specify a first name");
                    fName.requestFocus();
                    lName.setError("You need to specify a last name");
                    lName.requestFocus();
                }  else if (first.isEmpty()) {
                    fName.setError("You need to specify a first name");
                    fName.requestFocus();
                }  else if (last.isEmpty()) {
                    lName.setError("You need to specify a last name");
                    lName.requestFocus();
                }  else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Sign-up failed, please retry.", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "Check email for verification link.", Toast.LENGTH_SHORT).show();

                                        Intent transfer = new Intent(SignUpActivity.this, EmailVerificationActivity.class);
                                        transfer.putExtra("f_name", first);
                                        transfer.putExtra("l_name", last);
                                        startActivity(transfer);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Email not sent: " + e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}
