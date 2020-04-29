package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchQueryActivity extends AppCompatActivity {

    Button search;
    TextInputLayout name, location;

    private RequestQueue mQueue;

    private String key = "AjqfThnMDUJgbjMLKkOmbp0F8W73H4VG1P-Njsjy5nbXMIH84whR55Jb1HrOdzSQ";
    String baseUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query);

        this.name = findViewById(R.id.store_name);
        this.location = findViewById(R.id.location);
        this.search = findViewById(R.id.search_stores);

        mQueue = Volley.newRequestQueue(this);

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = buildUrl(baseUrl, name.getEditText().getText().toString(), location.getEditText().getText().toString(), key);
                JsonParse(url);
            }
        });
    }

    private void JsonParse(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Store> stores = new ArrayList<>();

                        try {
                            JSONArray resourceSets = response.getJSONArray("resourceSets");
                            JSONObject set = resourceSets.getJSONObject(0);
                            JSONArray resources = set.getJSONArray("resources");

                            for (int i = 0; i < resources.length(); i++) {
                                Store tempStore = new Store();

                                JSONObject store = resources.getJSONObject(i);
                                tempStore.setStoreName(store.getString("name"));
                                tempStore.setWebsite(store.getString("Website"));
                                tempStore.setType(store.getString("entityType"));

                                JSONObject address = store.getJSONObject("Address");
                                tempStore.setAddress(address.getString("formattedAddress"));
                                tempStore.setPostalCode(address.getString("postalCode"));

                                stores.add(tempStore);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (stores.size() > 0) {
                            Intent transfer = new Intent(SearchQueryActivity.this, ListResultsActivity.class);
                            transfer.putExtra("stores", stores);
                            startActivity(transfer);
                        }
                        else {
                            Toast.makeText(SearchQueryActivity.this, "No stores found. Try to refine your location specified (only mention the town-name", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
        });

        this.mQueue.add(request);
    }

    public String buildUrl(String baseUrl, String store, String town, String key) {
        String x = baseUrl + "?query=" + TextUtils.join("%20", store.split(" ")) + "%20" + TextUtils.join("%20", town.split(" ")) + "&key=" + key;
        System.out.println(x);
        return x;
    }
}
