package com.example.workability;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class WorkoutLayout extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ToggleButton toggleButton;
    TextToSpeech tts;
    private ProgressBar progressBar;
    private TextView timerTextView;
    TextView titleId, objectiveId, descriptionId;
    private CountDownTimer countDownTimer;
    String title;
    private FirebaseFirestore db;
    String t,o,d;
    private final int totalTime = 30000; // 30 seconds
    private final int interval = 100; // 100ms update interval
    private boolean isRunning = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_layout);  // Ensure correct layout file is used!
        tts = new TextToSpeech(this, this);
        title = getIntent().getExtras().getString("email");
        // Initialize UI components
        toggleButton = findViewById(R.id.toggleBtn);
        progressBar = findViewById(R.id.progressBar);
        timerTextView = findViewById(R.id.timerTextView);
        titleId = findViewById(R.id.titleId);
        objectiveId = findViewById(R.id.objectiveId);
        descriptionId = findViewById(R.id.descriptionId);
        db = FirebaseFirestore.getInstance();
        initialize();
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        progressBar.setMax(totalTime / interval);
        progressBar.setProgress(totalTime / interval);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startCountdown();
            } else {
                stopCountdown();
            }
        });
        titleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakText(t);
            }
        });
        objectiveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakText(o);
            }
        });
        descriptionId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakText(d);
            }
        });
    }

    void initialize() {
        String disability = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'Disability'");

        // Display disability type for debugging
//        Toast.makeText(this, "Disability: " + disability, Toast.LENGTH_SHORT).show();

        if (disability == null || disability.isEmpty()) {
            Toast.makeText(this, "Invalid disability type", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection(disability)
                .whereEqualTo("Title", title)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {  // ✅ Check if results exist
                        QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        titleId.setText(documentSnapshot.getString("Title"));
//                        speakText(documentSnapshot.getString("Title"));
                        t = titleId.getText().toString().trim();
                        objectiveId.setText(documentSnapshot.getString("Objective"));
//                        speakText(documentSnapshot.getString("Objective"));
                        o = objectiveId.getText().toString().trim();
                        descriptionId.setText(documentSnapshot.getString("Description"));
                        speakText(documentSnapshot.getString("Description"));
                        d = descriptionId.getText().toString().trim();
                    } else {
                        Toast.makeText(WorkoutLayout.this, "No record found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(WorkoutLayout.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void startCountdown() {
        isRunning = true;
        toggleButton.setEnabled(false); // Disable button during countdown

        // Stop speaking if TTS is speaking
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }

        // Start the countdown timer for 30 seconds
        countDownTimer = new CountDownTimer(totalTime, interval) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                timerTextView.setText(seconds + "s");

                // Update progress bar
                int progress = (int) (millisUntilFinished / interval);
                progressBar.setProgress(progress);

                // Speak the current second count
                speakText(String.valueOf(seconds));
            }

            public void onFinish() {
                timerTextView.setText("Done!");
                toggleButton.setChecked(false);
                toggleButton.setEnabled(true); // Re-enable button after countdown finishes
                isRunning = false;
            }
        }.start();
    }

    private void stopCountdown() {
        if (isRunning && countDownTimer != null) {
            countDownTimer.cancel();
            isRunning = false;
        }
        timerTextView.setText("Stopped");
        progressBar.setProgress(progressBar.getMax());
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to English
            int langResult = tts.setLanguage(Locale.ENGLISH);
            if (langResult == TextToSpeech.LANG_MISSING_DATA
                    || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported or missing data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakText(String text) {
        if (tts != null && !tts.isSpeaking()) {
            // Speak the text aloud if not already speaking
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
