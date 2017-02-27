package cz.eago.testappeago;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.BIND_AUTO_CREATE;


public class AddressFragment extends Fragment {

    public static String AddressFragmnetTag = "ADDRESS_FRAGMENT_TAG";
    public static final String mBroadcastStringAction = "cz.eago.broadcast.string";

    public TextView text;

    MainService mainService;
    boolean mBounded;
    private IntentFilter mIntentFilter;

    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miSettings:

                text.setText(mainService.getCurrentAddress());
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_address, container, false);

        saveToPrefs("run", true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(getContext(), MainService.class);
        getActivity().bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mainService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            MainService.LocalBinder mLocalBinder = (MainService.LocalBinder)service;
            mainService = mLocalBinder.getServiceInstance();
            //setLocationText(mainService.getCurrentLocationStr());
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text = (TextView) getActivity().findViewById(R.id.textAddress);
    }

    public static AddressFragment newInstance(String someTitle) {
        AddressFragment addressFragment = new AddressFragment();
        return addressFragment;
    }

    public void saveToPrefs(String key, boolean value) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(key, value)
                .apply();
    }


}
