package es.upv.gnd.letslock.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


    public RepositorioNotificaciones notificaciones = new NotificacionesLista();
    public AdaptadorNotificacion adaptador = new AdaptadorNotificacion(notificaciones);


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerNotificaciones = vista.findViewById(R.id.recyclerId);
        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerNotificaciones.setAdapter(adaptador);
        recyclerNotificaciones.setHasFixedSize(true);

        notificaciones.anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1, TipoNotificacion.ALERTA));
        notificaciones.anyade(new Notificacion("Timbre", "Nadie ha llamado al timbre", 1, TipoNotificacion.TIMBRE));
        notificaciones.anyade(new Notificacion("Timbre", "8348543 ha llamado al timbre", 1, TipoNotificacion.ALERTA));


        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerNotificaciones.getChildAdapterPosition(v);
                mostrar(pos);
            }
        });


        return vista;

    }


    private Activity actividad;

    public void mostrar(int pos) {
        Intent i = new Intent(actividad, FotoTimbreActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivity(i);

    }

}
