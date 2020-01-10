package com.example.serpumar.androidthings_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.util.ArrayList;

import es.upv.gnd.letslock.androidthings.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AccesoActivity extends Activity {
    private String correo = "";
    EditText correoText;
    EditText pinText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int count = 0;
    EditText pin;
    Button btn;
    Button cancelar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitaracceso);
        //Button btnSolicitar = findViewById(R.id.btnSolicitar);
        //correoText = findViewById(R.id.editTxtCorreo);
        //pinText = findViewById(R.id.editTextCodigo);


        /*btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = stringCorreo.enviarCorreo(AccesoActivity.this, correoText);
                db.collection("Datos").document("Puerta").update("pinInvitado", codigo);
            }
        });*/

        pin = findViewById(R.id.PinText);
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = pin.getText().toString();
                comprobarPinInvitado(codigo);
            }
        });


    }

    public void comprobarPinInvitado(final String pinCod) {
        db.collection("Datos").document("Puerta").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    if (task.getResult().getString("pinInvitado") != null) {

                        String codigo = task.getResult().getString("pinInvitado");


                        Log.d("Pin", codigo);
                        if (pinCod.equals(codigo)) {
                            db.collection("Datos").document("Puerta").update("puerta", true);
                            db.collection("Datos").document("Puerta").update("pinInvitado", null);
                            Toast.makeText(AccesoActivity.this, "PIN CORRECTO, DESBLOQUEANDO PUERTA", Toast.LENGTH_SHORT).show();
                            pin.setText("");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Log.w(TAG, "Error en sleep()", e);
                            }
                            db.collection("Datos").document("Puerta").update("puerta", false);
                            count = 0;
                        } else {
                            if (count == 3) {
                                db.collection("Datos").document("Puerta").update("pinInvitado", null);
                                Toast.makeText(AccesoActivity.this, "PIN INCORRECTO, intentos: " + count + ". Vuelta a solicitar otro código", Toast.LENGTH_SHORT).show();
                                count = 0;
                            } else {
                                count++;
                                Toast.makeText(AccesoActivity.this, "PIN INCORRECTO, intentos: " + count, Toast.LENGTH_SHORT).show();
                            }
                            pin.setText("");
                        }
                    } else {
                        Toast.makeText(AccesoActivity.this, "SOLICITA CÓDIGO PARA PODER ACCEDER", Toast.LENGTH_SHORT).show();
                        pin.setText("");

                    }

                } else {
                    count++;
                    Log.e("Firestore", "Error al leer", task.getException());
                }

            }
        });


    }
}
