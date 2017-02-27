package cz.eago.testappeago;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AddressFragment extends Fragment {

    public static String AddressFragmnetTag = "ADDRESS_FRAGMENT_TAG";

    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_address, container, false);

        saveToPrefs("run", true);

        return rootView;
    }

    public static AddressFragment newInstance(String someTitle) {
        AddressFragment splashScreenFragment = new AddressFragment();
        return splashScreenFragment;
    }

    public void saveToPrefs(String key, boolean value) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(key, value)
                .apply();
    }

    private void startService(){
        getActivity().startService(new Intent(getContext(), MainService.class));
    }

}
