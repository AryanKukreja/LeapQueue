package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions", "deprecation"})
public class SelectedStoreActivity extends AppCompatActivity {

    Store store;
    ImageView titleImg;
    ViewGroup.LayoutParams params;
    boolean userRevPresent;
    MaterialButton refresh;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_store);

        this.userRevPresent = false;
        this.params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.refresh = findViewById(R.id.refresh);

        this.store = new Store();
        this.db = FirebaseFirestore.getInstance();
        this.fAuth = FirebaseAuth.getInstance();
        this.user = fAuth.getCurrentUser();

        Intent intent = getIntent();
        final Intent refresh = new Intent(SelectedStoreActivity.this, SelectedStoreActivity.class);
        refresh.putExtras(intent.getExtras());

        Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.store.storeName = userData.getString("name");
            this.store.type = userData.getString("type");
            this.store.website = userData.getString("website");
            this.store.address = userData.getString("address");
            this.store.postalCode = userData.getString("postal");
        }
        else {
            Log.e("SelectedStoreActivity", "Failed for some reason :(");
        }

        // Set store title card
        this.titleImg = findViewById(R.id.store_icon);
        if (this.store.type.equals("Hotel")) {
            this.titleImg.setImageResource(R.drawable.hotel);
        }
        else if (this.store.type.equals("Restaurant")) {
            this.titleImg.setImageResource(R.drawable.restaurant);
        }
        else {
            this.titleImg.setImageResource(R.drawable.trolley);
        }

        TextView name = findViewById(R.id.name);
        TextView addr = findViewById(R.id.address);
        MaterialButton webButton = findViewById(R.id.web);

        name.setText(this.store.storeName);
        addr.setText(this.store.address);
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webPage = Uri.parse(store.website);
                Intent browser = new Intent(Intent.ACTION_VIEW, webPage);
                startActivity(browser);
            }
        });

        db.collection("Reviews").whereEqualTo("postal", Integer.parseInt(this.store.postalCode)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addReview(document.getData(), document);
                            }

                            MaterialButton button = findViewById(R.id.add_edit_delete);

                            if (!userRevPresent) {
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Intent transfer = new Intent(SelectedStoreActivity.this, UserReviewActivity.class);
                                        transfer.putExtra("reviewPresent", false);
                                        transfer.putExtra("name", store.storeName);
                                        transfer.putExtra("addr", store.address);
                                        transfer.putExtra("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        transfer.putExtra("post", store.postalCode);
                                        startActivity(transfer);
                                    }
                                });
                            }

                            button.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("SelectedStoreActivity", "Error getting documents: " + task.getException());
                        }
                    }
                });

        this.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(refresh);
                finish();
            }
        });

    }

    public void addReview(Map<String, Object> hash, DocumentSnapshot document) {
        Review review = new Review();

        if (hash.get("comment") != null) {
            review.setComment(Objects.requireNonNull(hash.get("comment")).toString());
        } else { review.setComment("No comment"); }

        if (hash.get("item_availability") != null) {
            review.setItemAvail(Double.parseDouble(hash.get("item_availability").toString()));
        }

        if (hash.get("queue_time") != null) {
            review.setQueueTime(Integer.parseInt(hash.get("queue_time").toString()));
        }

        if (hash.get("staff_efficiency") != null) {
            review.setStaffEff(Double.parseDouble(hash.get("staff_efficiency").toString()));
        }

        if (hash.get("date") != null) {
            Timestamp stamp = document.getTimestamp("date");
            Date date = stamp.toDate();
            Calendar queueDateCal = Calendar.getInstance();
            queueDateCal.setTime(date);

            review.date = queueDateCal.get(Calendar.DAY_OF_MONTH) + "/" + (queueDateCal.get(Calendar.MONTH) + 1) + "/" + queueDateCal.get(Calendar.YEAR);
            review.dateDate = date;
        }

        if (hash.get("email").toString().equals(user.getEmail())) {
            addUserReview(review);
            this.userRevPresent = true;
        }
        else {
            putReviewIn(review);
        }

    }

    public void putReviewIn(Review review) {
        LinearLayout reviewCard = findViewById(R.id.reviews_container);

        LinearLayout queueTime = createLayout(false);
        LinearLayout itemAvail = createLayout(false);
        LinearLayout staffEff  = createLayout(false);

        TextView queueText = createText(getResources().getString(R.string.queue_time) + "   ");
        queueText.setBottom(10);
        TextView itemText  = createText(getResources().getString(R.string.item_avail) + "  ");
        itemText.setBottom(10);
        TextView staffText = createText(getResources().getString(R.string.staff_eff) + "   ");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 60);

        TextView comments = createText("\"" + review.comment + "\"");
        comments.setTypeface(null, Typeface.ITALIC);
        comments.setTextColor(getResources().getColor(R.color.grey));

        TextView dateVisit = createText("Date Visited: " + "  " + review.date);
        dateVisit.setLayoutParams(params);

        TextView queueVal = createText(review.queueTime + " minutes");
        queueVal.setTypeface(null, Typeface.BOLD);
        int time = review.getQueueTime();
        if (time < 5) {
            queueVal.setTextColor(getResources().getColor(R.color.green));
        }
        else if (time > 5 && time < 20) {
            queueVal.setTextColor(getResources().getColor(R.color.yellow));
        }
        else if (time > 20 && time < 45) {
            queueVal.setTextColor(getResources().getColor(R.color.orange));
        }
        else {
            queueVal.setTextColor(getResources().getColor(R.color.red));
        }

        RatingBar item = createRatingBar(review.itemAvail.floatValue(), true);
        RatingBar staff = createRatingBar(review.staffEff.floatValue(), true);

        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                5
        ));
        v.setBackgroundColor(Color.parseColor("#B3B3B3"));


        queueTime.addView(queueText);
        queueTime.addView(queueVal);
        itemAvail.addView(itemText);
        itemAvail.addView(item);
        staffEff.addView(staffText);
        staffEff.addView(staff);

        reviewCard.addView(comments);
        reviewCard.addView(queueTime);
        reviewCard.addView(itemAvail);
        reviewCard.addView(staffEff);
        reviewCard.addView(dateVisit);
        reviewCard.addView(v);
    }

    public void addUserReview(final Review review) {
        final LinearLayout userCard = findViewById(R.id.user_box);

        LinearLayout queueTime = createLayout(false);
        LinearLayout itemAvail = createLayout(false);
        LinearLayout staffEff  = createLayout(false);

        TextView queueText = createText(getResources().getString(R.string.queue_time) + "   ");
        TextView itemText  = createText(getResources().getString(R.string.item_avail) + "  ");
        TextView staffText = createText(getResources().getString(R.string.staff_eff) + "   ");

        TextView comments = findViewById(R.id.comment_section);
        String comment = "\"" + review.comment + "\"";
        comments.setText(comment);

        TextView queueVal = createText(review.queueTime + " minutes");
        queueVal.setTypeface(null, Typeface.BOLD);
        int time = review.getQueueTime();
        if (time < 5) {
            queueVal.setTextColor(getResources().getColor(R.color.green));
        }
        else if (time > 5 && time < 20) {
            queueVal.setTextColor(getResources().getColor(R.color.yellow));
        }
        else if (time > 20 && time < 45) {
            queueVal.setTextColor(getResources().getColor(R.color.orange));
        }
        else {
            queueVal.setTextColor(getResources().getColor(R.color.red));
        }

        RatingBar item = createRatingBar(review.itemAvail.floatValue(), true);
        RatingBar staff = createRatingBar(review.staffEff.floatValue(), true);

        MaterialButton button = findViewById(R.id.add_edit_delete);
        button.setText(R.string.edit_reviews);

        final int queue = review.getQueueTime();
        final float eff = review.staffEff.floatValue();
        final float itm = review.itemAvail.floatValue();
        final String cmnt = review.comment, email = user.getEmail();
        final String name = store.storeName, addr = store.address, post = store.postalCode;


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer = new Intent(getBaseContext(), UserReviewActivity.class);

                transfer.putExtra("qtm", queue);
                transfer.putExtra("eff", eff);
                transfer.putExtra("itm", itm);
                transfer.putExtra("com", cmnt);
                transfer.putExtra("name", name);
                transfer.putExtra("addr", addr);
                transfer.putExtra("date", review.dateDate);
                transfer.putExtra("datee", review.dateDate.toString());
                transfer.putExtra("reviewPresent", true);
                transfer.putExtra("email", email);
                transfer.putExtra("post", post);

                startActivity(transfer);
            }
        });

        TextView heading = findViewById(R.id.my_review_header);
        heading.setText(R.string.this_reviews);

        TextView dateVisit = createText("Date Visited: " + "  " + review.date);

        queueTime.addView(queueText);
        queueTime.addView(queueVal);
        itemAvail.addView(itemText);
        itemAvail.addView(item);
        staffEff.addView(staffText);
        staffEff.addView(staff);


        userCard.addView(dateVisit);
        userCard.addView(queueTime);
        userCard.addView(itemAvail);
        userCard.addView(staffEff);
    }

    public LinearLayout createLayout(boolean vertical) {
        LinearLayout layout = new LinearLayout(SelectedStoreActivity.this);
        layout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

        return layout;
    }

    public TextView createText(String text) {
        TextView view = new TextView(SelectedStoreActivity.this);

        view.setText(text);
        view.setTypeface(null, Typeface.BOLD);
        view.setTextAppearance(SelectedStoreActivity.this, R.style.TextAppearance_MaterialComponents_Body2);
        view.setPadding(0, 0, 0, 10);

        return view;
    }

    public RatingBar createRatingBar(float val, boolean ind) {
        RatingBar bar = new RatingBar(SelectedStoreActivity.this, null, android.R.attr.ratingBarStyleSmall);

        bar.setIsIndicator(ind);

        bar.setNumStars(5);
        bar.setRating(val);

        return bar;
    }
}
