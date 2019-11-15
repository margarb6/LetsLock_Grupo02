package es.upv.gnd.letslock.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.bbdd.AdaptadorUsuariosFirestoreUI;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.AdaptadorUsuarios;
import es.upv.gnd.letslock.bbdd.RepositorioUsuarios;
import es.upv.gnd.letslock.bbdd.Usuario;

public class PersonasFragment extends Fragment {

    View vista;

    private RepositorioUsuarios usuarios;
    private RecyclerView recyclerView;
    public AdaptadorUsuarios adaptador = new AdaptadorUsuarios(usuarios);
    //public static AdaptadorUsuariosFirestoreUI adaptador2;
    private FirestoreRecyclerAdapter<Usuario,AdaptadorUsuarios.ViewHolder> adaptador2;
    private CollectionReference usuarios_coleccion = FirebaseFirestore.getInstance().collection("usuarios");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_personas, container, false);


        recyclerView = vista.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptador);

        Query query = usuarios_coleccion.orderBy("nombre", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Usuario> opciones = new FirestoreRecyclerOptions
                .Builder<Usuario>().setQuery(query, Usuario.class).build();
        adaptador2 = new AdaptadorUsuariosFirestoreUI(opciones);





        adaptador2 = new FirestoreRecyclerAdapter<Usuario, AdaptadorUsuarios.ViewHolder>(opciones) {

            View view;

            @Override
            protected void onBindViewHolder(@NonNull AdaptadorUsuarios.ViewHolder viewHolder, int i, @NonNull Usuario usuario) {
                ImageView imagen = view.findViewById(R.id.foto_lista);


                //new DescargarFoto(imagen, getResources())

                viewHolder.guardarUsuario(usuario.getNombre(), imagen);
            }

            @NonNull
            @Override
            public AdaptadorUsuarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.elemento_lista,parent,false);
                return new AdaptadorUsuarios.ViewHolder(view);

            }
        };

        recyclerView.setAdapter(adaptador2);

        return vista;
        }


    @Override public void onStart () {
        super.onStart();
        adaptador2.startListening();
    }
     @Override public void onStop () {
        super.onStop();
        adaptador2.stopListening();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        adaptador2.stopListening();
    }


}