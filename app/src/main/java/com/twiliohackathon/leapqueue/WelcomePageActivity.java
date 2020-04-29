package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
    }

    public void goToLogin(View v) {
        startActivity(new Intent(WelcomePageActivity.this, LoginActivity.class));
    }

    public void goToSignUp(View v) {
        startActivity(new Intent(WelcomePageActivity.this, SignUpActivity.class));
    }
}
