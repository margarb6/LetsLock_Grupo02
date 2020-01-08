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

public class Usuarios {

    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Establece en la base de datos el usuario
    static public void setUsuario(Usuario usuario) {

        db.collection("usuarios").document(user.getUid()).set(usuario);
    }

    //Obtiene el usuario de la base de datos
    static public void getUsuario(final UsuariosCallback callback) {

        db.collection("usuarios").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    //Si existe el usuario que queremos obtener lo devolvemos mediante un callback(Es asíncrono)
                    if (task.getResult().exists()) {

                        String nombre = task.getResult().getString("nombre");
                        boolean permisos = task.getResult().getBoolean("permisos");
                        String pin = task.getResult().getString("pin");
                        String fotoUrl = task.getResult().getString("fotoUrl");

                        Usuario usuario = new Usuario(nombre, permisos, pin, fotoUrl);
                        callback.getUsuariosCallback(usuario);

                        //Si no existe devolvemos uno vacío
                    } else {

                        Usuario usuario = new Usuario();
                        callback.getUsuariosCallback(usuario);
                    }
                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }

    static public void getUsuarios(final UsuariosCallback callback) {

        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();
                    ArrayList<String> idUsuario = new ArrayList<>();

                    for (int i = 0; i < docs.size(); i++) {

                        idUsuario.add(docs.get(i).getId());
                    }

                    callback.getAllUsuariosCallback(idUsuario);

                } else {

                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }
}
