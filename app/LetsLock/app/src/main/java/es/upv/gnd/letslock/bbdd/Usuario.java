package es.upv.gnd.letslock.bbdd;

public class Usuario {

    private String nombre;
    private boolean permisos;
    private String pin;
    private String fotoUrl;

    public Usuario(String nombre, boolean permisos, String pin, String fotoUrl) {

        this.nombre = nombre;
        this.permisos = permisos;
        this.pin = pin;
        this.fotoUrl = fotoUrl;
    }

    public Usuario() {

        this.nombre = "";
        this.permisos = false;
        this.pin = "";
        this.fotoUrl = "";
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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String foto) {
        this.fotoUrl = fotoUrl;
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
