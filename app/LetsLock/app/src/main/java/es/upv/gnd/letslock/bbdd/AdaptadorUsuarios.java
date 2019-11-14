package es.upv.gnd.letslock.bbdd;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.Fragments.PerfilFragment;
import es.upv.gnd.letslock.Fragments.PersonasFragment;
import es.upv.gnd.letslock.R;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    protected RepositorioUsuarios usuarios;



    public AdaptadorUsuarios (RepositorioUsuarios usuarios) {
        this.usuarios = usuarios;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public ImageView foto;
        private File localFile;

   public void guardarUsuario(String nombre, ImageView iamgen) {
       this.nombre.setText(nombre);
       this.foto = iamgen;
   }

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            foto = itemView.findViewById(R.id.foto_lista);
        }

        // Personalizamos un ViewHolder a partir de un lugar

        public void personaliza(final Usuario usuario_) {
            final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

            nombre.setText(usuario_.getNombre());
            /*try {
                localFile = File.createTempFile("image", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String path = localFile.getAbsolutePath();
            Log.d("Almacenamiento", "creando fichero: " + path);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference ficheroRef = storageRef.child("Fotos_perfil/" + usuario.getUid());

            ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Almacenamiento", "Fichero bajado");
                    ImageView fotoContenedor = itemView.findViewById(R.id.foto_lista);
                    RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getClass().getResources(), BitmapFactory.decodeFile(path));
                    roundedDrawable.setCircular(true);
                    fotoContenedor.setImageDrawable(roundedDrawable);
                }

                //Si no se puede obtener ponemos como predeterminada la del usuario de google
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (usuario.getPhotoUrl()!=null){
                        DescargarFoto fotoDes= new DescargarFoto(PersonasFragment.this,R.id.foto_lista);
                        fotoDes.execute(usuario.getPhotoUrl().toString());
                    }
                }
            });*/

            foto.setScaleType(ImageView.ScaleType.FIT_END);
        }

    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista, parent, false);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int id) {
        Usuario usuario = usuarios.elemento(id);
        holder.personaliza(usuario);
    }
    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return usuarios.tamanyo();
    }

    public static void personalizaVista(ViewHolder holder, Usuario usuario) {

    }

}
