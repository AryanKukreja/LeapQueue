package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    public TextInputLayout emailId, password, conf_password, fname, lname;
    Button btnSignUp;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.mFirebaseAuth = FirebaseAuth.getInstance();

        this.emailId = findViewById(R.id.edit_email);
        this.password = findViewById(R.id.edit_password);
        this.btnSignUp = findViewById(R.id.submit_signup_details);
        this.conf_password = findViewById(R.id.confirm_password);

        this.fname = findViewById(R.id.edit_first_name);
        this.lname = findViewById(R.id.edit_last_name);

        this.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getEditText().getText().toString();
                String pwd = password.getEditText().getText().toString();
                String conf = conf_password.getEditText().getText().toString();

                final String first = fname.getEditText().getText().toString();
                final String last = lname.getEditText().getText().toString();

                if (email.isEmpty() && pwd.isEmpty()) {
                    emailId.setError("An email is required");
                    password.setError("A password is required");
                    emailId.requestFocus();
                    password.requestFocus();
                    conf_password.requestFocus();
                }
                else if (email.isEmpty()) {
                    emailId.setError("An email is required");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("A password is required");
                    password.requestFocus();
                }
                else if (pwd.compareTo(conf) != 0) {
                    conf_password.setError("Password must match above field");
                    conf_password.requestFocus();
                }
                else if (first.isEmpty() && last.isEmpty()) {
                    fname.setError("You need to specify a first name");
                    fname.requestFocus();
                    lname.setError("You need to specify a last name");
                    lname.requestFocus();
                }
                else if (first.isEmpty()) {
                    fname.setError("You need to specify a first name");
                    fname.requestFocus();
                }
                else if (last.isEmpty()) {
                    lname.setError("You need to specify a last name");
                    lname.requestFocus();
                }
                else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Sign-up failed, please retry.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
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
