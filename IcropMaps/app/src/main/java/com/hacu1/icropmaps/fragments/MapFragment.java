package com.hacu1.icropmaps.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.hacu1.icropmaps.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.content.Context.LOCATION_SERVICE;


public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener, GoogleMap.OnPoiClickListener {
    View vista;
    private GoogleMap gMap;
    private MapView mapView; //Captura el layout
    //Action Button
    private FloatingActionButton fab;

    private LocationManager locationManager;
    private Location currentLocation;
    private Marker marker;
    private CameraPosition cameraZoom;


    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_map, container, false);
        fab = (FloatingActionButton) vista.findViewById(R.id.fab);

        fab.setOnClickListener(this);

        return vista;
    }

    //Se crea la vista
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Referencias
        mapView = (MapView) vista.findViewById(R.id.map);//Mapa del layout
        if (mapView != null) {
            mapView.onCreate(null);//Crea Manualmente el mapview
            mapView.onResume();//Se llama cuando este activity esta visible
            mapView.getMapAsync(this);//Implementa la interfaz OnMapReadyCallBack
        }

        //SOLICITA GPS
        //validarGPS();
    }

    //Genera un Loop: No deja al usuario seguir a la App si no activa el GPS
    @Override
    public void onResume() {
        super.onResume();
        //this.validarGPS();
    }


    /*@Override
    public void onPause() {
        //gMap = null;
        super.onPause();

    }*/

    //Metodo que administra el objeto GoogleMap
    //objeto GoogleMap: configura las opciones de vista de un mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Obtenemos al parametro del metodo
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);


        //Escucha de clic en los puntos de interes
        gMap.setOnPoiClickListener(this);

        //PROBAR PARA PERMISOS
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //gMap.setMyLocationEnabled(true);
            //gMap.getUiSettings().setMyLocationButtonEnabled(false);//Quita el boton cuanto encuentra la location
            /*  Cuando se actualice la señal toma 1 provider - el que se encuentre disponible  */
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER //Proveedor de location
                    , 0   //Minimo de Tiempo de actualizacion - milisegundos (Para tema de bateria consume mucha energia al hacer peticiones constantemente).
                    , 0    //Minimo de distancia en actualizarse - metros
                    , this);//Pide las actualizaciones de la localizacion
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    this);//Pide las actualizaciones de la localizacion

        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, LOCATION_HARDWARE}, 100);
        }
    }

    //Dibuja un circulo segun la posicion de la persona
    private void dibujarRadio(Location location) {

        Circle circle;
        circle = gMap.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(),location.getLongitude()))
                        .radius(80)
                        .fillColor(Color.TRANSPARENT)
                        .strokeWidth(1)
                        .strokeColor(Color.BLUE)
        );

    }
    //PROBAR PARA PERMISOS

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // gMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER //Proveedor de location
                        , 0   //Minimo de Tiempo de actualizacion - milisegundos (Para tema de bateria consume mucha energia al hacer peticiones constantemente).
                        , 0    //Minimo de distancia en actualizarse - metros
                        , this);//Pide las actualizaciones de la localizacion
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);//Pide las actualizaciones de la localizacion

            } else {
                Toast.makeText(getContext(), "Permiso no aceptado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Verifica si esta activado el GPS (Ubicacion)
    private boolean validarGPS() {
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            //Si no tiene señal = 0
            if (gpsSignal == 0) {
                //EL GPS NO ESTA ACTIVADO
                return false;
            } else {
                return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Cuadro de dialogo que menciona activar señal de gps
    private void mostrarInformacionAlerta() {
        new AlertDialog.Builder(getContext())
                .setTitle("Señal GPS")
                .setMessage("No tienes señal de GPS. Deseas activar la señal ahora?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent que nos lleva a una ventanada para activar el setting
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onClick(View view) {
        //Cuando haga clic en el FloatingActionButton lanzamos el metodo
        //SOLICITA GPS
        if (!this.validarGPS()) {//Si el gps no esta habilitado
            mostrarInformacionAlerta();
            ;
        } else {
            //Activar el boton para dar la localizacion
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null){ //Si no funciona el proveedor de gps
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            currentLocation = location;

            if (currentLocation != null){//Si no es nulo tomamos el marcador si existe y lo actualizamos, si no lo creamos
                createOrUpdateMarkerByLocation(location);
                zoomToLocation(location);
            }
        }
    }

    private void createOrUpdateMarkerByLocation(Location location){
        if (marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).draggable(true));//Añadimos el marcador cada vez que actualice
        }else{
            marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        dibujarRadio(location);
    }

    private void zoomToLocation(Location location){
        cameraZoom = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(15) //limit --> 21
                .bearing(0) // 0 --> 365º
                .tilt(30)   //limit 90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraZoom));
    }

    //Pide las actualizaciones de la localizacion
    //cada vez que cambia envia la Location

    //Cada vez que cambia la Location
    @Override
    public void onLocationChanged(Location location) {
        //Manera correcta de actualizar la location del marker
        /*Toast.makeText(getContext(),"Changed! -> " + location.getProvider(),Toast.LENGTH_SHORT).show();//Muestra el proveedor de location
        createOrUpdateMarkerByLocation(location);
        dibujarRadio(location);*/
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //Cuando el usuario de clic a un POI (Punto de interes: parques, hoteles, etc.)
    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getContext(),"Presiono el Punto de interes: \n" + poi.name,Toast.LENGTH_LONG).show();
        /*Toast.makeText(getContext(),"Presiono: " + poi.name + "\nId Lugar: " + poi.placeId
                                                +"\n Latitud: " + poi.latLng.latitude
                                                +"\n Longitud: " + poi.latLng.longitude,Toast.LENGTH_LONG).show();*/
    }
}
