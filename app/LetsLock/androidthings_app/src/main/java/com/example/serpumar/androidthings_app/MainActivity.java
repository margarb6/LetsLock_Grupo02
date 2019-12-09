package com.example.serpumar.androidthings_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.chaos.view.PinView;
import com.example.serpumar.comun.Datos;
import com.example.serpumar.comun.Imagen;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import es.upv.gnd.letslock.androidthings.R;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends AppCompatActivity {


    public Datos dato;
    private ArduinoUart uart = new ArduinoUart("UART0", 9600);
    final String TAG = "Respuesta";
    private static final int INTERVALO = 30000; // Intervalo (ms)
    private static final int INTERVALO_TAG = 1000; // Intervalo (ms)
    private Handler handler = new Handler(); // Handler
    public Button upload;

    public String tag;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    String pinPropietario = "";

    Pinview pinview;
    private int count = 0;
    Button camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);


        Log.i("Prueba", "Lista de UART disponibles: " + ArduinoUart.disponibles());

        pinview = findViewById(R.id.pinview);

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                Toast.makeText(MainActivity.this, pinview.getValue(), Toast.LENGTH_SHORT).show();
                comprobarPin(pinview.getValue());
            }
        });

        //enterPin();
        Button btnSolicitarPin = findViewById(R.id.btnSolicitarCodigo);
        btnSolicitarPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AccesoActivity.class);
                startActivity(i);
            }
        });

    }

    public void enviarDatosFirestore(Datos dato) {
        db.collection("Datos").document("Datos").update("distancia", dato.getDistancia());
        db.collection("Datos").document("Datos").update("presencia", dato.getPresencia());

        //db.collection("Datos").document("Datos").update("tag","12345");
    }



    public void enviarDatosFirestoreTag(String tag) {
        db.collection("Datos").document("Datos").update("tag", tag);
    }

    /*private void update() {
        db.collection("Datos").document("Puerta").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //user = documentSnapshot.toObject(User.class);
                //user.setUid(mAuth.getUid());
                if (documentSnapshot.getBoolean("puerta")) {
                    abrirPuerta();
                    Log.d("Puerta", "Puerta abierta");
                } else if (!documentSnapshot.getBoolean("puerta")) {
                    cerrarPuerta();
                    Log.d("Puerta", "Puerta cerrada");

                }
            }
        });
    }*/

    public void comprobarPin(final String pin) {
        db.collection("Datos").document("Puerta").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //Si consigue leer en Firestore
                if (task.isSuccessful()) {

                    String codigo = task.getResult().getString("pin");


                    Log.d("Pin", codigo);
                    if (pin.equals(codigo)) {
                        db.collection("Datos").document("Puerta").update("puerta", true);
                        Toast.makeText(MainActivity.this, "PIN CORRECTO, DESBLOQUEANDO PUERTA", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Log.w(TAG, "Error en sleep()", e);
                        }
                        db.collection("Datos").document("Puerta").update("puerta", false);
                        count = 0;
                    } else {
                        if (count == 3) {
                            Toast.makeText(MainActivity.this, "PIN INCORRECTO, intentos: " + count, Toast.LENGTH_SHORT).show();
                            count = 0;
                        } else {
                            count++;
                            Toast.makeText(MainActivity.this, "PIN INCORRECTO, intentos: " + count, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

}
