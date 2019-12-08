package es.upv.gnd.letslock.bbdd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Casas {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String idCasa;

    //Establece en la base de datos la casa del usuario
    static public void setCasa(final String idUsuario, Context context) {

        SharedPreferences prefs = context.getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("idCasa")) idCasa = prefs.getString("idCasa", "");

        final Casas casaBD = new Casas();

        casaBD.getCasa(new CasasCallback() {
            @Override
            public void getCasasCallback(Casa casa) {

                ArrayList<String> ids = casa.getIdUsuarios();
                if(!ids.contains(idUsuario)) ids.add(idUsuario);

                db.collection("casa").document(idCasa).set(new Casa(ids));
            }
        });
    }

    //Obtiene el usuario de la base de datos
    static public void getCasa(final CasasCallback callback) {

        db.collection("casa").document(idCasa).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    //Si existe la casa que queremos obtener lo devolvemos mediante un callback(Es asíncrono)
                    if (task.getResult().exists()) {

                        ArrayList<String> idUsuario = (ArrayList<String>) task.getResult().get("idUsuarios");
                        Casa casa = new Casa(idUsuario);
                        callback.getCasasCallback(casa);

                        //Si no existe devolvemos una vacía
                    } else {

                        Casa casa = new Casa();
                        callback.getCasasCallback(casa);
                    }
                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }
}
