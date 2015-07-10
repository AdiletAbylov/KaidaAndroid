package com.desu.mapapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desu.mapapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.rey.material.drawable.RippleDrawable;
import com.rey.material.widget.FloatingActionButton;

public class MapFragment extends Fragment {


    MapView mapView;
    private GoogleMap mMap;

    MapsHelper mapsHelper = new MapsHelper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)          {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton bt_float = (FloatingActionButton)v.findViewById(R.id.Button);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v instanceof  FloatingActionButton){
                    FloatingActionButton bt = (FloatingActionButton)v;
                    bt.setLineMorphingState((bt.getLineMorphingState() + 1) % 2, true);
                }

                System.out.println(v + " " + ((RippleDrawable)v.getBackground()).getDelayClickType());
            }
        };

        bt_float.setOnClickListener(listener);



        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        mMap.animateCamera(cameraUpdate);

*/
        setUpMap();
        return v;
    }




    private void setUpMap() {
        //mMap.getUiSettings().setCompassEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mapsHelper.mapInit(mMap, getActivity());//map,context
        mapsHelper.toPos(mapsHelper.BishkekCoordinates,12);//coord,zoom
        mapsHelper.setMarker("Бишкек", mapsHelper.BishkekCoordinates);//title,latitude,longitude
        //mapsHelper.getDirection(mapsHelper.BishkekCoordinates, mapsHelper.BishkekCoordinates1);
        mapsHelper.getPoints(getString(R.string.server_url));
        //mapsHelper.clear();

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




}