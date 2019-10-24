package es.upv.gnd.letslock.ui.inicio;

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

public class InicioFragment extends Fragment {

    private InicioViewModel InicioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InicioViewModel =
                ViewModelProviders.of(this).get(InicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inicio, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        InicioViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}