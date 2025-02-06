package com.example.workability;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        createDatabase();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                String query = "SELECT * FROM Configuration";
                if (DBClass.checkIfRecordExist(query)) {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                    finish();
                } else {
                    intent = new Intent(SplashScreen.this, Login.class);
                    finish();
                }
                startActivity(intent);
                finish();
            }
        }, 2700);
    }

    public void createDatabase() {
        String query;
        DBClass.database = openOrCreateDatabase(DBClass.dbname, MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS Configuration(CName VARCHAR, CValue VARCHAR);";
        DBClass.execNonQuery(query);
    }
}