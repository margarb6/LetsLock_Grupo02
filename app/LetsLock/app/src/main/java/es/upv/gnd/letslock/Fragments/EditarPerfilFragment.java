package es.upv.gnd.letslock.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
    boolean permisos;
    String fotoUrl;

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

        vista = inflater.inflate(R.layout.fragment_perfil_editar, container, false);

        final TextView nombre = vista.findViewById(R.id.nombre);
        final TextView pin = vista.findViewById(R.id.pin);

        //Establece los valores firestore en los TextView
        userBD.getUsuario(new UsuariosCallback() {
            public void getUsuariosCallback(Usuario usuarioBD) {
                nombre.setText(usuarioBD.getNombre());
                id = usuario.getUid();
                permisos= usuarioBD.isPermisos();
                pin.setText(usuarioBD.getPin());
                fotoUrl = usuarioBD.getFotoUrl();
            }

            @Override
            public void getAllUsuariosCallback(ArrayList<String> idUsuarios, ArrayList<Usuario> usuario) {

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
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeFile(path));
                roundedDrawable.setCircular(true);
                fotoContenedor.setImageDrawable(roundedDrawable);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (usuario.getPhotoUrl() != null) {
                    DescargarFoto fotoDes = new DescargarFoto(EditarPerfilFragment.this.getActivity(), R.id.FotoEditar);
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
        Button guardar = vista.findViewById(R.id.button_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmación");
                builder.setMessage("¿Está seguro de cambiar su nombre, permisos y foto?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String texto="";
                        if(pin.getText().length()<4) texto= "Inserte un mínimo de 4 digitos";
                        if(nombre.getText().length()== 0) texto = (texto.isEmpty() ?  "Inserte un nombre": "Inserte un mínimo de 4 digitos y un nombre");
                        if(texto.isEmpty()){
                            subirFoto();
                            userBD.setUsuario(new Usuario(nombre.getText().toString(), permisos, pin.getText().toString(), fotoUrl));
                            startActivity(new Intent(getContext(), TabsActivity.class));
                        }
                        else Toast.makeText(getContext(),texto,Toast.LENGTH_LONG).show();
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
        if (prefs.contains("image")) foto = comprimirFoto(Uri.parse(prefs.getString("image", "null"))); //foto = comprimirFoto(Uri.parse(prefs.getString("image", "null"))); //foto = Uri.parse(prefs.getString("image", "null"));
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

    public Uri comprimirFoto(Uri uri) {

        //ImageDecoder.Source source = ImageDecoder.createSource(this.getContext().getContentResolver(), uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = null;
        File file = null;

        try {
            file = File.createTempFile("compressed", ".jpg");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri),null, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            //compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            FileOutputStream fileout = new FileOutputStream(file);
            fileout.write(out.toByteArray());
            fileout.flush();
            fileout.close();

            Uri uriFinal = Uri.fromFile(file);
            Log.d("Uri", "Uri final: " + uriFinal);
            return uriFinal;


        } catch (Exception e) {
            Log.e("Error","Error al comprimir");
        }

        return uri;
    }
}