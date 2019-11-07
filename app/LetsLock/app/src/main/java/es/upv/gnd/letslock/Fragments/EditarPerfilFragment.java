package es.upv.gnd.letslock.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.TabsActivity;

public class EditarPerfilFragment extends Fragment {

    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_usuario_editar, container, false);

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        final TextView nombre = vista.findViewById(R.id.nombre);
        final TextView email = vista.findViewById(R.id.email);
        email.setText(usuario.getEmail());
        nombre.setText(usuario.getDisplayName());

        Button guardar = vista.findViewById(R.id.button);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmación");
                builder.setMessage("¿Está seguro de cambiar su correo y nombre?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nombre.getText().toString())
                                //.setPhotoUri(Uri.parse("https://www.ejemplo.com/usuario/foto.jpg"))
                                .build();
                        usuario.updateProfile(perfil).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent intent = new Intent(getActivity(), TabsActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });

                builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
            }
        });

        DescargarFoto foto = new DescargarFoto(this, R.id.FotoEditar);

        try {

            foto.execute(usuario.getPhotoUrl().toString());

        } catch (NullPointerException error) {

            return vista;
        }

        return vista;
    }
}