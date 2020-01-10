package es.upv.gnd.letslock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import es.upv.gnd.letslock.PreferenciasActivity;
import es.upv.gnd.letslock.R;

public abstract class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getActivity();
        addPreferencesFromResource(R.xml.preferencias);
        SwitchPreference dayNightSwitch = (SwitchPreference) findPreference(getString(R.string.modo_noche));
        dayNightSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean activado = (boolean) newValue;

                if (!activado) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                getActivity().finish();
                startActivity(new Intent(getActivity(), PreferenciasActivity.class));

                return true;
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            int myColor = Color.parseColor("#3C3C3C");
            view.setBackgroundColor(myColor);

        }else{
            int myColor = Color.parseColor("#FFFFFF");
            view.setBackgroundColor(myColor);
        }
        return view;
    }

}
