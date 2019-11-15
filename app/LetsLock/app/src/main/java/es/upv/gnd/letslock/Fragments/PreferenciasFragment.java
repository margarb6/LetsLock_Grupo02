package es.upv.gnd.letslock.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import es.upv.gnd.letslock.AcercaDeActivity;
import es.upv.gnd.letslock.R;

public class PreferenciasFragment extends PreferenceFragment {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

        Preference button = findPreference(getString(R.string.acerca_de));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), AcercaDeActivity.class);
                startActivity(i);
                return true;
            }
        });
        Preference button2 = findPreference(getString(R.string.pagina_web));
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.serviciotecnicooficial.saunierduval.es//"));
                startActivity(intent);

                return true;
            }
        });
        Preference button3 = findPreference(getString(R.string.email));
        button3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "asunto");
                intent.putExtra(Intent.EXTRA_TEXT, "texto del correo");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jtomas@upv.es"});
                startActivity(intent);


                return true;
            }
        });
        Preference button4 = findPreference(getString(R.string.llamar_telefono));
        button4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:962849347"));
                startActivity(intent);
                return true;
            }
        });
        Preference button5 = findPreference(getString(R.string.localizacion));
        button5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:41.656313,-0.877351"));
                startActivity(intent);
                return true;
            }
        });

    }


}