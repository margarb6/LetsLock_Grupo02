package es.upv.gnd.letslock.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.CallLog;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;

import android.preference.SwitchPreference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Provider;
import java.util.Date;
import java.util.UUID;

import es.upv.gnd.letslock.AcercaDeActivity;
import es.upv.gnd.letslock.BuzonActivity;
import es.upv.gnd.letslock.FormularioActivity;
import es.upv.gnd.letslock.MainActivity;
import es.upv.gnd.letslock.NotificationActivity;
import es.upv.gnd.letslock.PreferenciasActivity;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.ServicioMusica;
import es.upv.gnd.letslock.VideosActivity;
import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;
import es.upv.gnd.letslock.bbdd.Notificacion;
import es.upv.gnd.letslock.bbdd.Notificaciones;
import io.grpc.CallOptions;
import io.grpc.internal.BackoffPolicy;

import static android.provider.CallLog.*;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static es.upv.gnd.letslock.NotificationActivity.CHANNEL_1_ID;




public class PreferenciasFragment extends PreferenceFragment {

    /*public static class PrefsFragment extends PreferenceFragment {
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
    }*/

    private View vista;
    private static final int SOLICITUD_PERMISO_ACTION_CALL = 0;

    private EditText edit_nombre;
    private EditText edit_asunto;
    private EditText edit_mensaje;
    public static SharedPreferences mTheme;
    MediaPlayer reproductor;

    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idCasa;
    private double distancia;
    private NotificationManagerCompat notificationManager;
    int contador;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

        NotificationActivity notificationActivity= new NotificationActivity();
        notificationActivity.createNotificationChannels(getContext());
        notificationManager = NotificationManagerCompat.from(getContext());

        //recibirCarta();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reproductor = MediaPlayer.create(getContext(), R.raw.faded);
        }*/

        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment() {
                    @Override
                    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

                    }
                })
                .commit();*/

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

        final Preference checkbox1 = findPreference(getString(R.string.musica));
        checkbox1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean activado = (boolean) newValue;

                if(!activado) {
                    getActivity().stopService(new Intent(getActivity(),
                            ServicioMusica.class));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "servicio de musica desactivado", Toast.LENGTH_LONG).show();
                    }

                }else{
                    getActivity().startService(new Intent(getActivity(),
                            ServicioMusica.class));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "servicio de musica activado", Toast.LENGTH_LONG).show();
                    }

                }
                return true;
            }
        });

        final Preference checkbox2 = findPreference(getString(R.string.buzonn));
        checkbox2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean activado = (boolean) newValue;

                if(!activado) {
                    getActivity().stopService(new Intent(getActivity(),
                            BuzonActivity.class));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "Buzon inteligente desactivado", Toast.LENGTH_LONG).show();
                    }

                }else{
                    getActivity().startService(new Intent(getActivity(),
                            BuzonActivity.class));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "Buzon inteligente activado", Toast.LENGTH_LONG).show();
                    }

                }
                return true;
            }
        });

        Preference button = findPreference(getString(R.string.acerca_de));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), VideosActivity.class);
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

    }

    public void ponerMusica () {
        reproductor.start();
    }

    public void quitarMusica () {
        reproductor.stop();
        reproductor.release();
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

    public void recibirCarta() {
        Log.d("BUZON","funciona");

        db.collection("Datos").document("Datos").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    distancia = task.getResult().getDouble("distancia");
                    if (distancia < 10 && contador == 0) {
                        Log.d("BUZON","funciona");
                        contador = 1;
                        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                .setContentTitle("Buzon")
                                .setContentText("Alguien ha dejado una carta")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(1, notification);

                        Casas casaBD = new Casas();
                        casaBD.getCasa(getContext(), new CasasCallback() {
                            @Override
                            public void getCasasCallback(Casa casa) {

                                Notificaciones notificacionesBD = new Notificaciones();
                                notificacionesBD.setNotificaciones(new Notificacion(UUID.randomUUID().toString(), "buzon", new Date().getTime() + 3600 * 1000, idCasa, casa.getIdUsuarios(), 0));
                                Log.d("CASA","funciona?"+idCasa);
                            }
                        });

                        //nombreT.setText(name);
                        // Log.d("DISTANCIA", distancia.toString());
                    }
                    if(distancia>10) {
                        contador = 0;
                    }
                }
            }

        });
    }

    public void enviarCorreo() {
        String listaCorreos = PreferenciasActivity.edit_nombre.getText().toString();
        String[] correos = listaCorreos.split(",");
        //fabio@gmail.com, david@gmail.com
        String asunto = PreferenciasActivity.edit_asunto.getText().toString();
        String mensaje = PreferenciasActivity.edit_mensaje.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Elige una forma"));
    }


    private void llamar() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, SOLICITUD_PERMISO_ACTION_CALL);
        } else {
            String dial = "tel:664410457";
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_ACTION_CALL) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            } else {
                Toast.makeText(getActivity(), "Sin el permiso, no puedo realizar la llamada", Toast.LENGTH_SHORT).show();
            }
        }
    }


}