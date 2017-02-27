package cz.eago.testappeago;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    LocationManager locationManager;
	private static final int MY_PERMISSIONS_REQUEST = 0;

	final long Delay = 5000;

    Timer RunSplash = new Timer();

    boolean permissionCheck = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
		setContentView(R.layout.activity_splash_screen);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startService();

        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST);
            permissionCheck = true;
        }
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                }else{
                    findViewById(R.id.ProgressBar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.permissionsDenied).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void startService(){
        checkLocation();
        startService(new Intent(this, MainService.class));

        RunSplash = new Timer();
        RunSplash.schedule(getShowSplashTimer(), Delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RunSplash.cancel();
        if(!permissionCheck)
            finish();
    }

    private TimerTask getShowSplashTimer(){
        return new TimerTask() {
            @Override
            public void run() {
                finish();

                Intent myIntent = new Intent(SplashScreenActivity.this, AddressActivity.class);
                startActivity(myIntent);
            }
        };
    }

    protected boolean enabled = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return enabled ?
                super.dispatchTouchEvent(ev) :
                true;
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            Toast.makeText(getBaseContext(), "Please Enable Location ", Toast.LENGTH_SHORT).show();
                    //showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    /* private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }*/
}
