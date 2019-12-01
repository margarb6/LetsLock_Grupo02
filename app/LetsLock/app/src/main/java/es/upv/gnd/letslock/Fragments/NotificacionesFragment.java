package es.upv.gnd.letslock.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import es.upv.gnd.letslock.FotoTimbreActivity;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.adapters.AdaptadorNotificacion;
import es.upv.gnd.letslock.model.Notificacion;
import es.upv.gnd.letslock.model.TipoNotificacion;
import es.upv.gnd.letslock.utils.NotificacionesLista;
import es.upv.gnd.letslock.utils.RepositorioNotificaciones;

public class NotificacionesFragment extends Fragment {

    View vista;
    RecyclerView recyclerNotificaciones;


    public AdaptadorNotificacion adaptador;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        RepositorioNotificaciones notificaciones = new NotificacionesLista();
        notificaciones.vaciarNotificaciones();

        //Escuchar base de datos y crear notificacion por cada dato nuevo en bd
        //notificaciones.anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1, TipoNotificacion.ALERTA));
        // notificaciones.anyade(new Notificacion("Timbre", "Nadie ha llamado al timbre", 1, TipoNotificacion.TIMBRE));
        //notificaciones.anyade(new Notificacion("Timbre", "8348543 ha llamado al timbre", 1, TipoNotificacion.ALERTA));



        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerNotificaciones = vista.findViewById(R.id.recyclerId);
        recyclerNotificaciones.setHasFixedSize(true);

        // Show most recent items at the top
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerNotificaciones.setLayoutManager(layoutManager);

        adaptador = new AdaptadorNotificacion(getContext(), FirebaseDatabase.getInstance().getReference().child("logs"));
        recyclerNotificaciones.setAdapter(adaptador);

        notificaciones.vaciarNotificaciones();

        //Escuchar base de datos y crear notificacion por cada dato nuevo en bd
        //notificaciones.anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1, TipoNotificacion.ALERTA));
       // notificaciones.anyade(new Notificacion("Timbre", "Nadie ha llamado al timbre", 1, TipoNotificacion.TIMBRE));
        //notificaciones.anyade(new Notificacion("Timbre", "8348543 ha llamado al timbre", 1, TipoNotificacion.ALERTA));


       /* adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerNotificaciones.getChildAdapterPosition(v);
                mostrar(pos);
            }
        });*/


        return vista;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize Firebase listeners in adapter
        adaptador.startListening();

        // Make sure new events are visible
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerNotificaciones.smoothScrollToPosition(adaptador.getItemCount());
                Log.e("KKKKKKKKKK","Notificacion creada");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // Tear down Firebase listeners in adapter
        adaptador.stopListening();
    }


    private Activity actividad;

    public void mostrar(int pos) {
        Intent i = new Intent(actividad, FotoTimbreActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivity(i);

    }

}
