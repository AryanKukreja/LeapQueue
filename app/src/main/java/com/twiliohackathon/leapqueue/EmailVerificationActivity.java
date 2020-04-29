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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EmailVerificationActivity extends AppCompatActivity {

    private static final String TAG = "EmailVerificationActivity";
    Button resend, cont;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser user;

    String fname, lname;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        this.resend = findViewById(R.id.resend_ver_email);
        this.cont = findViewById(R.id.continue_login);
        this.mfirebaseAuth = FirebaseAuth.getInstance();
        this.user = mfirebaseAuth.getCurrentUser();

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.fname = userData.getString("f_name");
            this.lname = userData.getString("l_name");
        }

        this.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(mfirebaseAuth.getCurrentUser()).reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user = mfirebaseAuth.getCurrentUser();
                    }
                });

                if (user.isEmailVerified()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(EmailVerificationActivity.this);

                    alert.setMessage("Your email is already verified. Close this message and Click \"Continue\"");
                    alert.setTitle("Account Already Verified");

                    alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                else {
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

        this.cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(mfirebaseAuth.getCurrentUser()).reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user = mfirebaseAuth.getCurrentUser();
                    }
                });
                if (!user.isEmailVerified()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(EmailVerificationActivity.this);

                    alert.setMessage("Your email has not been verified. Close this message, click \"Resend Verification Email\", and click on the link sent to the provided email address");
                    alert.setTitle("Account Not Verified");

                    alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                else {
                    createDocument();

                    Intent transfer = new Intent(EmailVerificationActivity.this, LoggedInActivity.class);
                    transfer.putExtra("f_name", fname);
                    transfer.putExtra("l_name", lname);
                    startActivity(transfer);

                    finish();
                }
            }
        });
  }

    public void createDocument() {
        Map<String, Object> document = new HashMap<>();
        document.put("first_name", fname);
        document.put("last_name", lname);

        this.mFirestore.collection("Users").document(Objects.requireNonNull(user.getEmail()))
                .set(document).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
