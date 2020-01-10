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

    private CollectionReference usuarios_coleccion = FirebaseFirestore.getInstance().collection("usuarios");
    private RecyclerView recyclerView;
    private AdaptadorPersonas adaptador;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_personas, container, false);
        setUpRecycler();

        return vista;
    }

    private void setUpRecycler() {

        Query query = usuarios_coleccion;
        FirestoreRecyclerOptions<Usuario> opciones = new FirestoreRecyclerOptions.Builder<Usuario>().setQuery(query, Usuario.class).build();

        adaptador = new AdaptadorPersonas(opciones, getContext());

        recyclerView = vista.findViewById(R.id.recyclerView);
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