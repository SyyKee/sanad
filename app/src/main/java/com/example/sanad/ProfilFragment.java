package com.example.sanad;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class ProfilFragment extends Fragment  {
    View view;
    private TextView logout;
    FirebaseAuth mAuth;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profil, container, false);


        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

        });



        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

}