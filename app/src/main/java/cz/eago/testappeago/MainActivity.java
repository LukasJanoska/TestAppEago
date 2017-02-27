package cz.eago.testappeago;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
    LocationManager locationManager;
	private static final int MY_PERMISSIONS_REQUEST = 0;
    public static final String mBroadcastStringAction = "cz.eago.broadcast.string";

    boolean permissionCheck = false;
    boolean firstRun = true;

    MainService mainService;
    boolean mBounded;
    private IntentFilter mIntentFilter;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if(savedInstanceState==null) {
            init();
        }
    }

    public void init() {

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            addSplashScreenFragment();
            startService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            permissionCheck = true;
        }

        firstRun = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstRun", firstRun);
    }

    private void addSplashScreenFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, SplashScreenFragment.newInstance(SplashScreenFragment.SplashScreenFragmnetTag))
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addSplashScreenFragment();
                    startService();
                }else{
                    findViewById(R.id.permissionsDenied).setVisibility(View.VISIBLE);
                }
            }
        }
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


    private Boolean exit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exit) {
                finish();
                stopService(new Intent(this, MainService.class));
            } else {
                String str = getString(R.string.backButtonStr);
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
        return true;
    }

    private void isWifiAvailable(NetworkInfo networkInfo) {

        if (networkInfo != null) { // connected to the internet
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(getApplicationContext(), networkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(getApplicationContext(), networkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mainService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            MainService.LocalBinder mLocalBinder = (MainService.LocalBinder)service;
            mainService = mLocalBinder.getServiceInstance();
            changeFragmentTextView(mainService.getCurrentLocationStr());

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        registerReceiver(mReceiver, mIntentFilter);
    }

    private void startService(){
        startService(new Intent(this, MainService.class));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastStringAction)) {
                Toast.makeText(getApplicationContext(), "asdasad", Toast.LENGTH_LONG).show();
                changeFragmentTextView(intent.getStringExtra("Data") + "\n\n");

                //pridat tlacitko do menu

            }
        }
    };

    /*public void updateFragment(String data) {
        AddressFragment fragment_obj =  (AddressFragment) getSupportFragmentManager().findFragmentByTag(AddressFragment.AddressFragmnetTag);
        fragment_obj.setLocationText(data);
    }*/

    public void changeFragmentTextView(String s) {
        android.support.v4.app.Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(frag.getView().findViewById(R.id.positionTxt) != null) {
            ((TextView) frag.getView().findViewById(R.id.positionTxt)).setText(s);
        }
    }

}
