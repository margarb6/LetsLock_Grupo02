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

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.adapters.AdaptadorNotificacionesFirestoreUI;
import es.upv.gnd.letslock.bbdd.Notificacion;

public class NotificacionesFragment extends Fragment {

    View vista;
    private RecyclerView recyclerView;
    private AdaptadorNotificacionesFirestoreUI adaptador;
    private CollectionReference notificacion_coleccion = FirebaseFirestore.getInstance().collection("notificaciones");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerView = vista.findViewById(R.id.recyclerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = notificacion_coleccion.orderBy("hora", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notificacion> opciones = new FirestoreRecyclerOptions.Builder<Notificacion>().setQuery(query, Notificacion.class).build();
        adaptador = new AdaptadorNotificacionesFirestoreUI(opciones);
        recyclerView.setAdapter(adaptador);

        return vista;
    }


    @Override public void onStart () {
        super.onStart();
        adaptador.startListening();
    }
    @Override public void onStop () {
        super.onStop();
        adaptador.stopListening();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

}
