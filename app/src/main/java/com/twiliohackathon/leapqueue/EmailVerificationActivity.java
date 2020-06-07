package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.sql.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class EmailVerificationActivity extends AppCompatActivity {

    private static final String TAG = "EmailVerificationActivity";
    FirebaseAuth mfirebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        this.mfirebaseAuth = FirebaseAuth.getInstance();
        this.user = mfirebaseAuth.getCurrentUser();

        final Bundle userData = getIntent().getExtras();
        if (userData == null) {
            System.exit(-1);
        }

        findViewById(R.id.resend_ver_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(mfirebaseAuth.getCurrentUser()).reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user = mfirebaseAuth.getCurrentUser();
                    }
                });

                if (user.isEmailVerified()) {
                    createAlert("Account Already Verified","Your email is already verified. Close this message and Click \"Continue\"");
                } else {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EmailVerificationActivity.this, "Email verification link has been re-sent", Toast.LENGTH_SHORT).show();
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

        findViewById(R.id.continue_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(mfirebaseAuth.getCurrentUser()).reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user = mfirebaseAuth.getCurrentUser();
                    }
                });
                if (!user.isEmailVerified()) {
                    createAlert("Account Not Verified",
                            "Your email has not been verified. Close this message, click \"Resend Verification Email\", and click on the link sent to the provided email address"
                    );
                } else {
                    createDocument(userData.getString("f_name"), userData.getString("l_name"));

                    Intent transfer = new Intent(EmailVerificationActivity.this, LoggedInActivity.class);
                    transfer.putExtra("f_name", userData.getString("f_name"));
                    transfer.putExtra("l_name", userData.getString("l_name"));

                    startActivity(transfer);
                    finish();
                }
            }
        });
  }

    public void createDocument(String firstName, String lastName) {
        Map<String, Object> document = new HashMap<>();
        document.put("first_name", firstName);
        document.put("last_name", lastName);
        document.put("joined_on", new Timestamp(System.currentTimeMillis()));

        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(user.getEmail()))
                .set(document)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully added to the Firestore");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "User could not be added to the Firestore" + e.getMessage());
                    }
                });
    }

    public void createAlert(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(EmailVerificationActivity.this);

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
