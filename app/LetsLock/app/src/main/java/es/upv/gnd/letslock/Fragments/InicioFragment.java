package es.upv.gnd.letslock.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;

import es.upv.gnd.letslock.R;

public class InicioFragment extends Fragment {

    View vista;
    ImageView imageView;
    boolean image1Displaying = true;
    Toast toast;
    LottieAnimationView lottieAnimationView;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_inicio, container, false);


        imageView = vista.findViewById(R.id.puerta);
        lottieAnimationView = vista.findViewById(R.id.animation_view);
        lottieAnimationView.setSpeed((float) 0.5);
<<<<<<< HEAD
        lottieAnimationView.addValueCallback(
                new KeyPath("Door frame", "Rectangle 2"),
                LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
                    }
                }
        );
=======

>>>>>>> fabio
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
}