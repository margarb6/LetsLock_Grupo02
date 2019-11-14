package es.upv.gnd.letslock.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.Fotos;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.TabsActivity;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

public class EditarPerfilFragment extends Fragment {

    View vista;
    private final String TAG = "EDITARPERFILFRAGMENT";

    //Variables globales
    private Uri foto;
    String id = "";

    //Inicializamos firebase auth y firestore
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    Usuarios userBD = new Usuarios();

    //Inicializamos el storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    //Request code de las fotos
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_usuario_editar, container, false);

        final TextView nombre = vista.findViewById(R.id.nombre);
        final RadioButton propietario = vista.findViewById(R.id.propietario);
        final RadioButton habitante = vista.findViewById(R.id.habitante);

        //Establece los valores firestore en el TextView y el RadioButton
        userBD.getUsuario(new UsuariosCallback() {
            public void getUsuariosCallback(Usuario usuarioBD) {
                nombre.setText(usuarioBD.getNombre());
                id = usuarioBD.getId();
                if (usuarioBD.isPermisos()) propietario.setChecked(true);
                else habitante.setChecked(true);
            }
        });

        //Obtenemos la imagen del storage
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("Fotos_perfil/" + usuario.getUid());

        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");
                ImageView fotoContenedor = vista.findViewById(R.id.FotoEditar);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), BitmapFactory.decodeFile(path));
                roundedDrawable.setCircular(true);
                fotoContenedor.setImageDrawable(roundedDrawable);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (usuario.getPhotoUrl() != null) {
                    DescargarFoto fotoDes = new DescargarFoto(EditarPerfilFragment.this, R.id.FotoEditar);
                    fotoDes.execute(usuario.getPhotoUrl().toString());
                    Log.e("Almacenamiento", "ERROR: bajando fichero");
                }
            }
        });

        //Añadimos un AlertDialog en el onclick de la imagen
        final Fotos fotos = new Fotos(getActivity());
        vista.findViewById(R.id.FotoEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Método de cambiar la foto");
                /*builder.setNeutralButton("Foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        foto = fotos.tomarFoto(RESULTADO_FOTO);
                    }
                });*/
                builder.setNegativeButton("Galería", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fotos.ponerDeGaleria(RESULTADO_GALERIA);
                    }
                });
                builder.show();
            }
        });

        //Añadimos un AlertDialog para guardar los datos en firestore y el storage
        Button guardar = vista.findViewById(R.id.button);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmación");
                builder.setMessage("¿Está seguro de cambiar su nombre, permisos y foto?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean permisos = false;
                        if (propietario.isChecked()) permisos = true;

                        Usuario usuarioDefinitivo = new Usuario(nombre.getText().toString(), id, permisos);
                        userBD.setUsuario(usuarioDefinitivo);
                        subirFoto();
                    }
                });
                builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
        return vista;
    }

    public void subirFoto() {

        //Buscamos el fichero y subimos la nueva foto
        SharedPreferences prefs = getActivity().getSharedPreferences("Foto_perfil", Context.MODE_PRIVATE);
        if (prefs.contains("image")) foto = Uri.parse(prefs.getString("image", "null"));
        StorageReference ficheroRef = storageRef.child("Fotos_perfil/" + usuario.getUid());
        if (foto != null) {
            ficheroRef.putFile(foto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Almacenamiento", "Fichero subido");
                    //Cambiamos la vista
                    Intent intent = new Intent(getActivity(), TabsActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("Almacenamiento", "ERROR: subiendo fichero");
                }
            });
        }
    }
}