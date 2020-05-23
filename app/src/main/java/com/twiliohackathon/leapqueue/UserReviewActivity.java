package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.travijuu.numberpicker.library.NumberPicker;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserReviewActivity extends AppCompatActivity {

    RatingBar item, staff;
    TextInputEditText comments;
    TextView name, addr;
    NumberPicker queue, hour, minute;

    MaterialButton delete, submit;
    TextInputEditText date;
    Spinner time;
    Date dateVal;

    FirebaseFirestore store;
    String postal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        this.submit = findViewById(R.id.submit);
        this.name = findViewById(R.id.name);
        this.addr = findViewById(R.id.address);
        this.store = FirebaseFirestore.getInstance();
        this.delete = findViewById(R.id.delete);
        this.item = findViewById(R.id.item);
        this.queue = findViewById(R.id.queue);
        this.staff = findViewById(R.id.staff);
        this.comments = findViewById(R.id.comment_field);
        TextView title = findViewById(R.id.review_action);

        this.queue = findViewById(R.id.queue);
        queue.setMax(500);
        queue.setMin(0);
        queue.setUnit(4);
        queue.setValue(1);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_ranges, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time.setAdapter(adapter);

        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Yassssssssssssssssssssssssss", "Thi was chosen: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("Yassssssssssssssssssssssssss", "Nothing was chosen");
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                DatePickerDialog picker = new DatePickerDialog(UserReviewActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String output = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                date.setText(output);
                            }
                        }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH), cldr.get(Calendar.DAY_OF_MONTH));
                picker.show();
            }
        });

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.addr.setText(bundle.getString("addr"));
            this.name.setText(bundle.getString("name"));
            this.postal = bundle.getString("post");

            if (bundle.getBoolean("reviewPresent")) {
                String text = "Edit Your Review";
                title.setText(text);

                this.item.setRating(bundle.getFloat("itm"));
                this.queue.setValue(bundle.getInt("qtm"));
                this.staff.setRating(bundle.getFloat("eff"));
                this.comments.setText(bundle.getString("com"));
                this.dateVal = (Date) bundle.get("date");

                Calendar queueDateCal = Calendar.getInstance();
                queueDateCal.setTime(dateVal);
                String datee = queueDateCal.get(Calendar.DAY_OF_MONTH) + "/" + (queueDateCal.get(Calendar.MONTH) + 1) + "/" + queueDateCal.get(Calendar.YEAR);
                this.date.setText(datee);

                this.submit.setText(R.string.update);

                this.delete.setVisibility(View.VISIBLE);
                this.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        store.collection("Reviews")
                                .whereEqualTo("email", bundle.getString("email"))
                                .whereEqualTo("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("post"))))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot snapshot = task.getResult();

                                        assert snapshot != null;
                                        for (QueryDocumentSnapshot doc : snapshot) {
                                            store.collection("Reviews").document(doc.getId())
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(UserReviewActivity.this, "Your review has been deleted. Go back to the last page", Toast.LENGTH_LONG).show();
                                                            }
                                                            else {
                                                                Toast.makeText(UserReviewActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
            }
            else {
                this.delete.setVisibility(View.GONE);

                this.submit.setText(getResources().getString(R.string.sub));
                this.submit.setText(R.string.sub);
            }

            this.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> dbData = new HashMap<>();
                    dbData.put("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("post"))));
                    dbData.put("email", bundle.getString("email"));
                    dbData.put("comment", comments.getText().toString());
                    dbData.put("item_availability", item.getRating());
                    dbData.put("queue_time", String.valueOf(queue.getValue()));
                    dbData.put("staff_efficiency", staff.getRating());

                    try {
                        Date fuck = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                        dbData.put("date", new Timestamp(fuck.getTime()));
                        Log.e("FFFFF", dbData.get("date").toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    final Map<String, Object> dbData2 = dbData;

                    store.collection("Reviews")
                            .whereEqualTo("email", bundle.getString("email"))
                            .whereEqualTo("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("post"))))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        QuerySnapshot snapshot = task.getResult();
                                        for (QueryDocumentSnapshot doc : snapshot) {
                                            store.collection("Reviews").document(doc.getId())
                                                    .set(dbData2)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(UserReviewActivity.this, "Your update has been saved. Go back to the last page", Toast.LENGTH_LONG).show();
                                                            }
                                                            else {
                                                                Toast.makeText(UserReviewActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    else {
                                        store.collection("Reviews").add(dbData2)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(UserReviewActivity.this, "Your update has been saved. Go back to the last page", Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UserReviewActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                }
            });
        }
        else {
            Log.e("UserReviewActivity", "Something went wrong");
        }
    }
}
