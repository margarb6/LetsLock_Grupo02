package com.example.serpumar.androidthings_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chaos.view.PinView;
import com.example.serpumar.comun.Datos;
import com.example.serpumar.comun.Imagen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;




import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
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
public class MainActivity extends Activity {


    public Datos dato;
    private ArduinoUart uart = new ArduinoUart("UART0", 9600);
    final String TAG = "Respuesta";
    private static final int INTERVALO = 30000; // Intervalo (ms)
    private Handler handler = new Handler(); // Handler
    public Button upload;


    //Timbre
    private ButtonInputDriver mButtonInputDriver;


    private DoorbellCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private Handler temporizadorHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.i("Prueba", "Lista de UART disponibles: " + ArduinoUart.disponibles());

        try {
            handler.post(runnable); // 3. Llamamos al handler
            Log.d("Prueba", "Llega aqui?");
        } catch (Exception e) {
            Log.e(TAG, "Error en PeripheralIO API", e);
        }

        // upload = findViewById(R.id.button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatosFirestore(dato);
            }
        });


        //Timbre

        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

        initPIO();

        mCamera = DoorbellCamera.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);

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

    private void initPIO() {
        try {
            mButtonInputDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForButton(),
                    com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_ENTER);
            mButtonInputDriver.register();
        } catch (IOException e) {
            mButtonInputDriver = null;
            Log.w("gpio pins", "Could not open GPIO pins", e);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Doorbell rang!
            Log.d("btn pressed", "button pressed");
            mCamera.takePicture();
            return true;
        }


        return super.onKeyUp(keyCode, event);
    }



    static void registrarImagen(String titulo, String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Imagen imagen = new Imagen(titulo, url);
        db.collection("imagenes_timbre").document().set(imagen);
    }
    /* private Runnable tomaFoto = new Runnable() {
         @Override public void run() {
             mCamera.takePicture();
             temporizadorHandler.postDelayed(tomaFoto, 60 * 1000);
             //Programamos siguiente llamada dentro de 60 segundos
         }
     };*/
    private ImageReader.OnImageAvailableListener
            mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes); image.close();
                    onPictureTaken(imageBytes);
                }
            };

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            String nombreFichero = UUID.randomUUID().toString();
            subirBytes(imageBytes, "imagenes_timbre/"+nombreFichero);

            final Bitmap bitmap = BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.length);
            /*runOnUiThread(new Runnable() {
                @Override public void run() {
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);
                }
            });*/
        }
    }
    private void subirBytes(final byte[] bytes, String referencia) {
        final StorageReference storageRef  = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putBytes(bytes);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override public Task<Uri> then(@NonNull
                                                    Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw task.getException();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("Almacenamiento", "URL: " + downloadUri.toString());
                    registrarImagen("Subida por R.P.", downloadUri.toString());
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo bytes");
                }
            }
        });
    }




}
