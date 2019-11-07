package es.upv.gnd.letslock.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.upv.gnd.letslock.R;

public class InicioFragment extends Fragment {

    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_inicio, container, false);
        return vista;
    }
}