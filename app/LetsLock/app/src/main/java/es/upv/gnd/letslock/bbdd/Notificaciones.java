package es.upv.gnd.letslock.bbdd;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

                    for (int i = 0; i < docs.size(); i++) {

                        ArrayList<String> idUsuario = (ArrayList<String>) docs.get(i).get("idUsuarios");
                        for (int j = 0; j < idUsuario.size(); j++) {

                            if (idUsuario.get(j).equals(user.getUid()))
                                idCasa = docs.get(i).getId();
                        }
                    }

                    not(callback);

                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }

    private static void not(final NotificacionesCallback callback) {

        Query query = db.collection("notificaciones").orderBy("hora", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {

                    Log.e("Firestore", "Error al leer", e);

                    return;
                }

                ArrayList<Notificacion> notificaciones = new ArrayList<>();
                ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                for (int i = 0; i < docs.size(); i++) {

                    if (Objects.equals(docs.get(i).getString("idCasa"), idCasa)) {

                        ArrayList<String> usuarios = (ArrayList<String>) docs.get(i).get("idUsuarios");
                        notificaciones.add(new Notificacion(docs.get(i).getId(), docs.get(i).getString("tipo"), docs.get(i).getLong("hora"), idCasa, usuarios, i));
                    }
                }

                callback.getNotificacionesCallback(notificaciones);

            }
        });
    }

    static public void setNotificaciones(Notificacion notificacion) {

        HashMap<String, Object> hMap = new HashMap<>();
        hMap.put("hora", notificacion.getHora());
        hMap.put("idCasa", notificacion.getIdCasa());
        hMap.put("idUsuarios", notificacion.getIdUsuarios());
        hMap.put("tipo", notificacion.getTipo());

        db.collection("notificaciones").document(notificacion.getId()).set(hMap);
    }
}