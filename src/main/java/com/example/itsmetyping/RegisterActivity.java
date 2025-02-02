package com.example.itsmetyping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;

    private Button registerButton;

    private TextView txtAlreadyRegistered;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.etUserEmail);
        userPassword = findViewById(R.id.etUserPassword);
        registerButton = findViewById(R.id.btRegisterButton);
        txtAlreadyRegistered = findViewById(R.id.txtAlreadyRegistered);

        registerButton.setOnClickListener(registerButtonClickListener);
        txtAlreadyRegistered.setOnClickListener(txtAlreadyRegisteredClickListener);

    }

    private View.OnClickListener registerButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startRegistration();
        }
    };

    private View.OnClickListener txtAlreadyRegisteredClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    };

    private void startRegistration() {
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Successful Sign Up!", Toast.LENGTH_SHORT).show();
                            firebaseAuth = null;
                            startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
                            finish();
                        } else{
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Sorry, Unsuccessful Sign Up!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                userPassword.setError("Empty field not allowed!");
            }
        } else if (email.isEmpty()) {
            userEmail.setError("Empty field not allowed");
        } else {
            userEmail.setError("Please enter a valid email");
        }
    }
}
