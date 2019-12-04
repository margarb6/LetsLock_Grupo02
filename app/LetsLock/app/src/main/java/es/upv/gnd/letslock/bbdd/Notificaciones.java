package es.upv.gnd.letslock.bbdd;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Notificaciones {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static String idCasa;

    static public void getNotificaciones(final NotificacionesCallback callback) {

        db.collection("casa").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();

                    for (int i=0; i< docs.size(); i++){

                        ArrayList<String> idUsuario = (ArrayList<String>) docs.get(i).get("idUsuarios");
                        for (int j=0; j< idUsuario.size(); j++){

                            if(idUsuario.get(i).equals(user.getUid())) idCasa= docs.get(i).getId();
                        }
                    }

                    not(callback);

                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }

    private static void not(final NotificacionesCallback callback){

        db.collection("notificaciones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    ArrayList<Notificacion> notificaciones = new ArrayList<>();

                    ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();

                    for (int i=0; i< docs.size(); i++){

                        if(docs.get(i).getString("idCasa").equals(idCasa)) notificaciones.add(new Notificacion(docs.get(i).getString("tipo"), docs.get(i).getLong("hora"), idCasa));
                    }

                    callback.getNotificacionesCallback(notificaciones);

                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });

    }
}
