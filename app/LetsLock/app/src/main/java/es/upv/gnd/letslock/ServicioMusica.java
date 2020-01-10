package es.upv.gnd.letslock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static es.upv.gnd.letslock.NotificationActivity.CHANNEL_1_ID;

public class ServicioMusica extends Service {

    //private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    private NotificationManagerCompat notificationManager;

    MediaPlayer reproductor;
    @Override public void onCreate() {
        NotificationActivity notificationActivity= new NotificationActivity();
        notificationActivity.createNotificationChannels(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(getApplicationContext());


        reproductor = MediaPlayer.create(this, R.raw.faded);
    }
    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {


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

        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle("Musica activada")
                .setContentText("Servicio de musica activado en la aplicacion")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1,notification);

       // reproductor.seekTo(length);
        reproductor.start();
        reproductor.setLooping(true);
        return START_STICKY;
    }
    @Override public void onDestroy() {

        reproductor.pause();
        double length = reproductor.getCurrentPosition();
        notificationManager.cancel(NOTIFICACION_ID);
    }
    @Override public IBinder onBind(Intent intencion) {
        return null;
    }
}