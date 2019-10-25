package es.upv.gnd.letslock.ui.editar_perfil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;
import java.net.URL;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.ui.acerca_de.AcercaDeFragment;
import es.upv.gnd.letslock.ui.perfil.MainTabs;

public class EditarPerfilFragment extends Fragment {

    private EditarPerfilViewModel EditarPerfilViewModel;
    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EditarPerfilViewModel = ViewModelProviders.of(this).get(EditarPerfilViewModel.class);
        vista = inflater.inflate(R.layout.fragment_usuario_editar, container, false);

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        final TextView nombre = vista.findViewById(R.id.nombre);
        final TextView email = vista.findViewById(R.id.email);
        email.setText(usuario.getEmail());

        cargarDatos();

        Button guardar = vista.findViewById(R.id.button);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre.getText().toString())
                        //.setPhotoUri(Uri.parse("https://www.ejemplo.com/usuario/foto.jpg"))
                        .build();
                usuario.updateProfile(perfil).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Confirmación");
                        builder.setMessage("¿Está seguro de cambiar su correo y nombre?");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                cargarDatos();
                            }
                        });

                        builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                email.setText(usuario.getEmail());
                            }
                        });
                        builder.show();
                    }
                });
            }
        });

        CargaImagenes nuevaTarea = new CargaImagenes();
        try {

            nuevaTarea.execute(usuario.getPhotoUrl().toString());
        }
        catch (NullPointerException error){

            return vista;
        }

        return vista;
    }

    private void cargarDatos() {

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        final TextView nombre = vista.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground", "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            ImageView foto = vista.findViewById(R.id.foto);
            foto.setImageBitmap(result);
        }
    }

    private Bitmap descargarImagen(String imageHttpAddress) {
        URL imageUrl = null;
        Bitmap imagen = null;

        try {
            InputStream in = new URL(imageHttpAddress).openStream();
            imagen = BitmapFactory.decodeStream(in);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return imagen;
    }
}
