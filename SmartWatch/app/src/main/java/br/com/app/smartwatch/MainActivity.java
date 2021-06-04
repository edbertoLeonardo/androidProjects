 package br.com.app.smartwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

 public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
             startListening();
         }
     }

     public void startListening(){
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
             locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
         }
     }

     public void updateLocationInfo(Location location) throws IOException {

         Log.i("Info ", location.toString());
         TextView latTextView = findViewById(R.id.latTextView);
         TextView longTextView = findViewById(R.id.longTextView);
         TextView altTextView = findViewById(R.id.altTextView);
         TextView accTextView = findViewById(R.id.accTextView);

         latTextView.setText("Latitude: " + location.getLatitude());
         longTextView.setText("Longitude: " + location.getLongitude());
         altTextView.setText("Altitude: " + location.getAltitude());
         accTextView.setText("Precisao: " + location.getAccuracy());

         Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

         String address = "Endereço não encontrado";
         List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
         if (listAddresses != null && listAddresses.size() > 0){

             Log.i("Informações do local", listAddresses.get(0).toString());

             address = "Endereço: \n";
             if (listAddresses.get(0).getSubThoroughfare() != null){
                 address += listAddresses.get(0).getSubThoroughfare() + " ";
             }

             if (listAddresses.get(0).getThoroughfare() != null){
                 address += listAddresses.get(0).getThoroughfare() + "\n";
             }

             if (listAddresses.get(0).getLocality() != null){
                 address += listAddresses.get(0).getLocality() + "\n";
             }

             if (listAddresses.get(0).getPostalCode() != null){
                 address += listAddresses.get(0).getPostalCode() + "\n";
             }

             if (listAddresses.get(0).getCountryName()!= null){
                 address += listAddresses.get(0).getCountryName() + "\n";
             }

         }

         TextView addressTextView = findViewById(R.id.addressTextView);
         addressTextView.setText(address);

     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    updateLocationInfo(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (Build.VERSION.SDK_INT < 23 ){
            startListening();

        }else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0 , 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null){
                    try {
                        updateLocationInfo(location);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}