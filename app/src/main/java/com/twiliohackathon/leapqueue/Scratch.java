package com.twiliohackathon.leapqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Scratch extends AppCompatActivity {

    String dateString = "26/4/2019";
    FirebaseFirestore store;
    TextView comm, date, email, item, post, queue, staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);

        store = FirebaseFirestore.getInstance();

        comm = findViewById(R.id.comments);
        date = findViewById(R.id.date);
        email = findViewById(R.id.email);
        item = findViewById(R.id.item);
        post = findViewById(R.id.postal);
        queue = findViewById(R.id.queue);
        staff = findViewById(R.id.staff);

        store.collection("Reviews")
                .whereEqualTo("email", "sammy.parker52@gmail.com")
                .whereEqualTo("postal", 11416)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.e("Yas1", doc.get("comment").toString());
                            Timestamp stamp = doc.getTimestamp("date");
                            Date date = stamp.toDate();
                            Log.e("yas", date.toString());

                            Calendar queueDateCal = Calendar.getInstance();
                            queueDateCal.setTime(date);

                            Log.e("QueueCal", queueDateCal.get(Calendar.DAY_OF_MONTH) + "/" + (queueDateCal.get(Calendar.MONTH) + 1) + "/" + queueDateCal.get(Calendar.YEAR));
                        }
                    }
                });
    }
}
