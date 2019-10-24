package es.upv.gnd.letslock;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflador,
                                       ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_usuario,
                contenedor, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = (TextView) vista.findViewById(R.id.nombre);
        TextView email = (TextView) vista.findViewById(R.id.email);
        TextView proveedores = (TextView) vista.findViewById(R.id.proveedores);
        TextView telefono = (TextView) vista.findViewById(R.id.telefono);
        TextView iden = (TextView) vista.findViewById(R.id.iden);
        proveedores.setText(usuario.getProviders().toString());
        nombre.setText(usuario.getDisplayName());
        email.setText(usuario.getEmail());
        iden.setText(usuario.getUid());
        telefono.setText(usuario.getPhoneNumber());



        return vista;
    }
}
