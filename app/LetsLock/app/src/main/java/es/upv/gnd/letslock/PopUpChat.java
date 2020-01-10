package es.upv.gnd.letslock;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import es.upv.gnd.letslock.Fragments.ChatFragment;

public class PopUpChat extends Activity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private String user;
    private String name;
    private Boolean permisos = true;
    private Boolean permisosUser = true;
    private String fotoUrl = "";

    private ImageView foto;
    private TextView nombreT;
    private TextView txtPermisos;
    private Button btnPermisos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupchat);

        final String userId = getIntent().getStringExtra("user_id");
        Log.d("User", userId);
        db.collection("usuarios").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    name = task.getResult().getString("nombre");
                    nombreT.setText(name);
                    Log.d("Nombre", name);
                    permisos = task.getResult().getBoolean("permisos");
                    Log.d("Permisos", permisos.toString());
                    txtPermisos.setText(permisos.toString());
                    fotoUrl = task.getResult().getString("fotoUrl");
                }
            }
        });

        db.collection("usuarios").document(usuario.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    permisosUser = task.getResult().getBoolean("permisos");
                    if(permisosUser) {
                        btnPermisos.setVisibility(View.VISIBLE);
                    }
                }
            }
        });



        //LAYOUT Con ImageView, nombre y BUTTON SI TIENE PERMISOS

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.5), (int) (height*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        foto = findViewById(R.id.avatar_pop);
        nombreT = findViewById(R.id.nombre_pup);
        btnPermisos = findViewById(R.id.btnPermisos);
        txtPermisos = findViewById(R.id.perm_result);


        //Imagen

        //Obtenemos la imagen del storage
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Obtenemos la imagen del storage
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ficheroRef = storageRef.child("Fotos_perfil/" + userId);

        ficheroRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Almacenamiento", "Fichero bajado");

                //Comprimir imagen
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedDrawable.setCircular(true);
                foto.setImageDrawable(roundedDrawable);
            }

            //Si no se puede obtener ponemos como predeterminada la del usuario de google
        }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (fotoUrl != null) {
                                DescargarFoto fotoDes = new DescargarFoto(PopUpChat.this, R.id.avatar_pop);
                                fotoDes.execute(fotoUrl);
                            }
                        }
        });

        btnPermisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("usuarios").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            permisos = task.getResult().getBoolean("permisos");
                        }
                    }
                });
                if(permisos) {
                    db.collection("usuarios").document(userId).update("permisos", false);
                    txtPermisos.setText("False");
                } else {
                    db.collection("usuarios").document(userId).update("permisos", true);
                    txtPermisos.setText("True");
                }
            }
        });

    }
}

