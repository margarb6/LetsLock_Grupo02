package es.upv.gnd.letslock.ui.plano;

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

public class PlanoFragment extends Fragment {

    private PlanoViewModel PlanoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanoViewModel =
                ViewModelProviders.of(this).get(PlanoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_plano, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        PlanoViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}