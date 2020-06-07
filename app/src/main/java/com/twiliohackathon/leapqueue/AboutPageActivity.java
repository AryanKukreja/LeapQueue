package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.io.InputStream;

public class AboutPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        findViewById(R.id.termsAndConditionsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String termsAndConditions = "";
                try {
                    InputStream readData = getAssets().open("TermsOfService.txt");
                    int size = readData.available();

                    byte[] buffer = new byte[size];
                    readData.read(buffer);
                    termsAndConditions = new String(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                createAlert(
                        "Terms and Conditions of Use",
                        termsAndConditions
                );
            }
        });

        findViewById(R.id.feedbackLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                link.setData(Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSfSI_ywFeQfeawtIP8ER4B_psvAwufreYFhAM1xFZyTCvmHIg/viewform"));
                startActivity(link);
            }
        });

        findViewById(R.id.githubLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                link.setData(Uri.parse("https://github.com/AryanKukreja/LeapQueue/"));
                startActivity(link);
            }
        });

        findViewById(R.id.goHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutPageActivity.this, LoggedInActivity.class));
            }
        });
    }

    public void createAlert(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(AboutPageActivity.this);

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
