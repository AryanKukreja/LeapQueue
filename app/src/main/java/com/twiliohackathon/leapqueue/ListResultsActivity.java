package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class ListResultsActivity extends AppCompatActivity {

    ArrayList<Store> stores;

    final static int TITLE = 0;
    final static int ADDRESS = 1;
    final static int TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_results);

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.stores = getIntent().getParcelableArrayListExtra("stores");
            if (this.stores.size() == 0) {
                Log.e("ListResultsActivity", "No stores found");
            }
        } else System.exit(-1);

        for (Store store : this.stores) this.addStoreToView(store);
    }

    public void addStoreToView(Store store) {
        TextView type = createTextView(store.type, TYPE),
                address = createTextView(store.address, ADDRESS),
                name = createTextView(store.storeName,TITLE);

        final String storeWebsite = store.website;
        final String storeName = store.storeName;
        final String storeType = store.type;
        final String storeAddress = store.address;
        final String storePostal = store.postalCode;

        MaterialButton linkButton = createButton("Visit Website");
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                link.setData(Uri.parse(storeWebsite));
                startActivity(link);
            }
        });

        MaterialButton chooseStoreButton = createButton("Pick Store");
        chooseStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer = new Intent(ListResultsActivity.this, SelectedStoreActivity.class);
                transfer.putExtra("name", storeName);
                transfer.putExtra("type", storeType);
                transfer.putExtra("website", storeWebsite);
                transfer.putExtra("address", storeAddress);
                transfer.putExtra("postal", storePostal);
                startActivity(transfer);
            }
        });

        LinearLayout shopData = createLinLay(10, true);
        shopData.addView(name);
        shopData.addView(type);
        shopData.addView(address);
        shopData.addView(chooseStoreButton);

        MaterialCardView shopCard = createCard();
        shopCard.addView(shopData);
        shopCard.setBottom(20);

        ((LinearLayout) findViewById(R.id.results_container)).addView(shopCard);
    }

    public MaterialButton createButton(String textField) {
        MaterialButton button = new MaterialButton(ListResultsActivity.this);
        button.setText(textField);
        return button;
    }

    public TextView createTextView(String text, int type) {
        TextView data = new TextView(getApplicationContext());

        data.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        data.setTypeface(ResourcesCompat.getFont(ListResultsActivity.this, R.font.roboto_black));
        data.setTextColor(getResources().getColor(R.color.black));
        data.setText(text);

        if (type == TITLE) {
            data.setTypeface(null, Typeface.BOLD);
            data.setTextAppearance(ListResultsActivity.this, R.style.TextAppearance_MaterialComponents_Headline5);
            data.setPadding(0, 0, 0, 10);
        }
        else if (type == ADDRESS) {
            data.setTextAppearance(ListResultsActivity.this, R.style.TextAppearance_MaterialComponents_Body2);
            data.setPadding(0, 0, 0, 20);
        }
        else if (type == TYPE) {
            data.setTextAppearance(ListResultsActivity.this, R.style.TextAppearance_MaterialComponents_Caption);
            data.setPadding(0, 0, 0, 10);
        }

        return data;
    }

    public MaterialCardView createCard() {
        MaterialCardView card = new MaterialCardView(ListResultsActivity.this);
        card.setLayoutParams(new MaterialCardView.LayoutParams(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.MATCH_PARENT));
        card.setContentPadding(20, 20, 20, 20);
        card.setCardElevation(6);
        card.setClickable(true);
        card.setUseCompatPadding(true);

        return card;
    }

    public LinearLayout createLinLay(int pad, boolean vertical) {
        LinearLayout layout = new LinearLayout(ListResultsActivity.this);

        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        layout.setPadding(pad, pad, pad, pad);

        return layout;
    }
}
