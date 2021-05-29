package com.example.sanad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TAG = "TAG";
    private EditText TextEmail, TextNom , TextCin , TextNumero , TextAdresse ,TextMotDePasse ;
    private TextView svg , s_inscrire;
    private ProgressBar barreProgression;
    private FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    String UID;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

       // svg = (TextView) findViewById(R.id.imageView3);
       // svg.setOnClickListener(this);

        s_inscrire = (Button) findViewById(R.id.button4);
        s_inscrire.setOnClickListener(this);

        TextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        TextNom = (EditText) findViewById(R.id.editTextTextNom);
        TextCin = (EditText) findViewById(R.id.editTextTextCin);
        TextNumero = (EditText) findViewById(R.id.editTextTextTel);
        TextAdresse = (EditText) findViewById(R.id.editTextTextAdresse);
        TextMotDePasse = (EditText) findViewById(R.id.editTextTextPassword);
        barreProgression = (ProgressBar) findViewById(R.id.progressBar);



    }

    @Override
    public void onClick(View v) {


        String email = TextEmail.getText().toString().trim();
        String nom = TextNom.getText().toString().trim();
        String cin = TextCin.getText().toString().trim();
        String  numero = TextNumero.getText().toString().trim();
        String adresse = TextAdresse.getText().toString().trim();
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

        if(nom.isEmpty()){
        TextNom.setError("Saisissez votre nom complet!");
        TextNom.requestFocus();
        return;
        }

        if(cin.isEmpty()){
            TextCin.setError("Saisissez votre CIN!");
            TextCin.requestFocus();
            return;
        }

        if(numero.isEmpty()){
            TextNumero.setError("Saisissez votre numéro!");
            TextNumero.requestFocus();
            return;
        }

        if(adresse.isEmpty()){
            TextAdresse.setError("Saisissez votre Adresse!");
            TextAdresse.requestFocus();
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
        mAuth.createUserWithEmailAndPassword(email, motDePasse).addOnCompleteListener((task) -> {

            if(task.isSuccessful()){
                Toast.makeText(RegisterActivity.this,"Utilisateur crée",Toast.LENGTH_SHORT).show();
                UID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("Users").document(UID);
                Map<String,Object> user = new HashMap<>();
                user.put("email",email);
                user.put("nomComplet",nom);
                user.put("cin",cin);
                user.put("numéro",numero);
                user.put("adresse",adresse);
                user.put("motDePasse",motDePasse);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Utilisateur crée"+UID);

                    }
                });
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }else {
                Toast.makeText(RegisterActivity.this,"Erreur"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                barreProgression.setVisibility(View.GONE);
            }
        });

    }
}