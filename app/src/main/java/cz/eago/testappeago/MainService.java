package cz.eago.testappeago;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainService extends Service implements LocationListener {

    private LocationManager locationManager;
    public Location currentLocation;
    public String actualLocationStr;

    public MainService() {
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
    }

    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MainService getServiceInstance() {
            return MainService.this;
        }
    }

    public String getCurrentAddress(){
        Geocoder geocoder;

        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        if(currentLocation == null){
            return actualLocationStr = "Nenalezena poloha";
        } else try {
            if(isOnline())
                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses != null) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            return address + " " + state + "\n " + country + " " + postalCode;
        }
        else
            return "Nelze získat adresu. Jste připojeni k internetu?";
    }

    private boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, " Service started", Toast.LENGTH_LONG).show();
        getLocation();
        return 0;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        actualLocationStr = "Latitude: " + location.getLatitude()
                + "\nLongitude: " + location.getLongitude();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(AddressActivity.mBroadcastStringAction);
        broadcastIntent.putExtra("Data", actualLocationStr);
        sendBroadcast(broadcastIntent);

        Log.i("location", actualLocationStr);
        Toast.makeText(getBaseContext(), "broadcast", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Location is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Location is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    public String getCurrentLocationStr(){
        if(actualLocationStr != null)
            return actualLocationStr;
        else
            return "";
    }
}
