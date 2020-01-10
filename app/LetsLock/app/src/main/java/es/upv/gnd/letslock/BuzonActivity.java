package es.upv.gnd.letslock;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;
import es.upv.gnd.letslock.bbdd.Notificacion;
import es.upv.gnd.letslock.bbdd.Notificaciones;

import static android.app.Service.START_STICKY;
import static es.upv.gnd.letslock.NotificationActivity.CHANNEL_1_ID;

public class BuzonActivity extends Service {

    //private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idCasa;
    private double distancia;
    private NotificationManagerCompat notificationManager;
    int contador;

    MediaPlayer reproductor;
    @Override public void onCreate() {
        NotificationActivity notificationActivity= new NotificationActivity();
        notificationActivity.createNotificationChannels(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        Log.d("BUZON","funciona");

    }
    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle("Buzon")
                .setContentText("Servicio de buzon activado en la aplicacion")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(2, notification);

        Log.d("BUZON","funciona");

        db.collection("Datos").document("Datos").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    distancia = task.getResult().getDouble("distancia");
                    if (distancia < 10 && contador == 0) {
                        /*Log.d("BUZON","funciona");
                        contador = 1;
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                .setContentTitle("Buzon")
                                .setContentText("Alguien ha dejado una carta")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(1, notification);

                        Casas casaBD = new Casas();
                        casaBD.getCasa(getApplicationContext(), new CasasCallback() {
                            @Override
                            public void getCasasCallback(Casa casa) {

                                Notificaciones notificacionesBD = new Notificaciones();
                                notificacionesBD.setNotificaciones(new Notificacion(UUID.randomUUID().toString(), "buzon", new Date().getTime() + 3600 * 1000, idCasa, casa.getIdUsuarios(), 0));
                                Log.d("CASA","funciona?"+idCasa);
                            }
                        });*/

                        //nombreT.setText(name);
                        // Log.d("DISTANCIA", distancia.toString());
                    }
                    if(distancia>10) {
                        contador = 0;
                    }
                }
            }

        });

        /*notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this, CANAL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Título")
                        .setContentText("Texto de la notificación.")
                       // .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                //android.R.drawable.ic_media_play))
                        .setWhen(System.currentTimeMillis() + 1000 * 60 * 60)
                        .setContentInfo("más info")
                        .setTicker("Texto en barra de estado");

        notificationManager.notify(NOTIFICACION_ID, notificacion.build());

        PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), 0);
        notificacion.setContentIntent(intencionPendiente);*/



        return START_STICKY;
    }


    @Override public void onDestroy() {
        notificationManager.cancel(NOTIFICACION_ID);
    }
    @Override public IBinder onBind(Intent intencion) {
        return null;
    }
}

