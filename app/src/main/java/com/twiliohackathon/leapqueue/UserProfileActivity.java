package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;

@SuppressWarnings({"ConstantConditions", "unused"})
@SuppressLint("SetTextI18n")
public class UserProfileActivity extends AppCompatActivity {
    FirebaseUser user;
    TextView saveChangesButton, displayName, displayEmail;
    ImageView editIcon;
    TextInputLayout fNameEdit, lNameEdit;
    String oldUserName;
    FirebaseFirestore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.store = FirebaseFirestore.getInstance();

        this.getUserInfo();
        this.getUserReviewsInfo();

        this.saveChangesButton = findViewById(R.id.save_changes);
        this.editIcon = findViewById(R.id.edit_info);
        this.displayName = findViewById(R.id.user_name);
        this.displayEmail = findViewById(R.id.email_addr);
        this.fNameEdit = findViewById(R.id.fname_edit);
        this.lNameEdit = findViewById(R.id.lname_edit);

        this.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIcon.setVisibility(View.GONE);
                displayName.setVisibility(View.GONE);
                displayEmail.setVisibility(View.GONE);

                saveChangesButton.setVisibility(View.VISIBLE);
                fNameEdit.setVisibility(View.VISIBLE);
                lNameEdit.setVisibility(View.VISIBLE);

                oldUserName = displayName.getText().toString();
            }
        });

        this.saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayName.setText(fNameEdit.getEditText().getText() + " " + lNameEdit.getEditText().getText());

                setUserInfo(fNameEdit.getEditText().getText().toString(),
                        lNameEdit.getEditText().getText().toString());

                saveChangesButton.setVisibility(View.GONE);
                fNameEdit.setVisibility(View.GONE);
                lNameEdit.setVisibility(View.GONE);

                editIcon.setVisibility(View.VISIBLE);
                displayName.setVisibility(View.VISIBLE);
                displayEmail.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.go_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, LoggedInActivity.class));
                finish();
            }
        });

        findViewById(R.id.terminate_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                store.collection("Users").document(displayEmail.getText().toString()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                store.collection("Reviews").whereEqualTo("email", displayEmail.getText().toString()).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                                    store.collection("Reviews").document(doc.getId()).delete()
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            });
                                                                }
                                                                Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void setUserInfo(String fName, String lName) {
        if (!(fName + " " + lName).equals(oldUserName)) {
            store.collection("Users").document(user.getEmail())
                    .update("first_name", fName,
                            "last_name", lName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            createAlert("User Profile Updated", "Success: Your user profile has been updated");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void getUserInfo() {
        FirebaseFirestore.getInstance().collection("Users").document(user.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        ((TextView) findViewById(R.id.join_date)).setText("Joined on "
                         + new SimpleDateFormat("d MMM, yyyy").format(document.getTimestamp("joined_on").toDate()));

                        ((TextView) findViewById(R.id.user_name)).setText(document.getString("first_name") + " " + document.getString("last_name"));
                        ((TextView) findViewById(R.id.email_addr)).setText(user.getEmail());
                    }
                });
    }
    public void getUserReviewsInfo() {
        FirebaseFirestore.getInstance().collection("Reviews").whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryResults) {
                        double positive = 0, negative = 0;
                        for (QueryDocumentSnapshot doc : queryResults) {
                            negative += Double.parseDouble(doc.get("down_votes").toString());
                            positive += Double.parseDouble(doc.get("up_votes").toString());
                        }
                        ((TextView) findViewById(R.id.approval)).setText((int)(positive / (positive + negative) * 100) + "% Approval");
                        ((TextView) findViewById(R.id.num_reviews)).setText(queryResults.size() + (queryResults.size() == 1 ? " review" : " reviews"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void createAlert(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileActivity.this);

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
