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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoggedInActivity extends AppCompatActivity {

    MaterialCardView logout, search, profile, about, reviews;
    TextView welcome;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String f_name, l_name;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.checkUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        this.welcome = findViewById(R.id.welcome_msg);

//        this.logout  = findViewById(R.id.logout);
        this.logout = findViewById(R.id.logout);
        this.search  = findViewById(R.id.search_store_card);
        this.about   = findViewById(R.id.about);
        this.reviews = findViewById(R.id.my_reviews);
        this.profile = findViewById(R.id.my_profile);

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.f_name = userData.getString("f_name");
            this.l_name = userData.getString("l_name");

            String temp = String.format(getResources().getString(R.string.welcome), f_name, l_name);
            welcome.setText(temp);
        }
        else {
            DocumentReference docRef = db.collection("Users").document(user.getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            f_name = document.getData().get("first_name").toString();
                            l_name = document.getData().get("last_name").toString();

                            Log.e("LoggedInActivity", f_name + " - " + l_name);

                            String temp = String.format(getResources().getString(R.string.welcome), f_name, l_name);
                            welcome.setText(temp);
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
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(LoggedInActivity.this, WelcomePageActivity.class));
            finish();
        }
    }

}
