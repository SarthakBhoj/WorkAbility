package com.example.workability;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class workouts extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<workout_item> examList;
    private workoutAdapter adapter; // Adapter is now a member of the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        examList = new ArrayList<>();
        adapter = new workoutAdapter(examList);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter
        recyclerView.setAdapter(adapter);

        // Load workouts
        loadWorkouts();
    }

    public void loadWorkouts() {
        // Get disability type from the database
        String disability = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'Disability'");

        // Display disability type for debugging
//        Toast.makeText(this, "Disability: " + disability, Toast.LENGTH_SHORT).show();

        if (disability == null || disability.isEmpty()) {
            Toast.makeText(this, "Invalid disability type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear the list if you want to reload the data
        examList.clear();

        // Firestore query
        db.collection(disability)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String title = documentSnapshot.getString("Title");
                            String description = documentSnapshot.getString("Description");
                            String objective = documentSnapshot.getString("Objective");

                            // Add item to the list if data is valid
                            if (title != null && description != null && objective != null) {
                                workout_item wk = new workout_item(title, objective, description);
                                examList.add(wk);
                            }
                        }

                        // Notify adapter after data is loaded
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(workouts.this, "Error loading workout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
