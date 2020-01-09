package es.upv.gnd.letslock.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import es.upv.gnd.letslock.bbdd.Usuario;

import android.content.Context;
import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Imagen;
import es.upv.gnd.letslock.bbdd.NotificacionesCallback;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

import static es.upv.gnd.letslock.bbdd.Usuarios.setUsuario;




    public class AdaptadorPersonas extends FirestoreRecyclerAdapter<Usuario, AdaptadorPersonas.PersonasViewHolder> {

        private Context context;
        protected ArrayList<Usuario> usuarios = new ArrayList<>();
        protected View.OnClickListener onClickListener;

        public AdaptadorPersonas(@NonNull FirestoreRecyclerOptions<Usuario> options) {
            super(options);
        }

        public class PersonasViewHolder extends RecyclerView.ViewHolder {
            public TextView nombre;
            public ImageView foto;
            private TextView permiso;
            ConstraintLayout expandableLayout;
            ConstraintLayout layout;
            private Button b;

            public PersonasViewHolder(@NonNull View itemView) {
                super(itemView);
                nombre = itemView.findViewById(R.id.nombre);
                foto = itemView.findViewById(R.id.foto_lista);
                permiso = itemView.findViewById(R.id.permiso_lista);
                b = itemView.findViewById(R.id.info_cambio);
                expandableLayout = itemView.findViewById(R.id.expandableLayout);
                layout = itemView.findViewById(R.id.layout);
/*
            holder.nombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usuario = usuarios.get();
                    usuario.setExpanded(!usuario.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });*/

            }
        }

        @NonNull
        @Override
        public PersonasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elemento_lista, parent, false);
            return new AdaptadorPersonas.PersonasViewHolder(view);
        }



        @Override
        protected void onBindViewHolder(@NonNull final AdaptadorPersonas.PersonasViewHolder holder, final int position, @NonNull final Usuario usuario) {

            usuarios.add(position,usuario);

            Log.d("hola","sfomep"+usuarios);

            holder.nombre.setText(usuario.getNombre());

            final boolean permisos = usuario.isPermisos();
            if(permisos) {
                holder.permiso.setText("Permiso de propietario");
            }else{
                holder.permiso.setText("Permiso de habitante");
            }
            final Usuarios userBD = new Usuarios();
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("caca","kjfbhiwbgwebgfuerkaugvkrew"+usuarios.size());
                    for (int i = 0; i < usuarios.size(); i++) {
                        if(!usuarios.get(i).isPermisos()) {

                            usuarios.get(i).setPermisos(true);
                        }else {
                            usuarios.get(i).setPermisos(false);
                        }
                        userBD.setUsuario(usuarios.get(i));
                    }

                    Log.d("caca","kjfbhiwbgwebgfuerkaugvkrew"+usuarios.get(0).isPermisos());

                }
            });
            //boolean isExpanded = usuarios.get(position).isExpanded();
            //holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);



       /*holder.nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < usuarios.size(); i++) {
                    if (!usuarios.get(i).isExpanded()) {
                        holder.expandableLayout.setVisibility(View.GONE);

                    } else {

                       holder.expandableLayout.setVisibility(View.VISIBLE);
                    }

                }
            }
        });*/



            //holder.permiso.setText(usuario.isPermisos()+"");

            //Picasso.get().load(usuarios.get(position).getFotoUrl()).into(holder.foto);
            //Picasso.get().load("https://lh3.googleusercontent.com/a-/AAuE7mCnO3XmxArknCte-t3MkhVk0P7KQjf5lHUOkYvfRQ=s96-c").into(holder.foto);

        }



    }



