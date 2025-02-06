package com.example.workability;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackEditText;
    private Button submitButton;
    private RecyclerView feedbackRecyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;

    private FirebaseFirestore db;
    private List<Feedback> feedbackList;
    private FeedbackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
//        errorTextView = findViewById(R.id.errorTextView);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);

        db = FirebaseFirestore.getInstance();

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(this, feedbackList);

        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackRecyclerView.setAdapter(adapter);

        loadFeedback();

        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String username = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Email'");
        String feedbackText = feedbackEditText.getText().toString().trim();

        if (username.isEmpty() || feedbackText.isEmpty()) {
            errorTextView.setText("Both fields are required!");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        Feedback feedback = new Feedback(username, feedbackText, new Date());

        db.collection("Feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted!", Toast.LENGTH_SHORT).show();
                    feedbackEditText.setText("");
                    loadFeedback();
//                    Intent intent =  new Intent(FeedbackActivity.this,MainActivity.class);
//                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Error submitting feedback", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFeedback() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Feedback")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    feedbackList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Feedback feedback = document.toObject(Feedback.class);
                        feedbackList.add(feedback);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FeedbackActivity.this, "Failed to load feedback", Toast.LENGTH_SHORT).show();
                });
    }
}
