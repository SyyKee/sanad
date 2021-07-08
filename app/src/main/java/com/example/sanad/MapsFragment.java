package com.example.sanad;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private String  subject, message;
    public String nomComplet,num,tvparticipation;
    public EditText participation;
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
    public static final String TAG = "TAG" ;
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
                                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,60));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
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
    private void sendEmail() {


        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getCurrentUser().getEmail();
        String mEmail = UID.toString();
        String mSubject = "Sanad";
        String mMessage = "C'est confirmé!";


        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mEmail, mSubject, mMessage);

        javaMailAPI.execute();
    }
    private void sendEmail2() {


        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getCurrentUser().getEmail();

        String mEmail = UID.toString();
        String mSubject = "Sanad";
        String mMessage = "Participation confirmée!";


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
        final Button btn2 = dialogView.findViewById(R.id.btn2);


        tvUsername.setText(declaration.getUsername());
        tvAddress.setText(declaration.getAddress());
        tvPhone.setText(declaration.getPhone());
        tvDeclaration.setText(declaration.getDecText());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.parseColor("#808080"));

            }

        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog2();
            }
        });

        AlertDialog alertDialog = dailogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void showDialog2() {

        // afficher notre boite de dialogue du fichier layout
        AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout2, null);
        dailogBuilder.setView(dialogView);
        final Button btn3 = dialogView.findViewById(R.id.btn33);
        //final EditText participation ;

       // participation = (EditText) dialogView.findViewById(R.id.tvParticipation);
        //tvparticipation  = participation.getText().toString().trim();


        btn3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

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

                                nomComplet = document.getData().get("nomComplet").toString();
                                num = document.getData().get("numéro").toString();


                                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime date = LocalDateTime.now();
                                date.format(format);
                                String dateS = date.toString();
                                boolean statut = false;

                                //fStore = FirebaseFirestore.getInstance();

                                participation = (EditText) dialogView.findViewById(R.id.tvtvParticipation);
                                tvparticipation  = participation.getText().toString().trim();

                                Map<String,Object> participationC = new HashMap<>();
                                participationC.put("Participation",tvparticipation);
                                participationC.put("Date",dateS);
                                participationC.put("Statut",statut);
                                participationC.put("Nom Complet",nomComplet);
                                participationC.put("Numéro",num);
                                String id = String.valueOf(System.currentTimeMillis());
                                fStore.collection("Participation")
                                        .document(id)
                                        .set(participationC);


                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { }});


                Toast.makeText(context, "Participation ajouté", Toast.LENGTH_SHORT).show();

                participation = null;
                sendEmail2();

            }
        });

        AlertDialog alertDialog = dailogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }




}
