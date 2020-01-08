package com.example.serpumar.androidthings_datos;

import android.app.Activity;
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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.serpumar.comun.Datos;
import com.example.serpumar.comun.Imagen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


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
    private static final int INTERVALO_TAG = 30000; // Intervalo (ms)
    private Handler handler = new Handler(); // Handler
    public Button upload;

    public String tag;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String pinPropietario = "";

    private int count = 0;

    //Timbre
    private ButtonInputDriver mButtonInputDriver;


    private DoorbellCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private Handler temporizadorHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Log.i("Prueba", "Lista de UART disponibles: " + ArduinoUart.disponibles());

        update();
        initPIO();

        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

        mCamera = DoorbellCamera.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);


        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        final ScheduledFuture<?> runnableHandle =
                scheduler.scheduleAtFixedRate(runnable, 0, 30, SECONDS);
        final ScheduledFuture<?> runnableRFIDHandle =
                scheduler.scheduleAtFixedRate(runnableRFID, 0, 30, SECONDS);

        /* try {
            handler.post(runnableRFID);
            handler.post(runnable); // 3. Llamamos al handler
            Log.d("Prueba","Llega aqui?");
        } catch (Exception e) {
            Log.e(TAG, "Error en PeripheralIO API", e);
        }*/


    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                String distancia = getDistancia();
                Log.d(TAG, "Recibido de Arduino de DISTANCIA: " + distancia);
                String presencia = getPresencia();
                Log.d(TAG, "Recibido de Arduino de PRESENCIA: " + presencia);
                dato = new Datos(distancia, presencia, tag);
                Log.d("Datos recibidos: ", dato.getDistancia() + dato.getPresencia() + dato.getTag());
                enviarDatosFirestore(dato);
                handler.postDelayed(runnable, INTERVALO);
                // 5. Programamos siguiente llamada dentro de INTERVALO ms
            } catch (Exception e) {
                Log.e(TAG, "Error al recibir Datos", e);
            }
        }
    };

    private Runnable runnableRFID = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "Llega a RFID??");
                tag = getEtiquetasRFID();
                if (!tag.equals("")) {
                    enviarDatosFirestoreTag(tag);
                }
                handler.postDelayed(runnableRFID, INTERVALO_TAG);
                // 5. Programamos siguiente llamada dentro de INTERVALO ms
            } catch (Exception e) {
                Log.e(TAG, "Error al recibir Datos", e);
            }
        }
    };


    public String getDistancia() {
        uart.escribir("D");
        try {
            Thread.sleep(3000);
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
            Thread.sleep(3000);
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        //String s = uart.leer();
        //Log.d(TAG, "Recibido de Arduino de AbrirPuerta: " + s);
    }

    public void cerrarPuerta() {
        uart.escribir("C");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        //String s = uart.leer();
        //Log.d(TAG, "Recibido de Arduino de CerrarPuerta: " + s);
    }

    public void abrirPuertaRFID() {

        String tag = getEtiquetasRFID();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        Log.d(TAG, "EtiquetaRFID: " + tag);
        if (tag.equals(" 04 2A C1 5A 51 59 80 ")) { //Etiqueta 3
            abrirPuerta();
        } else {
            Log.d(TAG, "Este usuario no existe");
        }
    }

    public void enviarDatosFirestore(Datos dato) {
        db.collection("Datos").document("Datos").update("distancia", dato.getDistancia());
        //db.collection("Datos").document("Datos").update("presencia", dato.getPresencia());

        //db.collection("Datos").document("Datos").update("tag","12345");
    }


    public void enviarDatosFirestoreTag(String tag) {
        db.collection("Datos").document("Datos").update("tag", tag);
    }

    private void update() {
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

        db.collection("Datos").document("Datos").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //user = documentSnapshot.toObject(User.class);
                //user.setUid(mAuth.getUid());

                Log.d(TAG, documentSnapshot.get("presencia").toString());
                if (documentSnapshot.getBoolean("presencia")) {
                    Log.d("CAMARA", "Detecta presencia");
                    db.collection("Datos").document("Datos").update("presencia", false);
                }
            }
        });
    }


    private void initPIO() {
        try {
            mButtonInputDriver = new ButtonInputDriver(
                    "BCM21",
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
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();
                    onPictureTaken(imageBytes);
                }
            };

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            String nombreFichero = UUID.randomUUID().toString();
            subirBytes(imageBytes, "imagenes_timbre/" + nombreFichero);

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
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putBytes(bytes);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull
                                          Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw task.getException();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
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
