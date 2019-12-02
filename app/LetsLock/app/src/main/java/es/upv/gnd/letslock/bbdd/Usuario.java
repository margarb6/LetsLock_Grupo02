package es.upv.gnd.letslock.bbdd;

public class Usuario {

    private String nombre;
    private boolean permisos;
    private String pin;

    public Usuario(String nombre, boolean permisos, String pin) {

        this.nombre = nombre;
        this.permisos= permisos;
        this.pin= pin;
    }

    public Usuario(){

        this.nombre= "";
        this.permisos= false;
        this.pin= "";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPermisos() {
        return permisos;
    }

    public void setPermisos(boolean permisos) {
        this.permisos = permisos;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
