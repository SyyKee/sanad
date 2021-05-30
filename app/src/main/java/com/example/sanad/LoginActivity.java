package com.example.sanad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText TextEmail,TextMotDePasse;
    Button se_connecter;
    ProgressBar barreProgression;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextEmail = findViewById(R.id.editTextTextEmailAddress);
        TextMotDePasse = findViewById(R.id.editTextTextPassword);
        barreProgression = findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        se_connecter = findViewById(R.id.button4);

        se_connecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = TextEmail.getText().toString().trim();
                String motDePasse = TextMotDePasse.getText().toString().trim();

                if(email.isEmpty()){
                    TextEmail.setError("Saisissez votre email!");
                    TextEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    TextEmail.setError("Email invalide!");
                    TextEmail.requestFocus();
                    return;

                }
                if(motDePasse.isEmpty()){
                    TextMotDePasse.setError("Saisissez votre mot de passe!");
                    TextMotDePasse.requestFocus();
                    return;
                }
                if (motDePasse.length() < 6) {
                    TextMotDePasse.setError("Votre mot de passe doit être composé d'au moins 6 caractères");
                    TextMotDePasse.requestFocus();
                    return;
                }

                barreProgression.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,motDePasse).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Connexion réussie ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),navigation.class));
                        }else{
                            Toast.makeText(LoginActivity.this,"Email ou mot de passe incorrect"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            barreProgression.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}