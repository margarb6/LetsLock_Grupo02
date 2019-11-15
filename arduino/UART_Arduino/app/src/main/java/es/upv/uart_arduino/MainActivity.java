package es.upv.uart_arduino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

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

    private ArduinoUart uart = new ArduinoUart("UART0", 9600);
    final String TAG = "Respuesta";
    public String distancia;
    public String presencia;
    public String etiquetaRFID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        getDistancia();
        getPresencia();
        //getEtiquetasRFID();
        //abrirPuerta();
        abrirPuertaRFID();
    }
    public void getDistancia() {
        uart.escribir("D");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        distancia = uart.leer();
        Log.d(TAG, "Recibido de Arduino de DISTANCIA: " + distancia);
    }

    public void getPresencia() {
        uart.escribir("P");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        presencia = uart.leer();
        Log.d(TAG, "Recibido de Arduino de PRESENCIA: " + presencia);
    }

    public void getEtiquetasRFID() {
        uart.escribir("G");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        etiquetaRFID = uart.leer();
        Log.d(TAG, "Recibido de Arduino de RFID: " + etiquetaRFID);
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

        getEtiquetasRFID();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        Log.d(TAG, "EtiquetaRFID: " + etiquetaRFID);
        if(etiquetaRFID.equals(" 04 2A C1 5A 51 59 80 ")) {
            abrirPuerta();
        } else {
            Log.d(TAG, "Este usuario no existe");
        }
    }
}
