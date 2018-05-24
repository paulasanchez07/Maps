package com.hacu1.icropmaps.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hacu1.icropmaps.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//Carga mapa de la clase implementada, cuando llege su turno de usarlo.
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ibague = new LatLng(4.4330777, -75.18926669999999);//Localizaciones: Latitudes y longitudes
        mMap.addMarker(new MarkerOptions().position(ibague).title("Hola Desde Ibagué").draggable(true));//Agregar el marcador (arrastrable) y asigna posicion

        //Permite establecer el rango de zoom (por defecto va desde 0 a 21)
        //Sirve para que el usuario no se aleje de la zona en la que vamos a trabajar
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(15);

        CameraPosition camera = new CameraPosition.Builder()
                .target(ibague)
                .zoom(10)   //limite --> 21
                .bearing(0)//orientacion de la camara hacia el este 90grados
                .tilt(90)   //efecto 3d en edificios - limite 90
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(ibague));//Mueve la camara hacia el marcador

        /************Eventos en el mapa*******************/

        //Cuando hacemos clic en el Mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Sabemos donde dimos clic
                Toast.makeText(MapsActivity.this,"Click en: \n  Lat: "+latLng.latitude
                        + "\n Lon:" + latLng.longitude,Toast.LENGTH_SHORT).show();
            }
        });

        //Cuando hacemos un clic con una duracion mas larga
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Sabemos donde dimos clic
                Toast.makeText(MapsActivity.this,"Long Click en: \n  Lat: "+latLng.latitude
                        + "\n Lon:" + latLng.longitude,Toast.LENGTH_SHORT).show();
            }
        });

        //Para cuando queremos arrastrar el marcador
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //Cuando empezamos a arrastrar el marcadorç
                /*Toast.makeText(MapsActivity.this,"MarkerDragStart en: \n  Lat: "+marker.getPosition().latitude
                        + "\n Lon:" + marker.getPosition().longitude,Toast.LENGTH_SHORT).show();
                 */
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //Mientras el marcador es arrastrado
                /*Toast.makeText(MapsActivity.this,"MarkerDrag en: \n  Lat: "+marker.getPosition().latitude
                        + "\n Lon:" + marker.getPosition().longitude,Toast.LENGTH_SHORT).show();
                */
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //Cuando termina de arrastrar el marcador
                Toast.makeText(MapsActivity.this,"MarkerDragEnd en: \n  Lat: "+marker.getPosition().latitude
                        + "\n Lon:" + marker.getPosition().longitude,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
