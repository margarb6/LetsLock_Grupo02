package es.upv.gnd.letslock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Notificacion;

public class AdaptadorNotificacionesFirestoreUI extends FirestoreRecyclerAdapter<Notificacion, AdaptadorNotificacionesFirestoreUI.ViewHolder> {

    protected View.OnClickListener onClickListener;

    public AdaptadorNotificacionesFirestoreUI(@NonNull FirestoreRecyclerOptions<Notificacion> options) {
        super(options);
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo;
        public ImageView foto;
        public TextView descripcion;
        public TextView hora;

        public ViewHolder(View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.nombreNotificacion);
            foto = itemView.findViewById(R.id.iconoNotificacion);
            descripcion = itemView.findViewById(R.id.descripcionNotificacion);
            hora = itemView.findViewById(R.id.fechaNotificacion);
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(final Notificacion notificacion) {

            Date fechaAct = new Date();
            long actual = fechaAct.getTime() + 3600 * 1000;

            for (int i = 1; i < 60; i++) {

                if (actual - notificacion.getHora() <= 60 * 1000 * i && actual - notificacion.getHora() >= 60 * 1000 * (i - 1))
                    hora.setText("Hace " + i + " min");
            }

            for (int i = 2; i < 25; i++) {

                if (actual - notificacion.getHora() <= 3600 * 1000 * i && actual - notificacion.getHora() >= 3600 * 1000 * (i - 1)){
                    int res= i-1;
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

                case "Timbre":
                    tipo.setText(notificacion.getTipo());
                    foto.setImageResource(R.drawable.timbre);
                    descripcion.setText("Alguien ha llamado al timbre");
                    break;

                case "solicitudPin":
                    tipo.setText("Solicitud pin");
                    descripcion.setText("Alguien ha enviado un pin para abrir la puerta");
                    foto.setImageResource(R.drawable.timbre);
                    break;

                case "ErrorPin":
                    tipo.setText("Error pin");
                    descripcion.setText("Alguien está intentando entrar a su casa");
                    foto.setImageResource(R.drawable.alerta);
                    break;

                case "Puerta":
                    tipo.setText(notificacion.getTipo());
                    descripcion.setText("Se ha abierto la puerta");
                    foto.setImageResource(R.drawable.timbre);
                    break;
            }
        }
    }

    @Override
    public AdaptadorNotificacionesFirestoreUI.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacion_lista, parent, false);
        return new AdaptadorNotificacionesFirestoreUI.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorNotificacionesFirestoreUI.ViewHolder holder, int position, @NonNull Notificacion notificacion) {
        holder.personaliza(notificacion);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }
}