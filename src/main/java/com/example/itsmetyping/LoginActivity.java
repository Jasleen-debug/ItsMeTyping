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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView txtNeedNewAccount;

    private FirebaseAuth firebaseAuthLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuthLogin = FirebaseAuth.getInstance();

        loginEmail = (EditText) findViewById(R.id.email);
        loginPassword = (EditText) findViewById(R.id.password);

        loginButton = findViewById(R.id.btnLoginButton);
        txtNeedNewAccount = findViewById(R.id.txtNotSignedUp);
        progressDialog = new ProgressDialog(this);

        loginButton.setOnClickListener(loginButtonClickListener);
        txtNeedNewAccount.setOnClickListener(txtNeedNewAccountClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            FirebaseUser currentUser = firebaseAuthLogin.getCurrentUser();
            Log.d("JK","In login on start");
            if(currentUser != null){
                sendToMain();
            }
        }catch(Exception e){
            Log.d("JK",e.toString());
        }

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private View.OnClickListener loginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressDialog.setMessage("Checking Login");
            progressDialog.show();
            Log.d("JK","I am inside login click listener");
            Log.d("JK","in login");
            String email = null;
            String password = null ;
            try{
                email = loginEmail.getText().toString();
                Log.d("JK",email);
                password = loginPassword.getText().toString();
            }catch (Exception e ){
                // This was only for testing purpose!
                email = "stupid@gmail.com";
                password = "password";
                Log.d("JK",e.toString());
            }
            loginUser(email, password);
        }
    };

    private View.OnClickListener txtNeedNewAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    private void loginUser(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()){
            firebaseAuthLogin.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("JK","in on complete");
                    if (task.isSuccessful()) {
                        Log.d("JK","task success");
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                        sendToMain();
                        progressDialog.dismiss();
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "Empty fields are not allowed!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}