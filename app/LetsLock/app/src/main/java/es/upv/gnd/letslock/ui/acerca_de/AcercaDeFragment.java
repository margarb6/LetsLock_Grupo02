package es.upv.gnd.letslock.ui.acerca_de;

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

public class AcercaDeFragment extends Fragment {

    private AcercaDeViewModel AcercaDeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AcercaDeViewModel =
                ViewModelProviders.of(this).get(AcercaDeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_acerca_de, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        AcercaDeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}