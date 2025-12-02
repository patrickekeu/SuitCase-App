package com.example.suitcaseapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suitcaseapp.R;
import com.example.suitcaseapp.Adapter.ToDoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // UI elements
    private Button createAccountButton;
    private TextView alreadyAccountButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private EditText confirmpasswordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Initialize UI elements
        alreadyAccountButton = findViewById(R.id.alreadyHaveAccount);

        createAccountButton = findViewById(R.id.btnRegister);

        usernameEditText = findViewById(R.id.input_username);

        emailEditText = findViewById(R.id.input_email);

        passwordEditText = findViewById(R.id.input_password);
        confirmpasswordEditText = findViewById(R.id.input_confirm_password);

        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        //In for debug and testing purposes to clear all logged in users
        mAuth.signOut();

        // Set up click listeners for buttons
        createAccountButton.setOnClickListener(this);
        alreadyAccountButton.setOnClickListener(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // If user is already signed in, navigate to the ToDoAdapter activity
            Intent intent = new Intent(this, ToDoAdapter.class);

            startActivity(intent);
        }
        else
        {
            Log.d("currentUserCheck", "not logged in");
        }
    }

    @Override
    public void onClick(View view) {
        // Get values from input fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirm_password = confirmpasswordEditText.getText().toString().trim();

        int id = view.getId();
        if(id == R.id.alreadyHaveAccount)
        {
            // Navigate to LoginActivity if the user already has an account
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
        else if (id == R.id.btnRegister)
        {
            // Check if password matches the confirmation password
            if(!password.equals(confirm_password))
            {
                Toast.makeText(RegisterActivity.this, "Password doesn't match.", Toast.LENGTH_LONG).show();

            }
            else
            {
                // Create a new user with the provided email and password
                newUser(email, password);
            }


        }
    }

    // Method to create a new user with the provided email and password
    private void newUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthLog", "createUserWithEmail:success");
                            startActivity(new Intent(RegisterActivity.this, viewToDoActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AuthLog", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }


                    }

                });


    }


}