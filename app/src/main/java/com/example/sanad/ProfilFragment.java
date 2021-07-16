
package com.example.sanad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfilFragment extends Fragment  {
    View view;
    private TextView logout;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String UID,name,phone;
    Button insert;
    //private FirebaseDatabase demande;
    public DatabaseReference refdemande;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profil, container, false);
        context = container.getContext();

        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

        });





        // demande = FirebaseDatabase.getInstance();
        //refdemande= demande.getReference();

         // refdemande = FirebaseDatabase.getInstance().getReference();
         insert = (Button) view.findViewById(R.id.membre);
         insert.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 demander();
                 insert.setEnabled(false);
             }
         });

         return view;
    }

    //public void onCreate(Bundle savedInstanceState) {
       // super.onCreate(savedInstanceState);


    //}
      private void demander(){

          fStore = FirebaseFirestore.getInstance();
          mAuth = FirebaseAuth.getInstance();

          UID = mAuth.getCurrentUser().getUid();
          DocumentReference documentReference = fStore.collection("Users").document(UID);

          documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if(task.isSuccessful()){
                      DocumentSnapshot document = task.getResult();
                      if(document.exists()){
                          name = document.getData().get("nomComplet").toString();

                          String nom = name;
                          String id = mAuth.getCurrentUser().getUid();
                          refdemande = FirebaseDatabase.getInstance("https://sanad-30a51-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("requestmm").child(UID);

                          Demandes test = new Demandes(nom,id);
                           //refdemande.child("requestmm").setValue(test);


                         // HashMap<String, Demandes> registerHash = new HashMap<>();
                         // registerHash.put(refdemande.push().getKey(), test);

                         // refdemande.push().setValue(registerHash);
                          refdemande.setValue(test);
                          Toast.makeText(context, "Demande ajouté" , Toast.LENGTH_SHORT).show();
                      }
                  }
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) { }});


      }
}