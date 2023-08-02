package com.aok.travelbook;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.aok.travelbook.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private ActivityMainBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;

    // GoogleMap'i uygulama içine entegre etmek için xml'e MapView bileşenini eklememiz gerek.
    // mapView.getMapAsync() metodunu kullanarak MapView etkin hale geçer ve harita hazır olduğunda bir işaretçi ekler.
    // MapView yaşam döngüsüu yönetmek için onResume(), onPause(), OnDestroy() metotları kullanılır.
    // onResume(): Aktivitenin kullanıcıya görünür hale geldiğinde çağrılır. Bu metodun içinde, mapView.onResume() çağrısı ile harita görüntülemesinin etkinleştirilmesi sağlanır. Bu, haritanın kullanıcı etkileşimine hazır hale getirilmesini sağlar.
    // onPause():  Aktivite kullanıcıya görünmez hale geldiğinde çağrılır. Bu metodun içinde, mapView.onPause() çağrısı ile harita görüntülemesinin durdurulması sağlanır. Bu, arka planda gereksiz kaynak kullanımını önler.
    // onDestroy(): Aktivite tamamen kapatıldığında veya yok edildiğinde çağrılır. Bu metodun içinde, mapView.onDestroy() çağrısı ile harita görüntülemesinin ve bağlantılarının serbest bırakılması sağlanır. Bu, bellek sızıntılarını önlemeye yardımcı olur.

    // onSaveInstanceState(): MapView öğesinin durumunu kaydederek, aktivite yeniden oluşturulduğunda haritanın korunmasını sağlar.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                // Hata vermemesi için type casting yapılmıştır.
                locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        System.out.println("Location: " + location.toString());
                    }
                    // Eğer hata alırsak onStatusChange() çağır.
                };

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                        Snackbar.make(binding.getRoot()," Haritaları görütüleyebilmek için izne ihtiyaç var.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            }
                        }).show();
                    }else{
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }



                LatLng teknokent = new LatLng(37.7387013, 29.0899161); // Teknokent koordinatları
                googleMap.addMarker(new MarkerOptions().position(teknokent).title("PAU Teknokent"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(teknokent,20));
            }
        });

        registerLauncher();
    }

   @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void registerLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // İzin verildi.

                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    }

                }else{
                    // izin verilmedi ve Bir Toast mesajı gösterdi ekranda...

                    Toast.makeText(MainActivity.this, "İzin Verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}