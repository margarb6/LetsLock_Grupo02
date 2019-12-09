package es.upv.gnd.letslock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.upv.gnd.letslock.HistorialTimbreActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import es.upv.gnd.letslock.R;

public class TimbreFragment extends Fragment {

    View vista;
    TextView nadie_llama;
    TextView pregunta;
    ImageView imagen;
    Button si;
    Button no;
    Button historial;

    String TAG = "MARTA";
    private StorageReference storageRef;

    private boolean anonimo = false;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if (!anonimo) {

            vista = inflater.inflate(R.layout.fragment_timbre, container, false);

            nadie_llama = vista.findViewById(R.id.nadie_llama);
            pregunta = vista.findViewById(R.id.timbre_pregunta);
            imagen = vista.findViewById(R.id.imagen_timbre);
            si = vista.findViewById(R.id.timbre_boton_si);
            no = vista.findViewById(R.id.timbre_boton_no);
            historial = vista.findViewById(R.id.boton_historial);

            nadie_llama.setVisibility(View.INVISIBLE);
            pregunta.setVisibility(View.VISIBLE);
            si.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);

            historial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), HistorialTimbreActivity.class);
                    startActivity(i);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    nadie_llama.setVisibility(View.VISIBLE);
                    pregunta.setVisibility(View.INVISIBLE);
                    si.setVisibility(View.INVISIBLE);
                    no.setVisibility(View.INVISIBLE);

                }
            }, 30000);

            storageRef = FirebaseStorage.getInstance().getReference();


            FirebaseFirestore ff = FirebaseFirestore.getInstance();

            ff.collection("imagenes_timbre").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if (documentSnapshots.isEmpty()) {
                                Log.d(TAG, "onSuccess: LIST EMPTY");
                                return;
                            } else if (ultimaFoto(documentSnapshots) != null) {
                                // Convert the whole Query Snapshot to a list
                                // of objects directly! No need to fetch each
                                // document.

                                Log.d(TAG, "onSuccess: " + documentSnapshots.getDocuments().toString());
                                Glide.with(getContext())
                                        .load(ultimaFoto(documentSnapshots))
                                        .into(imagen);
                            } else {
                                imagen.setImageResource(R.drawable.applogo);
                            }
                        }
                    });
        } else {
            vista = inflater.inflate(R.layout.fragment_anonimo, container, false);
        }

        //bajarFichero();
        return vista;
    }

    //(System.currentTimeMillis()+5*60*1000) < docS.getLong("tiempo") && docS.getLong("tiempo" )>(System.currentTimeMillis()-5*60*10000)

    private URL ultimaFoto(QuerySnapshot qs) {

        long tiempo = 1000000000;
        long cincoMin = 5 * 60 * 1000;
        URL url = null;
        DocumentSnapshot ds = null;
        for (DocumentSnapshot docS : qs.getDocuments()) {
            if (docS.getLong("tiempo") > tiempo) {
                tiempo = docS.getLong("tiempo");
                ds = docS;
                if ((System.currentTimeMillis() - docS.getLong("tiempo")) < cincoMin) {
                    try {
                        url = new URL(ds.getString("url"));
                        Log.e("URL", ds.getString("url"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }


            }
        }


        return url;

    }

    private void bajarFichero() {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("imagenes_timbre/");
        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");
                imagen = vista.findViewById(R.id.imagen_timbre);
                imagen.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });
    }
}
