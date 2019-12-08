package es.upv.gnd.letslock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.upv.gnd.letslock.HistorialTimbreActivity;
import es.upv.gnd.letslock.R;

public class TimbreFragment extends Fragment {

    View vista;
    TextView nadie_llama;
    TextView pregunta;
    Button si;
    Button no;
    Button historial;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_timbre, container, false);

        nadie_llama = vista.findViewById(R.id.nadie_llama);
        pregunta = vista.findViewById(R.id.timbre_pregunta);
        si = vista.findViewById(R.id.timbre_boton_si);
        no = vista.findViewById(R.id.timbre_boton_no);
        historial = vista.findViewById(R.id.boton_historial);

        nadie_llama.setVisibility(View.INVISIBLE);
        pregunta.setVisibility(View.VISIBLE);
        si.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);

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

                nadie_llama.setVisibility(View.VISIBLE);
                pregunta.setVisibility(View.INVISIBLE);
                si.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);

            }
        },30000);


        return vista;
    }
}
