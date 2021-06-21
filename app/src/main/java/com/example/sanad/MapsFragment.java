package com.example.sanad;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private String  subject, message;
    TextView email;
    private Button button;
    View view;
    Context context;
    MapView mapView;
    GoogleMap map;
    List<DeclarationModel> list;
    int counter = 1;
    public FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    String UID;
    public static final String TAG = "TAG";
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = container.getContext();
        list = new ArrayList<>();

        // Initialiser mapView pour charger google maps
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return view;

    }

    // methode du clique sur le marker
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        String mark = marker.getTitle();
        int position = Integer.parseInt(mark);

        // Obtenir les donnees du marqueur et afficher ces donnees dans la boite de dialogue
        DeclarationModel declaration = list.get(position-1);
        showDialog(declaration);

        return false;

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;


        //Obtenir toutes les déclarations de firestore et les afficher sur la carte en tant que marqueur
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Declarations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DeclarationModel model = new DeclarationModel(document.getId(),document.get("username").toString(),document.get("address").toString(),
                                        document.get("phone").toString(),document.get("decText").toString(),document.getDouble("lati"),document.getDouble("longi"));
                                list.add(model);
                                LatLng userLocation = new LatLng(document.getDouble("lati"), document.getDouble("longi"));
                                map.addMarker(new MarkerOptions().position(userLocation).title(counter+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,5));
                                counter = counter+1;
                            }
                        }
                    }
                });

        //faire appel a la fonction onMarkerClick qui permet au user de cliquer sur la map
        map.setOnMarkerClickListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // verifier les permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
    private void senEmail() {

       // fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getCurrentUser().getEmail();
        //DocumentReference documentReference = fStore.collection("Users").document(UID);



        String mEmail = UID.toString();
        String mSubject = "pfe";
        String mMessage = "hello";


        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mEmail, mSubject, mMessage);

        javaMailAPI.execute();
    }



    // afficher la boite de dialogue apres le clique sur le marqueur
    private void showDialog(DeclarationModel declaration) {

        // afficher notre boite de dialogue du fichier layout
        AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dailogBuilder.setView(dialogView);
        final TextView tvUsername = dialogView.findViewById(R.id.tvUsername);
        final TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        final TextView tvPhone = dialogView.findViewById(R.id.tvPhone);
        final TextView tvDeclaration = dialogView.findViewById(R.id.tvDeclaration);
        final Button btn = dialogView.findViewById(R.id.btn);


        tvUsername.setText(declaration.getUsername());
        tvAddress.setText(declaration.getAddress());
        tvPhone.setText(declaration.getPhone());
        tvDeclaration.setText(declaration.getDecText());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senEmail();
            }
        });

        AlertDialog alertDialog = dailogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }


}
