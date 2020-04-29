package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class SelectedStoreActivity extends AppCompatActivity {

    Store store;
    ImageView titleImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_store);

        this.store = new Store();

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.store.storeName = userData.getString("name");
            this.store.type = userData.getString("type");
            this.store.website = userData.getString("website");
            this.store.address = userData.getString("address");
            this.store.postalCode = userData.getString("postal");
        }
        else {
            Log.e("SelectedStoreActivity", "Failedddd");
        }

        this.titleImg = findViewById(R.id.store_icon);

        if (this.store.type.equals("Hotel".toLowerCase())) {
            this.titleImg.setImageResource(R.drawable.hotel);
        }
        else if (this.store.type.equals("Restaurant".toLowerCase())) {
            this.titleImg.setImageResource(R.drawable.restaurant);
        }
        else {
            this.titleImg.setImageResource(R.drawable.trolley);
        }
    }
}
