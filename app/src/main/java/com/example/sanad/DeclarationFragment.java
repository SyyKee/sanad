package com.example.sanad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.sanad.RegisterActivity.TAG;

public class DeclarationFragment extends Fragment {

    View view;
    Context context;
    EditText editTextTextEmailAddress;
    Button submit;
    public FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    String UID, name="", address="", phone="", decText;
    LocationManager locationManager;
    LocationListener locationListener;

    // Permission de localisation
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_declaration, container, false);
        context = container.getContext();

        // initialisation de  Firestore and Firebase Auth
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("Users").document(UID);

        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        submit = view.findViewById(R.id.button4);


        // Lecture des donn??es de Firestore
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        name = document.getData().get("nomComplet").toString();
                       // address = document.getData().get("adresse").toString();
                        phone = document.getData().get("num??ro").toString();

                        //Toast.makeText(context, name+" "+phone+" "+address, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { }});

        // Enregistrer le text de la declaration avec les infos du user sur firestore en cliquant sur declarer

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decText = editTextTextEmailAddress.getText().toString().trim();
                if(decText.isEmpty()){
                    editTextTextEmailAddress.setError("Required!");
                    editTextTextEmailAddress.requestFocus();
                    return;
                }
                //Afficher un progress dialog et faire appel a getLocationn (localisation de l utilisateur qui declare)

                showProgressDialog();
                getLocationn();

            }
        });
        return view;
    }
     //convertir les cordonnes en adresse physique
    private String convertLocationToAddress(Location location) {
        String addressText;
        String errorMessage = "";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        } catch (IOException ioException) {
            // erreur de connection
            errorMessage = "erreur";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // cordonne erron??e
            errorMessage = "erreur";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // on a pas trouv?? l adresse
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "erreur";
                Log.e(TAG, errorMessage);
            }
            addressText = "Contactez nous pour fournir l adresse";

        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // chercher les lignes d adresse et les joindre.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "trouv??");
            addressText =
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments);
        }

        return addressText;

    }
    private void getLocationn() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // LocationManager et LocationListener sont utilises pour obtenir l emplacement actuel de l utilisateur
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                address = convertLocationToAddress(location);
                Map<String, Object> group = new HashMap<>();
                group.put("username", name);
                //group.put("address", address);
                group.put("address", address);
                group.put("phone", phone);
                group.put("decText", decText);
                group.put("lati", latitude);
                group.put("longi", longitude);

                String id = String.valueOf(System.currentTimeMillis());
                db.collection("Declarations")
                        .document(id)
                        .set(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressDialog();
                        Toast.makeText(context, "Declaration ajout??" , Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        Toast.makeText(context, "Erreur : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }};


        // v??rifier si la permission de localisation est autorisee a l application ou non. Sinon, affichez la boite de dialogue a l utilisateur pour autoriser les permissions
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);
            }
        }
    }

    // barre de progression
    ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Recherche de votre emplacement, veuillez patienter...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}