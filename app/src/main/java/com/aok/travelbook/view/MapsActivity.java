package com.aok.travelbook.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aok.travelbook.R;
import com.aok.travelbook.databinding.ActivityMapsBinding;
import com.aok.travelbook.model.Place;
import com.aok.travelbook.roomdatabase.PlaceDAO;
import com.aok.travelbook.roomdatabase.PlaceDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap appMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    boolean info;
    PlaceDatabase placeDatabase;
    PlaceDAO placeDAO;
    Double selectedLatitude;
    Double selectedLongitude;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Place selectedPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

       /* sharedPreferences = MapsActivity.this.getSharedPreferences("com.aok.travelbook", MODE_PRIVATE);
        info = sharedPreferences.getBoolean("info", false);*/

        placeDatabase = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "Place")
                //.allowM ainThreadQueries()
                .build();
        placeDAO = placeDatabase.placeDAO();

        selectedLatitude = 0.0;
        selectedLongitude = 0.0;

        binding.btnSave.setEnabled(false);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        appMap = googleMap;
        appMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String strInfo = intent.getStringExtra("info");

        if(strInfo.matches("new")){

            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.GONE);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
               /* if(!info){
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    appMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    sharedPreferences.edit().putBoolean("info", true).apply();
                }*/
                }
            };

            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.getRoot(),"Haritalara erişim için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }else{
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastLocation != null){

                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    appMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));

                }
            }

            appMap.setMyLocationEnabled(true); // Haritada kullanıcının mevcut konumunu gösteren mavi daire


        } else {

            appMap.clear();
            selectedPlace = (Place) intent.getSerializableExtra("place");

            LatLng latLng = new LatLng(selectedPlace.latitude, selectedPlace.longitude);
            appMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));

            appMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

            binding.placeText.setText(selectedPlace.name);
            binding.btnSave.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.VISIBLE);

        }





    }

    private void registerLauncher(){

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if(result){

                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                    }

                }else {

                    Toast.makeText(MapsActivity.this, "İzin Verilmedi", Toast.LENGTH_LONG);

                }

            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        appMap.clear();
        appMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

        binding.btnSave.setEnabled(true);

    }

    public void save(View view){

        Place place = new Place();
        place.name = binding.placeText.getText().toString();
        place.latitude =  selectedLatitude;
        place.longitude = selectedLongitude;

        //placeDAO.insert(place).subscribeOn(Schedulers.io()).subscribe();

        //Disposable
        compositeDisposable.add(placeDAO.insert(place)
                .subscribeOn(Schedulers.io())  // IO Thread içinde yapmasını söyledik
                .observeOn(AndroidSchedulers.mainThread())  // Main Thread içinde gözlemle
                .subscribe(MapsActivity.this::handleResponse));  // MapsActivity' den referans al demiş olduk

    }

    public void delete(View view){

        if(selectedPlace != null){

        compositeDisposable.add(placeDAO.delete(selectedPlace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MapsActivity.this::handleResponse));

         }
    }

    private void handleResponse(){
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}