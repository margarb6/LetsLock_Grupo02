package es.upv.medidor_ultrasonico_uart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

    final String TAG = "Respuesta";
    private ArduinoUart uart = new ArduinoUart("UART0", 9600);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        //ArduinoUart uart2 = new ArduinoUart("USB1-1.5:1.0", 9600);
        Log.d(TAG, "Mandado a Arduino: D y P");
        leerUltrasonico();
        leerPIR();
        //leerHora();
    }

    void leerUltrasonico() {
        uart.escribir("D");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e); }
        String s = uart.leer();
        Log.d(TAG, "Recibido de Arduino Prog_ultrasonico: "+s);
    }

    void leerPIR() {
        uart.escribir("P");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e); }
        String p = uart.leer();
        Log.d(TAG, "Recibido de Arduino Prog_PIR: "+p);
    }

    /*void leerHora() {
        uart.escribir("T");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e); }
        String t = uart.leer();
        Log.d(TAG, "Fecha y Hora: "+t);
    }*/
}
