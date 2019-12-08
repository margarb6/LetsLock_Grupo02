package es.upv.gnd.letslock.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import es.upv.gnd.letslock.Fragments.InicioFragment;
import es.upv.gnd.letslock.Fragments.TimbreFragment;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Notificacion;

import static es.upv.gnd.letslock.MainActivity.fragments;
import static es.upv.gnd.letslock.MainActivity.navigation;

public class AdaptadorNotificaciones extends RecyclerView.Adapter<AdaptadorNotificaciones.NotificacionesViewHolder> {

    protected ArrayList<Notificacion> notificaciones;
    protected View v;
    protected Fragment fragment;

    public AdaptadorNotificaciones(ArrayList<Notificacion> notificaciones, Fragment fragment) {
        this.notificaciones = notificaciones;
        this.fragment = fragment;
    }

    @Override
    public AdaptadorNotificaciones.NotificacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacion_lista, parent, false);
        return new AdaptadorNotificaciones.NotificacionesViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(AdaptadorNotificaciones.NotificacionesViewHolder holder, final int position) {
        holder.personaliza(notificaciones.get(position));
        holder.onclick(notificaciones.get(position).getTipo(), fragment, v);
    }

    // Indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        if (notificaciones == null) return 0;
        return notificaciones.size();
    }

    public void removeItem(int position) {
        notificaciones.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Notificacion notificacion, int position) {

        if(position>notificaciones.size()-1){

            notificaciones.add(notificacion);
            notifyItemInserted(position);
        }
        else{

            notificaciones.add(position, notificacion);
            notifyItemInserted(position);

            ArrayList<Integer> pos= new ArrayList<>();
            for (Notificacion not : notificaciones){

                pos.add(not.getPosition());
            }

            for (int i=0; i<notificaciones.size(); i++){

                for (int posicion : pos){

                    if(posicion<i) notifyItemChanged(posicion);
                }
            }
        }
    }

    public static class NotificacionesViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo;
        public ImageView foto;
        public TextView descripcion;
        public TextView hora;
        public ConstraintLayout layoutABorrar;

        public NotificacionesViewHolder(View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.nombreNotificacion);
            foto = itemView.findViewById(R.id.iconoNotificacion);
            descripcion = itemView.findViewById(R.id.descripcionNotificacion);
            hora = itemView.findViewById(R.id.fechaNotificacion);
            layoutABorrar = itemView.findViewById(R.id.layoutABorrar);
        }

        // Personalizamos un ViewHolder a partir de una notificación
        public void personaliza(final Notificacion notificacion) {

            Date fechaAct = new Date();
            long actual = fechaAct.getTime() + 3600 * 1000;

            for (int i = 1; i < 60; i++) {

                if (actual - notificacion.getHora() <= 60 * 1000 * i && actual - notificacion.getHora() >= 60 * 1000 * (i - 1))
                    hora.setText("Hace " + i + " min");
            }

            for (int i = 2; i < 25; i++) {

                if (actual - notificacion.getHora() <= 3600 * 1000 * i && actual - notificacion.getHora() >= 3600 * 1000 * (i - 1)) {
                    int res = i - 1;
                    hora.setText("Hace " + res + "h");
                }
            }

            if (actual - notificacion.getHora() > 1000 * 60 * 60 * 24 || notificacion.getHora() - actual > 0) {

                Date fechaIngles = new Date(notificacion.getHora());
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm     dd/MM/yyyy", new Locale("es", "ES"));

                try {

                    String fecha = dateFormat.format(fechaIngles);
                    hora.setText(fecha);

                } catch (Exception e) {
                }
            }

            switch (notificacion.getTipo()) {

                case "timbre":
                    tipo.setText("Timbre");
                    foto.setImageResource(R.drawable.notificacion_timbre);
                    descripcion.setText("Alguien ha llamado al timbre");
                    break;

                case "solicitudPin":
                    tipo.setText("Pin solicitado");
                    descripcion.setText("Alguien ha solicitado un pin para abrir la puerta");
                    foto.setImageResource(R.drawable.notificacion_solicitar_ping);
                    break;

                case "errorPin":
                    tipo.setText("Pin incorrecto");
                    descripcion.setText("Alguien está intentando entrar a su casa");
                    foto.setImageResource(R.drawable.notificacion_error_ping);
                    break;

                case "llamanPuerta":
                    tipo.setText("Puerta");
                    descripcion.setText("Se ha abierto la puerta");
                    foto.setImageResource(R.drawable.notificacion_puerta_abierta);
                    break;
            }
        }

        public void onclick(final String tipo, final Fragment fragment, View v){

            View contenedor = v.findViewById(R.id.notificacion_lista_id);
            contenedor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager manager = fragment.getFragmentManager();

                    switch (tipo) {

                        case "timbre":

                            manager.beginTransaction().replace(R.id.fragmentUtilizado, new TimbreFragment()).addToBackStack(null).commit();
                            navigation.getMenu().findItem(R.id.menu_inferior_timbre).setChecked(true);
                            fragments.add(new TimbreFragment());
                            break;

                        case "llamanPuerta":

                            manager.beginTransaction().replace(R.id.fragmentUtilizado, new InicioFragment()).addToBackStack(null).commit();
                            navigation.getMenu().findItem(R.id.menu_inferior_inicio).setChecked(true);
                            fragments.add(new InicioFragment());
                            break;
                    }
                }
            });
        }
    }
}
