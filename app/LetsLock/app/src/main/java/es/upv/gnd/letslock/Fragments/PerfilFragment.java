package es.upv.gnd.letslock.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.R;

public class PerfilFragment extends Fragment {

    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_perfil, container, false);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        TextView nombre = vista.findViewById(R.id.nombre);
        TextView email = vista.findViewById(R.id.email);
        TextView proveedores = vista.findViewById(R.id.proveedores);
        TextView telefono = vista.findViewById(R.id.telefono);
        TextView iden = vista.findViewById(R.id.iden);

        nombre.setText(usuario.getDisplayName());
        email.setText(usuario.getEmail());
        iden.setText(usuario.getUid());
        telefono.setText(usuario.getPhoneNumber());

        DescargarFoto foto = new DescargarFoto(this, R.id.FotoPerfil);

        try {

            foto.execute(usuario.getPhotoUrl().toString());
        }
        catch (NullPointerException error){

            return vista;
        }

        return vista;
    }
}


