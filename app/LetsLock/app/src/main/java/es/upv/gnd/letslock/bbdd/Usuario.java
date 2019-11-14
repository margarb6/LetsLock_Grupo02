package es.upv.gnd.letslock.bbdd;

public class Usuario {

    private String nombre;
    private boolean permisos;
    private String Tag;
    private String id;

    public Usuario(String nombre, boolean permisos, String Foto, String Tag, String id) {

        this.nombre = nombre;
        this.permisos= permisos;
        this.Tag= Tag;
        this.id= id;
    }

    public Usuario(String nombre, String id, boolean permisos) {

        this.nombre= nombre;
        this.permisos= permisos;
        this.Tag= "";
        this.id= id;
    }

    public Usuario(){

        this.nombre= "";
        this.permisos= false;
        this.Tag= "";
        this.id= "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isPermisos() {
        return permisos;
    }

    public void setPermisos(boolean permisos) {
        this.permisos = permisos;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
