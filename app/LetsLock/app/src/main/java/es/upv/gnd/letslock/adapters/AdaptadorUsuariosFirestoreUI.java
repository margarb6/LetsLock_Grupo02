package es.upv.gnd.letslock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.adapters.AdaptadorUsuarios;
import es.upv.gnd.letslock.bbdd.Usuario;

public class AdaptadorUsuariosFirestoreUI extends  FirestoreRecyclerAdapter<Usuario, AdaptadorUsuarios.ViewHolder> {

    protected View.OnClickListener onClickListener;

    public AdaptadorUsuariosFirestoreUI(
            @NonNull FirestoreRecyclerOptions<Usuario> options) {
        super(options);
    }
    @Override public AdaptadorUsuarios.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new AdaptadorUsuarios.ViewHolder(view);
    }
    @Override protected void onBindViewHolder(@NonNull AdaptadorUsuarios.ViewHolder holder, int position, @NonNull Usuario usuario) {
        AdaptadorUsuarios.personalizaVista(holder, usuario);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }
    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }
}





