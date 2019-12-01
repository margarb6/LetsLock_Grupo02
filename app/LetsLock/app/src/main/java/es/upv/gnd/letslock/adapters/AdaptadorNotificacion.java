package es.upv.gnd.letslock.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.text.format.DateUtils;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.upv.gnd.letslock.GlideApp;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.model.Notificacion;

import es.upv.gnd.letslock.utils.NotificacionesLista;
import es.upv.gnd.letslock.utils.RepositorioNotificaciones;

public class AdaptadorNotificacion extends FirebaseRecyclerAdapter<Notificacion, AdaptadorNotificacion.ViewHolder> {

    //public class DoorbellEntryAdapter extends FirebaseRecyclerAdapter<DoorbellEntry, DoorbellEntryAdapter.DoorbellEntryViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, descripcion, id, fecha;
        public ImageView icono, foto;

       // public ConstraintLayout notificacion_lista;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreNotificacion);
            descripcion = itemView.findViewById(R.id.descripcionNotificacion);
            id = itemView.findViewById(R.id.idNotificacion);
            fecha = itemView.findViewById(R.id.fechaNotificacion);
            icono = itemView.findViewById(R.id.iconoNotificacion);
            foto = itemView.findViewById(R.id.fotoNotificacion);

            //notificacion_lista = (ConstraintLayout) itemView.findViewById(R.id.notificacion_lista_id);
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Notificacion notificacion) {
            nombre.setText(notificacion.getNombre());
            descripcion.setText(notificacion.getDescripcion());


            int id = R.drawable.alerta;

            Log.e("JEJE", "" + notificacion.toString());

            switch (notificacion.getTipo()) {
                case TIMBRE:
                    id = R.drawable.timbre;
                    break;
            }
            icono.setImageResource(id);
            icono.setScaleType(ImageView.ScaleType.FIT_END);
        }
    }


    private Context mApplicationContext;
    private FirebaseStorage mFirebaseStorage;

    protected View.OnClickListener onClickListener;
    private RepositorioNotificaciones notificaciones; // Lista de notificaciones
    Dialog timbreDialog;

    public AdaptadorNotificacion(Context context, DatabaseReference ref) {
        super(new FirebaseRecyclerOptions.Builder<Notificacion>()
                .setQuery(ref, Notificacion.class)
                .build());

        mApplicationContext = context.getApplicationContext();
        mFirebaseStorage = FirebaseStorage.getInstance();

       // this.notificaciones = notificaciones;


    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public AdaptadorNotificacion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacion_lista, parent, false);

       /* final ViewHolder vHolder = new ViewHolder(v);

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

        return vHolder;*/
        View entryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notificacion_lista, parent, false);

        return new ViewHolder(entryView);
    }


    @Override
    protected void onBindViewHolder(ViewHolder holder, int i, @NonNull Notificacion notificacion) {

        notificacion = notificaciones.elemento(i);

        holder.personaliza(notificacion);
        // Display the timestamp
        CharSequence prettyTime = DateUtils.getRelativeDateTimeString(mApplicationContext,
                notificacion.getFecha(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        holder.fecha.setText(prettyTime);

        // Display the image
        if (notificacion.getFoto() != null) {
            StorageReference fotoRef = mFirebaseStorage.getReferenceFromUrl(notificacion.getFoto());

            GlideApp.with(mApplicationContext)
                    .load(fotoRef)
                    .placeholder(R.drawable.ic_image)
                    .into(holder.foto);
        }
        // Display the metadata
        if (notificacion.getAnnotations() != null) {
            ArrayList<String> keywords = new ArrayList<>( notificacion.getAnnotations().keySet());

            int limit = Math.min(keywords.size(), 3);
            holder.descripcion.setText(TextUtils.join("\n", keywords.subList(0, limit)));
        } else {
            holder.descripcion.setText("no annotations yet");
        }




    }


    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
}
