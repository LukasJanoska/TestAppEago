package cz.eago.testappeago;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends Activity {

    public static final String mBroadcastStringAction = "cz.eago.broadcast.string";
    private TextView mTextView;
    public TextView text;
    MainService mainService;
    boolean mBounded;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_address);

        mTextView = (TextView) findViewById(R.id.positionTxt);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
	}

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, MainService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(AddressActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mainService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(AddressActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            MainService.LocalBinder mLocalBinder = (MainService.LocalBinder)service;
            mainService = mLocalBinder.getServiceInstance();
            setLocationText(mainService.getCurrentLocationStr());
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
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastStringAction)) {
                setLocationText(intent.getStringExtra("Data") + "\n\n");
            }
        }
    };

    private void setLocationText(String text){
        mTextView.setText(text);
    }
}
