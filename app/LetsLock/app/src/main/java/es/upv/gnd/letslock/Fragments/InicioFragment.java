package es.upv.gnd.letslock.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Random;
import java.util.UUID;


import es.upv.gnd.letslock.JavaMailAPI;
import es.upv.gnd.letslock.bbdd.Notificacion;
import es.upv.gnd.letslock.bbdd.Notificaciones;

import es.upv.gnd.letslock.NotificationActivity;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;

import static es.upv.gnd.letslock.NotificationActivity.CHANNEL_1_ID;

public class InicioFragment extends Fragment {

    View vista;
    ImageView imageView;
    boolean image1Displaying = true;
    Toast toast;
    LottieAnimationView lottieAnimationView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    LottieAnimationView lottieAnimationView2;
    Button enviar;
    TextView codigo_y_correo;
    int codigo = 0000;
    String mensaje_confirmacion;
    static EditText correo;
    Button boton_enviar;
    Button boton_cancelar;

    private boolean anonimo = false;
    private String idCasa;
    private NotificationManagerCompat notificationManager;


    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);
        if (prefs.contains("idCasa")) idCasa = prefs.getString("idCasa", "");

        if(!anonimo){

            vista = inflater.inflate(R.layout.fragment_inicio, container, false);

            NotificationActivity notificationActivity= new NotificationActivity();
            notificationActivity.createNotificationChannels(getContext());

            notificationManager = NotificationManagerCompat.from(getContext());
            imageView = vista.findViewById(R.id.puerta);
            lottieAnimationView = vista.findViewById(R.id.animation_view);
            lottieAnimationView.setSpeed((float) 0.5);
            lottieAnimationView2 = vista.findViewById(R.id.animation_view2);
            enviar = vista.findViewById(R.id.b_enviar);
            lottieAnimationView2.setVisibility(View.INVISIBLE);
            codigo_y_correo = vista.findViewById(R.id.codigo_y_correo);
            correo = vista.findViewById(R.id.correo_input);
            correo.setVisibility(View.INVISIBLE);
            boton_enviar = vista. findViewById(R.id.boton_enviar);
            boton_enviar.setVisibility(View.INVISIBLE);
            boton_cancelar = vista. findViewById(R.id.boton_cancelar);
            boton_cancelar.setVisibility(View.INVISIBLE);

            boton_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boton_enviar.setVisibility(View.INVISIBLE);
                            boton_cancelar.setVisibility(View.INVISIBLE);
                            enviar.setVisibility(View.INVISIBLE);
                            correo.setVisibility(View.INVISIBLE);
                            lottieAnimationView2.setVisibility(View.VISIBLE);
                            enviarCorreo();

                        }
                    },0);
                }
            });
            boton_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enviar.setVisibility(View.VISIBLE);
                            correo.setVisibility(View.INVISIBLE);
                            boton_enviar.setVisibility(View.INVISIBLE);
                            boton_cancelar.setVisibility(View.INVISIBLE);
                        }
                    },0);
                }
            });

            enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enviar.setVisibility(View.VISIBLE);
                            correo.setVisibility(View.VISIBLE);
                            boton_enviar.setVisibility(View.VISIBLE);
                            boton_cancelar.setVisibility(View.VISIBLE);
                        }
                    },0);
                }
            });

            lottieAnimationView2.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView2.setVisibility(View.INVISIBLE);
                    enviar.setVisibility(View.VISIBLE);
                    codigo_y_correo.setText(mensaje_confirmacion);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView.pauseAnimation();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (image1Displaying){

                                toast.makeText(getActivity(), "Abriendo puerta", Toast.LENGTH_SHORT).show();
                                imageView.setImageResource(R.drawable.imagen_animacion);
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
                                lottieAnimationView.playAnimation();
                                image1Displaying = false;
                            }else{
                                imageView.setImageResource(R.drawable.imagen_animacion);
                                float progress = lottieAnimationView.getProgress();
                                int duration = 3100;
                                ValueAnimator valueAnimator = ValueAnimator.ofFloat(-progress,0 )
                                        .setDuration(duration);

                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {

                                        lottieAnimationView.setProgress(Math.abs((float)animation.getAnimatedValue()));
                                    }

                                });
                                valueAnimator.start();
                                db.collection("Datos").document("Puerta").update("puerta",false);

                                toast.makeText(getActivity(), "Cerrando puerta", Toast.LENGTH_SHORT).show();
                                image1Displaying = true;
                            }

                        }
                    },0);

                    return false;
                }

            });

        }else {

            vista = inflater.inflate(R.layout.fragment_anonimo, container, false);
        }

        return vista;
    }

    public int randomCode (int codigo) {
        int min = 1001;
        int max = 9998;
        codigo = new Random().nextInt((max - min) + 1) + min;
        return codigo;
    }

    public String enviarCorreo() {
        if (correo.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Escribe un correo valido", Toast.LENGTH_SHORT).show();
            lottieAnimationView2.setVisibility(View.INVISIBLE);
            enviar.setVisibility(View.VISIBLE);
        }else {
            //super.enviarCorreo();
            String listaCorreos = correo.getText().toString().trim();
            //String [] correos = listaCorreos.split(",");
            //fabio@gmail.com, david@gmail.com
            String asunto = "Letslock: codigo de acceso";
            int codigo_enviado = randomCode(codigo);
            mensaje_confirmacion = "El codigo "+codigo_enviado+ " ha sido enviado a "+listaCorreos;
            String mensaje = "Tu codigo de entrada es "+codigo_enviado;
            db.collection("Datos").document("Puerta").update("pinInvitado",String.valueOf(codigo_enviado));

            JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(), listaCorreos
                    ,asunto,mensaje);
            javaMailAPI.execute();
            lottieAnimationView2.playAnimation();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    codigo_y_correo.setText("");

                }
            },120000);

            Notification notification = new NotificationCompat.Builder(getContext(),CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                    .setContentTitle("Pin enviado")
                    .setContentText("Alguien ha enviado un pin para abrir la puerta")
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
                    notificacionesBD.setNotificaciones(new Notificacion(UUID.randomUUID().toString(), "solicitudPin", new Date().getTime() + 3600 * 1000, idCasa, casa.getIdUsuarios(), 0));
                }
            });



       /* Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Elige una forma"));*/

        }

        return mensaje_confirmacion;
    }
}