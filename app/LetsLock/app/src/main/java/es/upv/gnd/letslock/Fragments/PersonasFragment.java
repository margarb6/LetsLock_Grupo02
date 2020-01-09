package es.upv.gnd.letslock.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import es.upv.gnd.letslock.adapters.AdaptadorPersonas;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Usuario;

public class PersonasFragment extends Fragment {

    View vista;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usuarios_coleccion = FirebaseFirestore.getInstance().collection("usuarios");
    private RecyclerView recyclerView;
    private AdaptadorPersonas adaptador;
    //public AdaptadorUsuarios adaptador;
    //ArrayList<Usuario> usuarios;
    // private FirestoreRecyclerAdapter<Usuario,AdaptadorUsuarios.UsuariosViewHolder> adaptador2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_personas, container, false);

        setUpRecycler();

       /* adaptador2 = new FirestoreRecyclerAdapter<Usuario, AdaptadorUsuarios.UsuariosViewHolder>(opciones) {

            View view;
            

            @Override
            protected void onBindViewHolder(@NonNull AdaptadorUsuarios.UsuariosViewHolder viewHolder, int i, @NonNull Usuario usuario) {
                viewHolder.guardarUsuario(usuario.getNombre(), usuario.getFotoUrl(), usuario.isPermisos() + "", getActivity());
            }

            @NonNull
            @Override
            public AdaptadorUsuarios.UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.elemento_lista, parent, false);
                return new AdaptadorUsuarios.UsuariosViewHolder(view);

            }
        };

        recyclerView.setAdapter(adaptador2);*/

        return vista;
    }

    private void setUpRecycler() {

        Query query = usuarios_coleccion.orderBy("nombre", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Usuario> opciones = new FirestoreRecyclerOptions
                .Builder<Usuario>().setQuery(query, Usuario.class).build();

        adaptador = new AdaptadorPersonas(opciones);

        recyclerView = vista.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptador);

    }

    @Override
    public void onStart() {
        super.onStart();
        adaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }
}