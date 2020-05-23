package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

@SuppressWarnings("ConstantConditions")
public class SearchQueryActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private final String baseUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query);

        mQueue = Volley.newRequestQueue(this);

        findViewById(R.id.search_stores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonParse(buildUrl(baseUrl,
                    ((TextInputLayout) findViewById(R.id.store_name)).getEditText().getText().toString(),
                    ((TextInputLayout) findViewById(R.id.location)).getEditText().getText().toString(), getResources().getString(R.string.BING_MAPS_KEY)
                ));
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
                            JSONArray resources = response.getJSONArray("resourceSets")
                                    .getJSONObject(0)
                                    .getJSONArray("resources");

                            for (int i = 0; i < resources.length(); i++) {
                                JSONObject store = resources.getJSONObject(i);
                                JSONObject address = store.getJSONObject("Address");

                                stores.add(
                                        new Store(store.getString("name"),
                                                store.getString("entityType"),
                                                store.getString("Website"),
                                                address.getString("formattedAddress"),
                                                address.getString("postalCode"))
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (stores.size() > 0) {
                            Intent transfer = new Intent(SearchQueryActivity.this, ListResultsActivity.class);
                            transfer.putExtra("stores", stores);
                            startActivity(transfer);
                        } else {
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
        return baseUrl + "?query=" + TextUtils.join("%20", store.split(" ")) + "%20" + TextUtils.join("%20", town.split(" ")) + "&key=" + key;
    }
}
