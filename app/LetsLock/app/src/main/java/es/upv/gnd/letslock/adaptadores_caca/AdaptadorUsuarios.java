package es.upv.gnd.letslock.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Usuario;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.UsuariosViewHolder> {

    protected ArrayList<Usuario> usuarios;
    //protected View v;
    protected Context context;

    public AdaptadorUsuarios(ArrayList<Usuario> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
    }

    @Override
    public AdaptadorUsuarios.UsuariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        return new AdaptadorUsuarios.UsuariosViewHolder( LayoutInflater.from(context).inflate(R.layout.elemento_lista, parent, false));
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(AdaptadorUsuarios.UsuariosViewHolder holder, int id) {

        holder.nombre.setText(usuarios.get(id).getNombre());
        holder.permiso.setText(usuarios.get(id).isPermisos()+"");
        Picasso.get().load(usuarios.get(id).getFotoUrl()).into(holder.foto);


        //Usuario usuario = usuarios.get(id);
       // holder.personaliza(usuario);
    }

    // Indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        if (usuarios == null) return 0;
        return usuarios.size();
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class UsuariosViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public ImageView foto;
        //public String fotoURL;
        private TextView permiso;
        //private Activity c;
        // Creamos el ViewHolder con la vista de un elemento sin personalizar
        public UsuariosViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            foto = itemView.findViewById(R.id.foto_lista);
            permiso = itemView.findViewById(R.id.permiso_lista);
        }

        /*public void guardarUsuario(String nombre, String iamgen, String permiso, Activity c) {
            this.nombre.setText(nombre);
            this.fotoURL = iamgen;
            this.c = c;
            this.permiso.setText(permiso);
        }



        // Personalizamos un ViewHolder a partir de un lugar

        public void personaliza(final Usuario usuario_) {

            nombre.setText(usuario_.getNombre());
            boolean permisos = usuario_.isPermisos();
            if(permisos) {
                permiso.setText("Permiso de propietario");
            }else{
                permiso.setText("Permiso de habitante");
            }
            foto.setImageBitmap(new DescargarFoto2(c, R.id.foto_lista).descargarImagen(fotoURL));

            //permiso.setText(usuario_.isPermisos() + "");
            //foto.setScaleType(ImageView.ScaleType.FIT_END);
        }*/

    }





    public static void personalizaVista(UsuariosViewHolder holder, Usuario usuario) {

    }

}
