package es.upv.gnd.letslock.ui.perfil;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.TabsAdapter;

public class PerfilFragment extends Fragment {

    private PerfilViewModel PerfilViewModel;
    View vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        PerfilViewModel = ViewModelProviders.of(this).get(PerfilViewModel.class);
        vista = inflater.inflate(R.layout.fragment_perfil, container, false);

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

        try {

            nuevaTarea.execute(usuario.getPhotoUrl().toString());
        }
        catch (NullPointerException error){

            return vista;
        }

        return vista;
    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {


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