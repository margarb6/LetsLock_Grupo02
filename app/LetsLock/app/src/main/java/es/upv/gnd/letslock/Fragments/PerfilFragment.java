package es.upv.gnd.letslock.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.List;

import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

public class PerfilFragment extends Fragment {

    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_perfil, container, false);

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        final TextView nombre = vista.findViewById(R.id.nombre);
        TextView email = vista.findViewById(R.id.correo);
        TextView proveedores = vista.findViewById(R.id.proveedores);
        TextView telefono = vista.findViewById(R.id.telefono);
        TextView iden = vista.findViewById(R.id.iden);
        final TextView permisos = vista.findViewById(R.id.permisos);
        final TextView pin= vista.findViewById(R.id.pin);

        //Establecemos a cada TextView el valor de firebase auth
        email.setText(usuario.getEmail());
        iden.setText(usuario.getUid());
        telefono.setText(usuario.getPhoneNumber());

        //Establecemos el proveedor
        List<? extends UserInfo> infos = usuario.getProviderData();
        for (UserInfo ui : infos) {
            if (ui.getProviderId() != "firebase") {
                proveedores.setText(ui.getProviderId());
            }
        }

        //Obtenemos la imagen del storage
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Obtenemos la imagen del storage
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ficheroRef = storageRef.child("Fotos_perfil/" + usuario.getUid());

        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");
                ImageView fotoContenedor = vista.findViewById(R.id.FotoPerfil);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), BitmapFactory.decodeFile(path));
                roundedDrawable.setCircular(true);
                fotoContenedor.setImageDrawable(roundedDrawable);
            }

         //Si no se puede obtener ponemos como predeterminada la del usuario de google
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (usuario.getPhotoUrl()!=null){
                    DescargarFoto fotoDes= new DescargarFoto(PerfilFragment.this.getActivity(),R.id.FotoPerfil);
                    fotoDes.execute(usuario.getPhotoUrl().toString());
                }
            }
        });

        //Obtenemos el nombre, los permisos del usuario y el pin de la bd.
        final Usuarios userBD = new Usuarios();
        userBD.getUsuario(new UsuariosCallback() {

            public void getUsuariosCallback(Usuario usuarioBD) {
                if (usuarioBD.isPermisos()) permisos.setText("Propietario de la casa");
                else permisos.setText("Habitante");
                nombre.setText(usuarioBD.getNombre());
                pin.setText(usuarioBD.getPin());
            }
        });
        return vista;
    }


}


