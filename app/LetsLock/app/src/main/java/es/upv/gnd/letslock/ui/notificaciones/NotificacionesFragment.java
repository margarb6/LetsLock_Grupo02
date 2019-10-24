package es.upv.gnd.letslock.ui.notificaciones;

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

public class NotificacionesFragment extends Fragment {

    private NotificacionesViewModel NotificacionesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
<<<<<<< HEAD:app/LetsLock/app/src/main/java/es/upv/gnd/letslock/ui/gallery/GalleryFragment.java
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
=======
        NotificacionesViewModel =
                ViewModelProviders.of(this).get(NotificacionesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inicio, container, false);
>>>>>>> develop:app/LetsLock/app/src/main/java/es/upv/gnd/letslock/ui/notificaciones/NotificacionesFragment.java
        final TextView textView = root.findViewById(R.id.text_gallery);
        NotificacionesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}