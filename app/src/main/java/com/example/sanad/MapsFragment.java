package com.example.sanad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                             Bundle savedInstanceState) {

       //return inflater.inflate(R.layout.fragment_maps, container, false);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view);
            OnMapReadyCallback callback = new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    GoogleMap mMap = googleMap;
                    LatLng smiles = new LatLng(39.104729, -77.191294);
                    mMap.addMarker(new MarkerOptions().position(smiles).title("North Potomac Smiles, LLC."));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(smiles));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(smiles, 16));
                }
            };
        mapFragment.getMapAsync(callback);
        return view;

    }

/**   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    } */
}
