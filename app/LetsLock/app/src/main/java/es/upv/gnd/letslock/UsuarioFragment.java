package es.upv.gnd.letslock;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class UsuarioFragment extends Fragment {

    View vista;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {

        vista = inflador.inflate(R.layout.fragment_usuario, contenedor, false);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = vista.findViewById(R.id.nombre);
        TextView email = vista.findViewById(R.id.email);
        TextView proveedores = vista.findViewById(R.id.proveedores);
        TextView telefono = vista.findViewById(R.id.telefono);
        TextView iden = vista.findViewById(R.id.iden);

        List<String> proveedoresL = usuario.getProviders();
        String proveed = "";

        for (int i = 0; i < proveedoresL.size(); i++) {

            if (i == proveedoresL.size() - 2) {

                proveed += proveedoresL.get(i) + " y ";

            } else {

                if (proveedoresL.size() - 1 == i) {

                    proveed += proveedoresL.get(i) + ".";

                } else {

                    proveed += proveedoresL.get(i) + ", ";
                }
            }
        }

        proveedores.setText(proveed);
        nombre.setText(usuario.getDisplayName());
        email.setText(usuario.getEmail());
        iden.setText(usuario.getUid());
        telefono.setText(usuario.getPhoneNumber());

        CargaImagenes nuevaTarea = new CargaImagenes();
        nuevaTarea.execute(usuario.getPhotoUrl().toString());

       Button cambiarEditar = vista.findViewById(R.id.button);

        cambiarEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        return vista;
    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(vista.getContext());
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

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
            pDialog.dismiss();
        }

    }

    private Bitmap descargarImagen(String imageHttpAddress) {
        URL imageUrl = null;
        Bitmap imagen = null;

        try {
            InputStream in = new URL(imageHttpAddress).openStream();
            imagen= BitmapFactory.decodeStream(in);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return imagen;
    }
}
