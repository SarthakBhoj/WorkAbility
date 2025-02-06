package com.example.workability;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseFirestore firestore;
    private TextView textRegister;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();
        textRegister =  findViewById(R.id.textRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
            }
        });
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(!isValidEmail(email)){
            editTextEmail.setError("Enter Valid Email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search for user with the given email
        firestore.collection("Users")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String storedPassword = document.getString("Password");
                        String Name = document.getString("Name");
                        String Email = document.getString("Email");
                        String Disability = document.getString("Disability");
                        String UserId = document.getId();
                        if (Objects.equals(storedPassword, password)) {
                            // Successful login

                            String query ="DELETE FROM Configuration";
                            DBClass.execNonQuery(query);

                            query = "INSERT INTO Configuration(CName, CValue) ";
                            query += "VALUES('Name', '" +  Name + "')";
                            DBClass.execNonQuery(query);

                            query="INSERT INTO Configuration(CName,CValue)";
                            query += "VALUES('Email', '" + Email + "')";
                            DBClass.execNonQuery(query);

                            query = "INSERT INTO Configuration(CName, CValue) ";
                            query += "VALUES('Disability', '" + Disability + "')";
                            DBClass.execNonQuery(query);

                            query = "INSERT INTO Configuration(CName, CValue) ";
                            query += "VALUES('UserId', '" + UserId + "')";
                            DBClass.execNonQuery(query);

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("userEmail", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}