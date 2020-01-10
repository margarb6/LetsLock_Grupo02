package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.URL;

public class DescargarFoto2 extends AsyncTask<String, Void, Bitmap> {

    private Fragment fragment;
    Activity c;
    private  int id;

    public DescargarFoto2(Fragment fragment, int id){

        this.fragment= fragment;
        this.id= id;
    }

    public DescargarFoto2(int foto_lista) {
        id = foto_lista;
    }

    public DescargarFoto2(Activity c, int foto_lista) {
        this.c = c;
        id = foto_lista;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        String url = params[0];
        Bitmap imagen = descargarImagen(url);
        return imagen;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        super.onPostExecute(result);

        if (result != null) {

            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(c.getResources(), result);
            roundedDrawable.setCornerRadius(result.getHeight());
            ImageView foto = c.findViewById(id);
            foto.setImageDrawable(roundedDrawable);
        }
    }

    public Bitmap descargarImagen(String imageHttpAddress) {

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

