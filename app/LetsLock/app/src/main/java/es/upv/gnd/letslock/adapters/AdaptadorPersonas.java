package es.upv.gnd.letslock.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.upv.gnd.letslock.bbdd.Usuario;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

public class AdaptadorPersonas extends FirestoreRecyclerAdapter<Usuario, AdaptadorPersonas.PersonasViewHolder> {

    private Context context;
    protected ArrayList<Usuario> usuarios = new ArrayList<>();
    protected ArrayList<String> ids = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdaptadorPersonas(@NonNull FirestoreRecyclerOptions<Usuario> options, Context context) {
        super(options);
        this.context = context;
    }

    public class PersonasViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public ImageView foto;
        private TextView permiso;
        private Button b;

        public PersonasViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            foto = itemView.findViewById(R.id.foto_lista);
            permiso = itemView.findViewById(R.id.permiso_lista);
            b = itemView.findViewById(R.id.info_cambio);
        }
    }

    @NonNull
    @Override
    public PersonasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new AdaptadorPersonas.PersonasViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final AdaptadorPersonas.PersonasViewHolder holder, final int position, @NonNull final Usuario usuario) {

        if (usuarios.size() != ids.size()) usuarios.add(usuario);

        Usuarios usuariosBD = new Usuarios();
        usuariosBD.getUsuarios(new UsuariosCallback() {
            @Override
            public void getUsuariosCallback(Usuario usuario) {

            }

            @Override
            public void getAllUsuariosCallback(ArrayList<String> idUsuarios) {

                ids = idUsuarios;
            }
        });

        holder.nombre.setText(usuario.getNombre());
        holder.permiso.setText("Permiso de habitante");

        if (usuario.isPermisos()) {
            holder.permiso.setText("Permiso de propietario");
        }

        Glide.with(context).load(usuario.getFotoUrl())
                .placeholder(R.drawable.applogo)
                .circleCrop()
                .into(holder.foto);

        onclick(holder, position);
    }

    private void onclick(@NonNull final AdaptadorPersonas.PersonasViewHolder holder, final int position) {

        holder.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = ids.get(position);

                if (usuarios.get(position).isPermisos()) {

                    db.collection("usuarios").document(id).update("permisos", false);
                    usuarios.get(position).setPermisos(false);

                } else {

                    db.collection("usuarios").document(id).update("permisos", true);
                    usuarios.get(position).setPermisos(true);
                }
            }
        });
    }
}



