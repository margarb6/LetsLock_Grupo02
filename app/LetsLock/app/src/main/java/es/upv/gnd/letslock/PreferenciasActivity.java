package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatDelegate;

import es.upv.gnd.letslock.Fragments.PreferenciasFragment;

public class PreferenciasActivity extends Activity {

    private Activity actividad;
    private View vista;
    private static final int SOLICITUD_PERMISO_WRITE_CALL_LOG = 0;

    static public EditText edit_nombre;
    static public EditText edit_asunto;
    static public EditText edit_mensaje;
    static public SharedPreferences onOffModoNoche;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changePreferencesTheme();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();

    }

    public void mostrarPreferencias() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: " + pref.getBoolean("musica", true)
                + ", modo noche: " + pref.getBoolean("modo_noche", false);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, VideosActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, AnimacionPreferenciasActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void changePreferencesTheme() {
        SharedPreferences preferenciaNoche = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        boolean estaModoNoche = preferenciaNoche.getBoolean("modo_noche", true);

        if (estaModoNoche) {
            setTheme(R.style.DarkThemePreferences);
        } else {
            setTheme(R.style.LightTheme);
        }

    }


}
