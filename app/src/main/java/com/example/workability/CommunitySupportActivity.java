package com.example.workability;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workability.SharedExperience;
//import com.example.workability.adapters.ExperiencesAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommunitySupportActivity extends AppCompatActivity {
    private EditText experienceEditText;
    private Button shareButton;
    private RecyclerView experiencesRecyclerView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ExperiencesAdapter adapter;
    private List<SharedExperience> experiencesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_support);

        // Initialize UI Components
        experienceEditText = findViewById(R.id.experienceEditText);
        shareButton = findViewById(R.id.shareButton);
        experiencesRecyclerView = findViewById(R.id.experiencesRecyclerView);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // RecyclerView setup
        experiencesList = new ArrayList<>();
        adapter = new ExperiencesAdapter(this, experiencesList);
        experiencesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        experiencesRecyclerView.setAdapter(adapter);

        // Load experiences from Firestore
        loadExperiences();

        // Set OnClickListener for sharing experience
        shareButton.setOnClickListener(v -> shareExperience());
    }

    private void shareExperience() {
        String experienceText = experienceEditText.getText().toString().trim();

        if (experienceText.isEmpty()) {
//            errorTextView.setText("Experience cannot be empty");
//            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Assuming you have stored users in Firestore with a collection named "Users"
        String userId = "Email"; // Retrieve this from shared preferences or session
        db.collection("Users"
                ).whereEqualTo("Email",userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String userEmail = documentSnapshot.getString("email");
                            String disabilityType = documentSnapshot.getString("disabilityType"); // Assuming stored

                            SharedExperience experience = new SharedExperience(
                                    userEmail,        // User's email from Firestore
                                    disabilityType,   // User's disability type from Firestore
                                    experienceText,   // Experience text
                                    new Date()        // Use Java Date instead of FieldValue
                            );

                            db.collection("SharedExperiences")
                                    .add(experience)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(CommunitySupportActivity.this, "Experience shared!", Toast.LENGTH_SHORT).show();
                                        experienceEditText.setText("");
//                                    errorTextView.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CommunitySupportActivity.this, "Error sharing experience", Toast.LENGTH_SHORT).show();
                                    });
//
                            Toast.makeText(CommunitySupportActivity.this, "User not found in Firestore!", Toast.LENGTH_SHORT).show();
//
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CommunitySupportActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadExperiences() {
//        progressBar.setVisibility(View.VISIBLE);  // Show loading indicator

        db.collection("SharedExperiences")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    experiencesList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            SharedExperience experience = document.toObject(SharedExperience.class);
                            if (experience != null && experience.getTimestamp() != null) {
                                experiencesList.add(experience);
                            } else {
                                Log.e("FirestoreError", "Document missing timestamp: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e("FirestoreError", "Error parsing document: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
//                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CommunitySupportActivity.this, "Failed to load experiences: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FirestoreError", "Error loading experiences", e);
                });
    }


}
