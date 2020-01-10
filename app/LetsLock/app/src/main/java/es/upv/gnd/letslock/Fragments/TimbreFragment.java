package es.upv.gnd.letslock.Fragments;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import es.upv.gnd.letslock.HistorialTimbreActivity;

import com.bumptech.glide.Glide;
import com.example.serpumar.comun.Datos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import es.upv.gnd.letslock.NotificationActivity;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;
import es.upv.gnd.letslock.bbdd.Notificacion;
import es.upv.gnd.letslock.bbdd.Notificaciones;

import static es.upv.gnd.letslock.NotificationActivity.CHANNEL_1_ID;

import static com.example.serpumar.comun.Mqtt.broker;
import static com.example.serpumar.comun.Mqtt.clientId;
import static com.example.serpumar.comun.Mqtt.qos;
import static com.example.serpumar.comun.Mqtt.topicRoot;

public class TimbreFragment extends Fragment {

    View vista;
    TextView nadie_llama;
    TextView pregunta;
    ImageView imagen;
    Button si;
    Button no;
    Button historial;
    LottieAnimationView lottieAnimationView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "PILOTES";
    private StorageReference storageRef;

    private boolean anonimo = false;
    private String idCasa;
    private NotificationManagerCompat notificationManager;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if (!anonimo) {

            vista = inflater.inflate(R.layout.fragment_timbre, container, false);


            NotificationActivity notificationActivity= new NotificationActivity();
            notificationActivity.createNotificationChannels(getContext());

            notificationManager = NotificationManagerCompat.from(getContext());

            nadie_llama = vista.findViewById(R.id.nadie_llama);
            pregunta = vista.findViewById(R.id.timbre_pregunta);
            imagen = vista.findViewById(R.id.imagen_timbre);
            si = vista.findViewById(R.id.timbre_boton_si);
            no = vista.findViewById(R.id.timbre_boton_no);
            historial = vista.findViewById(R.id.boton_historial);
            lottieAnimationView = vista.findViewById(R.id.animation_view3);

            nadie_llama.setVisibility(View.INVISIBLE);
            pregunta.setVisibility(View.VISIBLE);
            si.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.INVISIBLE);

            si.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Datos").document("Puerta").update("puerta",true);
                    Notification notification = new NotificationCompat.Builder(getContext(),CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                            .setContentTitle("Puerta abierta")
                            .setContentText("Alguien ha abierto la puerta")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(1,notification);
                    Casas casaBD= new Casas();
                    casaBD.getCasa(getContext(), new CasasCallback() {
                        @Override
                        public void getCasasCallback(Casa casa) {

                            Notificaciones notificacionesBD = new Notificaciones();
                            notificacionesBD.setNotificaciones(new Notificacion(UUID.randomUUID().toString(), "llamanPuerta", new Date().getTime() + 3600 * 1000, idCasa, casa.getIdUsuarios(), 0));
                        }
                    });
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Datos").document("Puerta").update("puerta", false);
                }
            });

            historial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), HistorialTimbreActivity.class);
                    startActivity(i);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    storageRef = FirebaseStorage.getInstance().getReference();

                }
            }, 30000);

            storageRef = FirebaseStorage.getInstance().

                    getReference();


            FirebaseFirestore ff = FirebaseFirestore.getInstance();

            ff.collection("imagenes_timbre").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    } else if (ultimaFoto(documentSnapshots) != null) {
                        // Convert the whole Query Snapshot to a list
                        // of objects directly! No need to fetch each
                        // document.

                        Log.d(TAG, "onSuccess: " + documentSnapshots.getDocuments().toString());
                        Glide.with(getContext())
                                .load(ultimaFoto(documentSnapshots))
                                .into(imagen);
                    } else {
                        imagen.setImageResource(R.drawable.applogo);
                    }
                }
            });
        } else {
            vista = inflater.inflate(R.layout.fragment_anonimo, container, false);
        }

        //bajarFichero();
        return vista;
    }

    private URL ultimaFoto(QuerySnapshot qs) {

        long tiempo = 0;
        long cincoMin = 5 * 60 * 1000;
        long actualLong = System.currentTimeMillis();

        URL url = null;
        DocumentSnapshot ds = null;
        for (DocumentSnapshot docS : qs.getDocuments()) {
            if (docS.getLong("tiempo") > tiempo) {
                tiempo = docS.getLong("tiempo");
                ds = docS;
                Log.e("MARTA", "Tengo la foto mas actual");
                if ((actualLong - tiempo) < cincoMin) {
                    Log.e("MARTA", "Tengo una foto de hace menos de  5 mins");

                    try {
                        url = new URL(ds.getString("url"));
                        Log.e("URL", ds.getString("url"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return url;
    }

    private void bajarFichero() {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("imagenes_timbre/");
        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");
                imagen = vista.findViewById(R.id.imagen_timbre);
                imagen.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });

        new Runnable() {
            @Override
            public void run() {
                //client.set
            }
        }.run();
    }
}
