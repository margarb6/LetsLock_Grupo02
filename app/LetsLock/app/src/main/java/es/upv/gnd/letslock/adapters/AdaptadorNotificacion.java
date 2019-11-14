package es.upv.gnd.letslock.adapters;

import android.app.Dialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.model.Notificacion;
import es.upv.gnd.letslock.utils.NotificacionesLista;
import es.upv.gnd.letslock.utils.RepositorioNotificaciones;

public class AdaptadorNotificacion extends RecyclerView.Adapter<AdaptadorNotificacion.ViewHolder> {

    protected View.OnClickListener onClickListener;
    protected RepositorioNotificaciones notificaciones; // Lista de notificaciones
    Dialog timbreDialog;

    public AdaptadorNotificacion(RepositorioNotificaciones notificaciones){
        this.notificaciones = notificaciones;
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public AdaptadorNotificacion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacion_lista, parent, false);

       final ViewHolder vHolder = new ViewHolder(v);

       // Dialog
        timbreDialog = new Dialog(parent.getContext());
        timbreDialog.setContentView(R.layout.activity_foto_timbre);

        vHolder.notificacion_lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ImageView dialog_image = (ImageView) timbreDialog.findViewById(R.id.imageViewDialog);
             dialog_image.setImageResource(R.drawable.timbre);


            timbreDialog.show();
            }
        });

        return vHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notificacion notificacion = notificaciones.elemento(position);

        Log.e("mamabichote",""+notificacion.getNombre());
        holder.personaliza(notificacion);


    }

    @Override
    public int getItemCount() {
        return notificaciones.tamanyo();
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, descripcion, id, fecha;
        public ImageView icono;

        public ConstraintLayout notificacion_lista;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreNotificacion);
            descripcion = itemView.findViewById(R.id.descripcionNotificacion);
            id = itemView.findViewById(R.id.idNotificacion);
            fecha = itemView.findViewById(R.id.fechaNotificacion);
            icono = itemView.findViewById(R.id.iconoNotificacion);

            notificacion_lista = (ConstraintLayout) itemView.findViewById(R.id.notificacion_lista_id);
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Notificacion notificacion) {
            nombre.setText(notificacion.getNombre());
            descripcion.setText(notificacion.getDescripcion());


            int id = R.drawable.alerta;

            Log.e("JEJE",""+notificacion.toString());

            switch (notificacion.getTipo()) {
                case TIMBRE:
                    id = R.drawable.timbre;
                    break;
            }
            icono.setImageResource(id);
            icono.setScaleType(ImageView.ScaleType.FIT_END);
        }
    }
}

