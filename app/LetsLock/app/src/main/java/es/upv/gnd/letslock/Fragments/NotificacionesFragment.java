package es.upv.gnd.letslock.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upv.gnd.letslock.bbdd.Notificacion;
import es.upv.gnd.letslock.bbdd.Notificaciones;
import es.upv.gnd.letslock.bbdd.NotificacionesCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.adapters.AdaptadorNotificaciones;
import es.upv.gnd.letslock.adapters.DeleteAdaptador;

public class NotificacionesFragment extends Fragment implements DeleteAdaptador.RecyclerItemTouchHelperListener {

    View vista;
    private RecyclerView recyclerView;
    private AdaptadorNotificaciones adaptador;
    private Notificaciones notificacionesBD = new Notificaciones();

    private CheckBox timbre;
    private CheckBox puerta;
    private CheckBox solicitudPin;
    private CheckBox errorPin;
    private CheckBox buzon;

    private boolean permisos = false;
    private boolean anonimo = false;
    private ArrayList<Notificacion> notDefinitivas = new ArrayList<>();

    private ArrayList<Notificacion> notTimbre = new ArrayList<>();
    private ArrayList<Notificacion> notError = new ArrayList<>();
    private ArrayList<Notificacion> notPuerta = new ArrayList<>();
    private ArrayList<Notificacion> notSolicitud = new ArrayList<>();
    private ArrayList<Notificacion> notBuzon = new ArrayList<>();


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if(!anonimo){

            vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

            recyclerView = vista.findViewById(R.id.recyclerId);

            timbre = vista.findViewById(R.id.timbreCheck);
            errorPin = vista.findViewById(R.id.errorPinCheck);
            puerta = vista.findViewById(R.id.puertaCheck);
            solicitudPin = vista.findViewById(R.id.solicitudPinCheck);
           // buzon = vista.findViewById(R.id.);

            if (prefs.contains("permisos")) permisos = prefs.getBoolean("permisos", false);

            if (!permisos) {
                puerta.setVisibility(View.INVISIBLE);
                solicitudPin.setVisibility(View.INVISIBLE);
            } else {
                onclick(puerta, "llamanPuerta");
                onclick(solicitudPin, "solicitudPin");

            }

            onclick(timbre, "timbre");
            onclick(errorPin, "errorPin");

            notificacionesBD.getNotificaciones(new NotificacionesCallback() {
                @Override
                public void getNotificacionesCallback(ArrayList<Notificacion> notificaciones) {
                    filtrar(notificaciones);
                }
            });
        }
        else {

            vista = inflater.inflate(R.layout.fragment_anonimo, container, false);
        }

        return vista;
    }

    public void filtrar(ArrayList<Notificacion> notificaciones) {

        notDefinitivas.clear();

        for (int i = 0; i < notificaciones.size(); i++) {


            for (String usuario : notificaciones.get(i).getIdUsuarios()) {

                if (usuario.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    if (!permisos) {

                        if (notificaciones.get(i).getTipo().equals("errorPin") || notificaciones.get(i).getTipo().equals("timbre")) {
                            notificaciones.get(i).setPosition(notDefinitivas.size());
                            notDefinitivas.add(notificaciones.get(i));
                        }
                    } else {

                        notificaciones.get(i).setPosition(notDefinitivas.size());
                        notDefinitivas.add(notificaciones.get(i));
                    }
                }
            }
        }

        adaptador = new AdaptadorNotificaciones(notDefinitivas, this);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.SimpleCallback simpleCallback = new DeleteAdaptador(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof AdaptadorNotificaciones.NotificacionesViewHolder) {

            Notificacion notificacionBorrada = notDefinitivas.get(viewHolder.getAdapterPosition());
            int deletedIntex = viewHolder.getAdapterPosition();
            adaptador.removeItem(viewHolder.getAdapterPosition());
            recuperarNotificacionBorrada(viewHolder, notificacionBorrada, deletedIntex);

            notificacionBorrada.getIdUsuarios().remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificacionesBD.setNotificaciones(notificacionBorrada);
            timbre.setChecked(true);
            errorPin.setChecked(true);
            puerta.setChecked(true);
            solicitudPin.setChecked(true);
        }
    }

    private void recuperarNotificacionBorrada(RecyclerView.ViewHolder viewHolder, final Notificacion notificacionBorrada, final int deletedIntex) {

        Snackbar snackbar = Snackbar.make(((AdaptadorNotificaciones.NotificacionesViewHolder) viewHolder).layoutABorrar, "Notificación eliminada", Snackbar.LENGTH_LONG);
        snackbar.setAction("Deshacer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptador.restoreItem(notificacionBorrada, deletedIntex);
                notificacionBorrada.getIdUsuarios().add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                notificacionesBD.setNotificaciones(notificacionBorrada);
            }
        });
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }

    private void onclick(CheckBox checkBox, final String tipo) {

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {

                    for (int i= notDefinitivas.size()-1; i >=0 ; i--) {

                        if (tipo.equals(notDefinitivas.get(i).getTipo())) {

                            switch (tipo) {

                                case "timbre":

                                    notTimbre.add(notDefinitivas.get(i));
                                    adaptador.removeItem(i);
                                    break;

                                case "solicitudPin":

                                    notSolicitud.add(notDefinitivas.get(i));
                                    adaptador.removeItem(i);
                                    break;

                                case "errorPin":

                                    notError.add(notDefinitivas.get(i));
                                    adaptador.removeItem(i);
                                    break;

                                case "llamanPuerta":

                                    notPuerta.add(notDefinitivas.get(i));
                                    adaptador.removeItem(i);
                                    break;
                            }
                        }
                    }
                }else {

                    switch (tipo) {

                        case "timbre":

                            for (int i= notTimbre.size()-1; i >=0 ; i--) {

                                adaptador.restoreItem(notTimbre.get(i), notTimbre.get(i).getPosition());
                            }
                            notTimbre.clear();
                            break;

                        case "solicitudPin":

                            for (int i= notSolicitud.size()-1; i >=0 ; i--) {

                                adaptador.restoreItem(notSolicitud.get(i), notSolicitud.get(i).getPosition());
                            }
                            notSolicitud.clear();
                            break;

                        case "errorPin":

                            for (int i= notError.size()-1; i >=0 ; i--) {

                                adaptador.restoreItem(notError.get(i), notError.get(i).getPosition());
                            }
                            notError.clear();
                            break;

                        case "llamanPuerta":

                            for (int i= notPuerta.size()-1; i >=0 ; i--) {

                                adaptador.restoreItem(notPuerta.get(i), notPuerta.get(i).getPosition());
                            }
                            notPuerta.clear();
                            break;
                    }
                }
            }
        });
    }
}
