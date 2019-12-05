package com.example.serpumar.androidthings_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.serpumar.comun.Datos;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
public class MainActivity extends Activity {


    public Datos dato;
    private ArduinoUart uart = new ArduinoUart("UART0", 9600);
    final String TAG = "Respuesta";
    private static final int INTERVALO = 30000; // Intervalo (ms)
    private Handler handler = new Handler(); // Handler
    public Button upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.i("Prueba", "Lista de UART disponibles: " + ArduinoUart.disponibles());

        try {
            handler.post(runnable); // 3. Llamamos al handler
            Log.d("Prueba","Llega aqui?");
        } catch (Exception e) {
            Log.e(TAG, "Error en PeripheralIO API", e);
        }

        upload = findViewById(R.id.button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatosFirestore(dato);
            }
        });

    }


    private Runnable runnable = new Runnable() {
        @Override public void run() {
            try {
                String distancia = getDistancia();
                Log.d(TAG, "Recibido de Arduino de DISTANCIA: " + distancia);
                String presencia = getPresencia();
                Log.d(TAG, "Recibido de Arduino de PRESENCIA: " + presencia);
                String tag = "2345";
                dato = new Datos(distancia, presencia, tag);
                Log.d("Datos recibidos: ", dato.getDistancia() + dato.getPresencia() + dato.getTag());
                handler.postDelayed(runnable, INTERVALO);
                // 5. Programamos siguiente llamada dentro de INTERVALO ms
            } catch (Exception e) {
                Log.e(TAG, "Error al recibir Datos", e);
            }
        }
    };


    public String getDistancia() {
        uart.escribir("D");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String distancia = uart.leer();
        return distancia;
    }

    public String getPresencia() {
        uart.escribir("P");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String presencia = uart.leer();
        return presencia;
    }

    public String getEtiquetasRFID() {
        uart.escribir("G");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String tag = uart.leer();
        Log.d(TAG, "Recibido de Arduino de RFID: " + tag);
        return tag;
    }

    public void abrirPuerta() {
        uart.escribir("A");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String s = uart.leer();
        Log.d(TAG, "Recibido de Arduino de AbrirPuerta: " + s);
    }

    public void abrirPuertaRFID() {

        String tag = getEtiquetasRFID();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        Log.d(TAG, "EtiquetaRFID: " + tag);
        if(tag.equals(" 04 2A C1 5A 51 59 80 ")) { //Etiqueta 3
            abrirPuerta();
        } else {
            Log.d(TAG, "Este usuario no existe");
        }
    }

    public void enviarDatosFirestore(Datos dato) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Datos").document("Datos").set(dato);

        //db.collection("Datos").document("Datos").update("tag","12345");
    }
}
