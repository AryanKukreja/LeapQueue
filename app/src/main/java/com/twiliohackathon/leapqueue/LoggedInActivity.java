package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressWarnings("ConstantConditions")
public class LoggedInActivity extends AppCompatActivity {
    MaterialCardView logout, search;
    TextView welcome;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.checkUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        this.welcome = findViewById(R.id.welcome_msg);
        this.logout = findViewById(R.id.logout);
        this.search  = findViewById(R.id.search_store_card);

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            welcome.setText(
                String.format(getResources().getString(R.string.welcome),
                    userData.getString("f_name"),
                    userData.getString("l_name")
                ));
        }
        else {
            db.collection("Users").document(user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                welcome.setText(
                                    String.format(getResources().getString(R.string.welcome),
                                        document.getData().get("first_name").toString(),
                                        document.getData().get("last_name").toString()
                                    ));
                            } else {
                                Log.d("LoggedInActivity", "No such document");
                            }
                        } else {
                            Log.d("LoggedInActivity", "get failed with ", task.getException());
                        }
                    }
                });
        }

        this.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LoggedInActivity.this, WelcomePageActivity.class));
            }
        });

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoggedInActivity.this, SearchQueryActivity.class));
            }
        });
    }

    protected void checkUser() {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(LoggedInActivity.this, WelcomePageActivity.class));
            finish();
        }
    }
}