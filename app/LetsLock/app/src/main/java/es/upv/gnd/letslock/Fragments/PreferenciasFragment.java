package es.upv.gnd.letslock.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.CallLog;
import android.telecom.Call;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.security.Provider;

import es.upv.gnd.letslock.AcercaDeActivity;
import es.upv.gnd.letslock.ChatActivity;
import es.upv.gnd.letslock.FormularioActivity;
import es.upv.gnd.letslock.PreferenciasActivity;
import es.upv.gnd.letslock.R;
import io.grpc.CallOptions;
import io.grpc.internal.BackoffPolicy;

import static android.provider.CallLog.*;

public class PreferenciasFragment extends PreferenceFragment {

    private View vista;
    private static final int SOLICITUD_PERMISO_ACTION_CALL = 0;

    private EditText edit_nombre;
    private EditText edit_asunto;
    private EditText edit_mensaje;

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
                Intent i = new Intent(getActivity(), FormularioActivity.class);
                startActivity(i);

                return true;
            }
        });
        Preference button4 = findPreference(getString(R.string.llamar_telefono));
        button4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
             llamar();
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
        Preference button6 = findPreference(getString(R.string.contacto));
        button6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), ChatActivity.class));
                return true;
            }
        });
    }

    public void enviarCorreo() {
        String listaCorreos = PreferenciasActivity.edit_nombre.getText().toString();
        String [] correos = listaCorreos.split(",");
        //fabio@gmail.com, david@gmail.com
        String asunto = PreferenciasActivity.edit_asunto.getText().toString();
        String mensaje = PreferenciasActivity.edit_mensaje.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Elige una forma"));
    }


    private void llamar() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE}, SOLICITUD_PERMISO_ACTION_CALL);
        }else{
            String dial = "tel:664410457";
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
        }
    }

    /*void llamar() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)) {
            getActivity().getContentResolver().call(CallLog.Calls.CONTENT_URI,
                    "number='555555555'", null);
            Snackbar.make(vista, "Llamadas borradas del registro.",
                    Snackbar.LENGTH_SHORT).show();
        }else {
            solicitarPermiso(Manifest.permission.WRITE_CALL_LOG, "Sin el permiso"+
                            " administrar llamadas no puedo borrar llamadas del registro.",
                    SOLICITUD_PERMISO_WRITE_CALL_LOG, getActivity());
        }

    }

    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }*/

    @Override public void onRequestPermissionsResult(int requestCode,
                                                     String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_ACTION_CALL) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            } else {
                Toast.makeText(getActivity(), "Sin el permiso, no puedo realizar la llamada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}