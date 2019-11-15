package es.upv.letslock.androidthings;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class TimbreBoardDefaults {
    private static final String DEVICE_RPI3 = "rpi3";

    /**
     * Return the GPIO pin that the Button is connected on.
     */
    public static String getGPIOForButton() {
        if (DEVICE_RPI3.equals(Build.DEVICE)) {
            return "BCM21";
        }
        throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
    }
}