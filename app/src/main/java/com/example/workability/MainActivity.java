package com.example.workability;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView txtGreeting,workouttxt,communitytxt;
    TextToSpeech tts;
    LinearLayout workoutCard,communityCard;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onStart(){
        super.onStart();
        String Name = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Name'");
        txtGreeting.setText("Hi, "+Name);
//        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this,this);
        txtGreeting = findViewById(R.id.txtGreeting);
        workoutCard = findViewById(R.id.workoutCard);
        communityCard = findViewById(R.id.communityCard);
        communitytxt = findViewById(R.id.communitytxt);
        workouttxt = findViewById(R.id.workouttxt);

        communityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = communitytxt.getText().toString().trim();
                speakText(text);
                Intent intent =  new Intent(getApplicationContext(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
        workoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = workouttxt.getText().toString().trim();
                speakText(text);
                Intent intent =  new Intent(getApplicationContext(),workouts.class);
                startActivity(intent);
            }
        });
        txtGreeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakText(txtGreeting.getText().toString().trim());
            }
        });
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
        if (tts != null) {
            // Speak the text aloud
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

}