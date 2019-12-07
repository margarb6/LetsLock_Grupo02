package es.upv.gnd.letslock.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;

import java.util.Random;

import es.upv.gnd.letslock.JavaMailAPI;
import es.upv.gnd.letslock.PreferenciasActivity;
import es.upv.gnd.letslock.R;

public class InicioFragment extends Fragment {

    View vista;
    ImageView imageView;
    boolean image1Displaying = true;
    Toast toast;
    LottieAnimationView lottieAnimationView;
    LottieAnimationView lottieAnimationView2;
    Button enviar;
    TextView codigo_y_correo;
    int codigo = 0000;
    String mensaje_confirmacion;
    static EditText correo;
    Button boton_enviar;
    Button boton_cancelar;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_inicio, container, false);


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
                        lottieAnimationView2.playAnimation();
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


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        codigo_y_correo.setText("");

                    }
                },120000);
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
                           toast.makeText(getActivity(), "Cerrando puerta", Toast.LENGTH_SHORT).show();
                           image1Displaying = true;
                       }

                   }
               },0);

                return false;
            }

        });
        return vista;
    }

    public int randomCode (int codigo) {
        int min = 1001;
        int max = 9998;
        codigo = new Random().nextInt((max - min) + 1) + min;
        return codigo;

    }

    public String enviarCorreo() {
        //super.enviarCorreo();
        String listaCorreos = correo.getText().toString().trim();
        String [] correos = listaCorreos.split(",");
        //fabio@gmail.com, david@gmail.com
        String asunto = "Letslock: codigo de acceso";
        int codigo_enviado = randomCode(codigo);
        mensaje_confirmacion = "El codigo "+codigo_enviado+ " ha sido enviado a "+listaCorreos;
        String mensaje = "Tu codigo de entrada es "+codigo_enviado;

        JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(), listaCorreos
                ,asunto,mensaje);
        javaMailAPI.execute();

       /* Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Elige una forma"));*/

        return mensaje_confirmacion;
    }

}