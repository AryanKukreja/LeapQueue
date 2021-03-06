package com.twiliohackathon.leapqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class UserReviewActivity extends AppCompatActivity {
    RatingBar item, staff;
    TextInputEditText comments, date, time;
    NumberPicker queue;

    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    Date dateVal;

    Calendar queueDateCal = Calendar.getInstance();

    MaterialButton delete, submit;

    FirebaseFirestore store;
    String postal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        this.store = FirebaseFirestore.getInstance();

        this.submit = findViewById(R.id.submit);
        this.delete = findViewById(R.id.delete);
        this.item = findViewById(R.id.item);
        this.queue = findViewById(R.id.queue);
        this.staff = findViewById(R.id.staff);
        this.comments = findViewById(R.id.comment_field);

        this.queue = findViewById(R.id.queue);
        queue.setMax(300);
        queue.setMin(0);
        queue.setUnit(4);
        queue.setValue(1);

        date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                datePicker = new DatePickerDialog(UserReviewActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                queueDateCal.set(Calendar.YEAR, year);
                                queueDateCal.set(Calendar.MONTH, monthOfYear);
                                queueDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
            }
        });

        this.time = findViewById(R.id.time);
        this.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerDialog(UserReviewActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText((hourOfDay > 12 ? hourOfDay - 12 : hourOfDay )+ ":" + (minute > 10 ? minute : (minute == 0 ? "00" : "0" + minute)) + (hourOfDay > 12 ? " PM" : " AM"));
                        queueDateCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        queueDateCal.set(Calendar.MINUTE, minute);
                    }
                }, 0, 0, false);

                timePicker.show();
            }
        });

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ((TextView) findViewById(R.id.address)).setText(bundle.getString("address"));
            ((TextView) findViewById(R.id.name)).setText(bundle.getString("name"));
            this.postal = bundle.getString("postal");

            if (bundle.getBoolean("reviewPresent")) {
                String text = "Edit Your Review";
                ((TextView) findViewById(R.id.review_action)).setText(text);

                this.item.setRating(bundle.getFloat("itm"));
                this.queue.setValue(bundle.getInt("qtm"));
                this.staff.setRating(bundle.getFloat("eff"));
                this.comments.setText(bundle.getString("com"));
                this.dateVal = (Date) bundle.get("date");

                queueDateCal = Calendar.getInstance();
                queueDateCal.setTime(dateVal);
                this.date.setText(queueDateCal.get(Calendar.DAY_OF_MONTH) + "/" + (queueDateCal.get(Calendar.MONTH) + 1) + "/" + queueDateCal.get(Calendar.YEAR));
                this.time.setText(queueDateCal.get(Calendar.HOUR_OF_DAY) + ":" + (queueDateCal.get(Calendar.MINUTE) < 10 ? "0" + queueDateCal.get(Calendar.MINUTE) : (queueDateCal.get(Calendar.MINUTE) == 0 ? "00" : queueDateCal.get(Calendar.MINUTE))) + (queueDateCal.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM"));

                this.submit.setText(R.string.update);

                this.delete.setVisibility(View.VISIBLE);
                this.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        store.collection("Reviews")
                                .whereEqualTo("email", bundle.getString("email"))
                                .whereEqualTo("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("postal"))))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot snapshot = task.getResult();
                                        for (QueryDocumentSnapshot doc : snapshot) {
                                            store.collection("Reviews").document(doc.getId())
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                finish();
                                                            } else {
                                                                Toast.makeText(UserReviewActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
            }  else {
                this.delete.setVisibility(View.GONE);
                this.submit.setText(getResources().getString(R.string.sub));
            }

            this.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> dbData = new HashMap<>();
                    dbData.put("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("postal"))));
                    dbData.put("email", bundle.getString("email"));
                    dbData.put("comment", comments.getText().toString());
                    dbData.put("item_availability", item.getRating());
                    dbData.put("queue_time", String.valueOf(queue.getValue()));
                    dbData.put("staff_efficiency", staff.getRating());
                    dbData.put("date", new Timestamp(queueDateCal.getTimeInMillis()));

                    final Map<String, Object> dbData2 = dbData;
                    store.collection("Reviews")
                            .whereEqualTo("email", bundle.getString("email"))
                            .whereEqualTo("postal", Integer.parseInt(Objects.requireNonNull(bundle.getString("postal"))))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        QuerySnapshot snapshot = task.getResult();
                                        for (QueryDocumentSnapshot doc : snapshot) {
                                            store.collection("Reviews").document(doc.getId())
                                                .set(dbData2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(UserReviewActivity.this, "Your update has been saved. Go back to the last page", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(UserReviewActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        }
                                    } else {
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
        }  else {
            Log.e("UserReviewActivity", "Something went wrong");
        }
    }
}