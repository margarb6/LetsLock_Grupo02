package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Fotos {

    private Activity actividad;

    public Fotos(Activity actividad) {

        this.actividad = actividad;
    }

    public Uri tomarFoto(int codigoSolicitud) {

        try {

            Uri uriUltimaFoto;
            File file = File.createTempFile("img_" + (System.currentTimeMillis() / 1000), ".jpg", actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES));

            if (Build.VERSION.SDK_INT >= 24) {

                uriUltimaFoto = FileProvider.getUriForFile(actividad, "es.upv.gnd.letslock.fileProvider", file);

            } else {

                uriUltimaFoto = Uri.fromFile(file);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto);

            actividad.startActivityForResult(intent, codigoSolicitud);
            return uriUltimaFoto;

        } catch (IOException ex) {

            Toast.makeText(actividad, "Error al crear fichero de imagen", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void ponerDeGaleria(int codigoSolicitud) {

        String action;

        if (android.os.Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat

            action = Intent.ACTION_OPEN_DOCUMENT;

        } else {

            action = Intent.ACTION_PICK;
        }

        Intent intent = new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        actividad.startActivityForResult(intent, codigoSolicitud);
    }
}
