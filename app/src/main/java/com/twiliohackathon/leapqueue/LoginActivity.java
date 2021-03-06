package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AppCompatActivity {
    public TextInputLayout emailId, password;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.mFirebaseAuth = FirebaseAuth.getInstance();

        this.emailId = findViewById(R.id.edit_email);
        this.password = findViewById(R.id.edit_password);

        findViewById(R.id.submit_login_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getEditText().getText().toString();
                String pwd = password.getEditText().getText().toString();

                if (!email.isEmpty() && !pwd.isEmpty()) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Log-in failed, please retry.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (!mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                                    createAlert("Account Not Verified",
                                            "Your account has not been verified. Verify it and then click \"Continue\", or request a email re-send below");
                                    startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, LoggedInActivity.class));
                                }
                                finish();
                            }
                        }
                    });
                }
                else {
                    if (email.isEmpty()) {
                        emailId.setError("An email is required");
                        emailId.requestFocus();
                    }
                    if (pwd.isEmpty()) {
                        password.setError("A password is required");
                        password.requestFocus();
                    }
                }
            }
        });

        findViewById(R.id.forgot_pw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void createAlert(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

        alert.setMessage(msg);
        alert.setTitle(title);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.create().show();
    }
}