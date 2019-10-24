package es.upv.gnd.letslock.ui.cerrar_sesion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import es.upv.gnd.letslock.R;

public class CerrarSesionFragment extends Fragment {

    private CerrarSesionViewModel CerrarSesionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CerrarSesionViewModel =
                ViewModelProviders.of(this).get(CerrarSesionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cerrar_sesion, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        CerrarSesionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }


}