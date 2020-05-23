package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions", "deprecation"})
public class SelectedStoreActivity extends AppCompatActivity {

    Store store;
    ImageView titleImg;
    boolean userRevPresent;
    MaterialButton refresh_button;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_store);

        this.userRevPresent = false;
        this.refresh_button = findViewById(R.id.refresh);
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        Intent temp = new Intent(SelectedStoreActivity.this, SelectedStoreActivity.class);
        temp.putExtras(getIntent().getExtras());
        final Intent refresh = temp;

        final Bundle userData = getIntent().getExtras();
        if (userData != null) {
            this.store = new Store(userData.getString("name"),
                    userData.getString("type"),
                    userData.getString("website"),
                    userData.getString("address"),
                    userData.getString("postal"));
        } else {
            System.exit(-1);
        }

        this.titleImg = findViewById(R.id.store_icon);
        if (this.store.type.equals("Hotel")) {
            this.titleImg.setImageResource(R.drawable.hotel);
        } else if (this.store.type.equals("Restaurant")) {
            this.titleImg.setImageResource(R.drawable.restaurant);
        } else {
            this.titleImg.setImageResource(R.drawable.trolley);
        }

        ((TextView) findViewById(R.id.name)).setText(this.store.storeName);
        ((TextView) findViewById(R.id.address)).setText(this.store.address);
        findViewById(R.id.web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(store.website)));
            }
        });

        FirebaseFirestore.getInstance().collection("Reviews").whereEqualTo("postal", Integer.parseInt(this.store.postalCode)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addReview(document.getData(), document);
                            }

                            if (!userRevPresent) {
                                findViewById(R.id.add_edit_delete).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent transfer = new Intent(SelectedStoreActivity.this, UserReviewActivity.class);
                                        transfer.putExtra("reviewPresent", false);
                                        transfer.putExtras(userData);
//                                        transfer.putExtra("name", store.storeName);
//                                        transfer.putExtra("addr", store.address);
                                        transfer.putExtra("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//                                        transfer.putExtra("post", store.postalCode);
                                        startActivity(transfer);
                                    }
                                });
                            }
                            findViewById(R.id.add_edit_delete).setVisibility(View.VISIBLE);
                        } else {
                            Log.e("SelectedStoreActivity", "Error getting documents: " + task.getException());
                        }
                    }
                });

        this.refresh_button.setOnClickListener(new View.OnClickListener() {
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
            review.dateDate = document.getTimestamp("date").toDate();
            Calendar queueDateCal = Calendar.getInstance();
            queueDateCal.setTime(review.dateDate);

            review.date = queueDateCal.get(Calendar.DAY_OF_MONTH) + "/" + (queueDateCal.get(Calendar.MONTH) + 1) + "/" + queueDateCal.get(Calendar.YEAR);

            if (queueDateCal.get(Calendar.HOUR) > 12) {
                review.hour = queueDateCal.get(Calendar.HOUR) - 12;
                review.am = false;
            }
            else {
                review.hour = queueDateCal.get(Calendar.HOUR);
                review.am = true;
            }
            review.minute = queueDateCal.get(Calendar.MINUTE);
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
        LinearLayout queueTime = createLayout(false),
                itemAvail = createLayout(false),
                staffEff  = createLayout(false),
                reviewCard = findViewById(R.id.reviews_container);

        LinearLayout.LayoutParams wrap_content_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        wrap_content_params.setMargins(0, 0, 0, 60);

        TextView dateVisit = createText("Date Visited: " + "  " + review.date, Typeface.BOLD);
        dateVisit.setLayoutParams(wrap_content_params);

        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 5));
        v.setBackgroundColor(Color.parseColor("#B3B3B3"));

        queueTime.addView(createText(getResources().getString(R.string.queue_time) + "   ", Typeface.BOLD));
        queueTime.addView(setTime(createText(review.queueTime + " minutes", Typeface.BOLD), review.getQueueTime()));
        itemAvail.addView(createText(getResources().getString(R.string.item_avail) + "  ", Typeface.BOLD));
        itemAvail.addView(createRatingBar(review.itemAvail.floatValue(), true));
        staffEff.addView(createText(getResources().getString(R.string.staff_eff) + "   ", Typeface.BOLD));
        staffEff.addView(createRatingBar(review.staffEff.floatValue(), true));

        reviewCard.addView(createText("\"" + review.comment + "\"",Typeface.ITALIC));
        reviewCard.addView(queueTime);
        reviewCard.addView(itemAvail);
        reviewCard.addView(staffEff);
        reviewCard.addView(dateVisit);
        reviewCard.addView(v);
    }

    @SuppressLint("SetTextI18n")
    public void addUserReview(final Review review) {
        LinearLayout queueTime = createLayout(false),
                itemAvail = createLayout(false),
                staffEff  = createLayout(false),
                userCard = findViewById(R.id.user_box);

        ((TextView) findViewById(R.id.my_review_header)).setText(R.string.this_reviews);
        ((TextView) findViewById(R.id.comment_section)).setText("\"" + review.comment + "\"");
        ((MaterialButton) findViewById(R.id.add_edit_delete)).setText(R.string.edit_reviews);

        final int queue = review.getQueueTime();
        final float eff = review.staffEff.floatValue(),
                itm = review.itemAvail.floatValue();
        final String cmnt = review.comment,
                email = user.getEmail(),
                name = store.storeName,
                addr = store.address,
                post = store.postalCode;

        findViewById(R.id.add_edit_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer = new Intent(getBaseContext(), UserReviewActivity.class);
                transfer.putExtra("qtm", queue);
                transfer.putExtra("eff", eff);
                transfer.putExtra("itm", itm);
                transfer.putExtra("com", cmnt);
                transfer.putExtra("name", name);
                transfer.putExtra("address", addr);
                transfer.putExtra("date", review.dateDate);
                transfer.putExtra("datee", review.dateDate.toString());
                transfer.putExtra("reviewPresent", true);
                transfer.putExtra("email", email);
                transfer.putExtra("postal", post);
                transfer.putExtra("hr", review.hour);
                transfer.putExtra("min", review.minute);
                transfer.putExtra("am", review.am);

                startActivity(transfer);
            }
        });

        queueTime.addView(createText(getResources().getString(R.string.queue_time) + "   ", Typeface.BOLD));
        queueTime.addView(setTime(createText(review.queueTime + " minutes", Typeface.BOLD), review.getQueueTime()));
        itemAvail.addView(createText(getResources().getString(R.string.item_avail) + "  ", Typeface.BOLD));
        itemAvail.addView(createRatingBar(review.itemAvail.floatValue(), true));
        staffEff.addView(createText(getResources().getString(R.string.staff_eff) + "   ", Typeface.BOLD));
        staffEff.addView(createRatingBar(review.staffEff.floatValue(), true));

        userCard.addView(createText("Date Visited: " + "  " + review.date, Typeface.BOLD));
        userCard.addView(queueTime);
        userCard.addView(itemAvail);
        userCard.addView(staffEff);
    }

    public TextView setTime(TextView queueVal, int time) {
        if (time < 5) {
            queueVal.setTextColor(getResources().getColor(R.color.green));
        } else if (time < 20) {
            queueVal.setTextColor(getResources().getColor(R.color.yellow));
        } else if (time < 45) {
            queueVal.setTextColor(getResources().getColor(R.color.orange));
        } else queueVal.setTextColor(getResources().getColor(R.color.red));

        return queueVal;
    }

    public LinearLayout createLayout(boolean vertical) {
        LinearLayout layout = new LinearLayout(SelectedStoreActivity.this);
        layout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        return layout;
    }

    public TextView createText(String text, int typeFace) {
        TextView view = new TextView(SelectedStoreActivity.this);
        view.setText(text);
        view.setTypeface(null, typeFace);
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