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

import com.example.serpumar.comun.Notificacion;
import com.example.serpumar.comun.Notificaciones;
import com.example.serpumar.comun.NotificacionesCallback;
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

    public static CheckBox timbre;
    public static CheckBox puerta;
    public static CheckBox solicitudPin;
    public static CheckBox errorPin;

    private boolean permisos = false;
    private ArrayList<Notificacion> notDefinitivas = new ArrayList<>();

    private ArrayList<Notificacion> notTimbre = new ArrayList<>();
    private ArrayList<Notificacion> notError = new ArrayList<>();
    private ArrayList<Notificacion> notPuerta = new ArrayList<>();
    private ArrayList<Notificacion> notSolicitud = new ArrayList<>();


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerView = vista.findViewById(R.id.recyclerId);

        timbre = vista.findViewById(R.id.timbreCheck);
        errorPin = vista.findViewById(R.id.errorPinCheck);
        puerta = vista.findViewById(R.id.puertaCheck);
        solicitudPin = vista.findViewById(R.id.solicitudPinCheck);

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
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

                for (int i = 0; i < notDefinitivas.size(); i++) {

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

                if (isChecked) {

                    switch (tipo) {

                        case "timbre":

                            for (Notificacion not : notTimbre) {

                                adaptador.restoreItem(not, not.getPosition());
                            }
                            notTimbre.clear();
                            break;

                        case "solicitudPin":

                            for (Notificacion not : notSolicitud) {

                                adaptador.restoreItem(not, not.getPosition());
                            }
                            notSolicitud.clear();
                            break;

                        case "errorPin":

                            for (Notificacion not : notError) {

                                adaptador.restoreItem(not, not.getPosition());
                            }
                            notError.clear();
                            break;

                        case "llamanPuerta":

                            for (Notificacion not : notPuerta) {

                                adaptador.restoreItem(not, not.getPosition());
                            }
                            notPuerta.clear();
                            break;
                    }
                }
            }
        });
    }
}
