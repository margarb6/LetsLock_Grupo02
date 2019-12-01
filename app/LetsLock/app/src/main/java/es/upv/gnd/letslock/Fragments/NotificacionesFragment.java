package es.upv.gnd.letslock.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import es.upv.gnd.letslock.FotoTimbreActivity;
import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.adapters.AdaptadorNotificacion;
import es.upv.gnd.letslock.model.Imagen;
import es.upv.gnd.letslock.model.Notificacion;
import es.upv.gnd.letslock.model.TipoNotificacion;
import es.upv.gnd.letslock.utils.NotificacionesLista;
import es.upv.gnd.letslock.utils.RepositorioNotificaciones;

import static es.upv.gnd.letslock.model.Imagen.registrarImagen;

public class NotificacionesFragment extends Fragment {

    //--------------------------------------------------------------------------------------------------
    // PRUEBA PRÁCTICA ALMACENAMIENTO
    //--------------------------------------------------------------------------------------------------
    private StorageReference storageRef;
    private AdaptadorNotificacion adaptador;
    View vista;
    RecyclerView recyclerNotificaciones;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerNotificaciones = vista.findViewById(R.id.recyclerId);
        recyclerNotificaciones.setHasFixedSize(true);

        Query query = FirebaseFirestore.getInstance()
                .collection("imagenes")
                .orderBy("tiempo", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<Imagen> opciones = new FirestoreRecyclerOptions
                .Builder<Imagen>().setQuery(query, Imagen.class).build();
        adaptador = new AdaptadorNotificacion(getContext(), opciones);
        recyclerNotificaciones.setAdapter(adaptador);
        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        storageRef = FirebaseStorage.getInstance().getReference();
      /*  bajarFichero();


    }


    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                String nombreFichero = UUID.randomUUID().toString();

                subirFichero(data.getData(), "imagenes/" + nombreFichero);
            }
        }
    }

    private void subirFichero(Uri fichero, String referencia) {
        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putFile(fichero);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull
                                          Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("Almacenamiento", "URL: " + downloadUri.toString());
                    registrarImagen("Subida por Móvil", downloadUri.toString());
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo fichero");
                }
            }
        });
    }

   /* private void bajarFichero() {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("imagenes/image");
        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });
    }*/

  /*  private void delete() {
        StorageReference referenciaImagen = storageRef.child("imagenes/image");
        referenciaImagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) { //Error al subir el fichero
            }
        });
    }*/




    @Override public void onStart() {
        super.onStart();
        adaptador.startListening();
    }
    @Override public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }




    //--------------------------------------------------------------------------------------------------
    // HE COMENTADO LO IMPLEMENTADO EN EL ANTERIOR SPRINT PARA PODER HACER UNA PRUEBA DE LA ÚLTIMA PRÁCTICA DE ALMACENAMIENTO
    //--------------------------------------------------------------------------------------------------


/*
    View vista;
    RecyclerView recyclerNotificaciones;


    public AdaptadorNotificacion adaptador;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        RepositorioNotificaciones notificaciones = new NotificacionesLista();
        notificaciones.vaciarNotificaciones();

        //Escuchar base de datos y crear notificacion por cada dato nuevo en bd
        //notificaciones.anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1, TipoNotificacion.ALERTA));
        // notificaciones.anyade(new Notificacion("Timbre", "Nadie ha llamado al timbre", 1, TipoNotificacion.TIMBRE));
        //notificaciones.anyade(new Notificacion("Timbre", "8348543 ha llamado al timbre", 1, TipoNotificacion.ALERTA));



        vista = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerNotificaciones = vista.findViewById(R.id.recyclerId);
        recyclerNotificaciones.setHasFixedSize(true);

        // Show most recent items at the top
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerNotificaciones.setLayoutManager(layoutManager);

        adaptador = new AdaptadorNotificacion(getContext(), FirebaseDatabase.getInstance().getReference().child("logs"));
        recyclerNotificaciones.setAdapter(adaptador);

        notificaciones.vaciarNotificaciones();

        //Escuchar base de datos y crear notificacion por cada dato nuevo en bd
        //notificaciones.anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1, TipoNotificacion.ALERTA));
       // notificaciones.anyade(new Notificacion("Timbre", "Nadie ha llamado al timbre", 1, TipoNotificacion.TIMBRE));
        //notificaciones.anyade(new Notificacion("Timbre", "8348543 ha llamado al timbre", 1, TipoNotificacion.ALERTA));


       /* adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerNotificaciones.getChildAdapterPosition(v);
                mostrar(pos);
            }
        });*/

/*
        return vista;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize Firebase listeners in adapter
        adaptador.startListening();

        // Make sure new events are visible
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerNotificaciones.smoothScrollToPosition(adaptador.getItemCount());
                Log.e("KKKKKKKKKK","Notificacion creada");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // Tear down Firebase listeners in adapter
        adaptador.stopListening();
    }


    private Activity actividad;

    public void mostrar(int pos) {
        Intent i = new Intent(actividad, FotoTimbreActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivity(i);

    }*/

}
