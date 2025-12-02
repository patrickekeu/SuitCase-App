package com.example.suitcaseapp.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.suitcaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // UI elements
    private Button loginButton;
    private TextView createAccountButton;
    private EditText emailAddress;
    private EditText password;

    // Firebase authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        // Sign out user if already signed in
        if (mAuth != null) {
            mAuth.signOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assigning UI elements to their respective IDs
        createAccountButton = findViewById(R.id.textViewSignUp);

        loginButton = findViewById(R.id.btnlogin);

        emailAddress = findViewById(R.id.input_username);

        password = findViewById(R.id.input_password);

        // Set click listeners for buttons
        createAccountButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnlogin){
            Log.i("signIn", "we've clicked sign in");

            // Get email and password from the input fields
            String email = emailAddress.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            signIn(email, userPassword);
        }
        else if(id == R.id.textViewSignUp) {
            Log.i("createAccount", "we've clicked create account");
            // Navigate to the RegisterActivity to create a new account
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    // Method to sign in with provided email and password
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthMessage", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //do something with UI

                            Intent intent = new Intent(LoginActivity.this, viewToDoActivity.class);

                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AuthMessage ", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });

    }


}