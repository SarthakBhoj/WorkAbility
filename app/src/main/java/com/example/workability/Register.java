package com.example.workability;  // Change to your package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button btnRegister;
    private TextView loginText;
    String disability;
    private FirebaseFirestore firestore;
    Spinner disabilitySpinner;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        loginText = findViewById(R.id.loginText);
        disabilitySpinner = findViewById(R.id.disability_type_spinner);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.register);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.disability_types,
                android.R.layout.simple_spinner_item
        );

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Register.this, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disabilitySpinner.setAdapter(adapter);

        // Set Listener (Optional)
        disabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disability = parent.getItemAtPosition(position).toString();
//                Toast.makeText(Register.this, "Selected: " + disability, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(!isValidEmail(email)){
            editTextEmail.setError("Enter Valid Email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user data
        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("Email", email);
        user.put("Disability", disability);
        user.put("Password", password);
        boolean res = false;
        // Store data in Firestore under "Users" collection
        // Store data in Firestore
        firestore.collection("Users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    // Navigate to HomeActivity on success
                    Intent intent = new Intent(Register.this, Login.class);
                    intent.putExtra("userName", name); // Send username to the next screen
                    startActivity(intent);
                    finish(); // Close registration activity
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }
}
