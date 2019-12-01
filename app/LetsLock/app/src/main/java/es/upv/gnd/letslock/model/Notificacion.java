package es.upv.gnd.letslock.model;

import android.util.Log;

import java.util.Map;

public class Notificacion {



    private String nombre;
    private long fecha;
    private String descripcion;
    private int id;
    private TipoNotificacion tipo;
    private String foto;
    Map<String, Float> annotations;


    public Map<String, Float> getAnnotations() {
        return annotations;
    }


    public Notificacion(String nombre, long fecha, String descripcion, int id) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id = id;
    }

    public Notificacion(String nombre, long fecha, String descripcion, int id, TipoNotificacion tipo) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id = id;
        this.tipo = tipo;
    }

    public Notificacion(String nombre, String descripcion, int id, TipoNotificacion tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.id = id;

        Log.e("TIPO:", tipo.getTexto());
        this.tipo = tipo;
    }


    public Notificacion(String nombre, String descripcion, int id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.id = id;
    }

    public Notificacion() {
        this.nombre = "ejemplo";
        this.fecha = System.currentTimeMillis();
        this.descripcion = "Esto es una descripción";
        this.id = 0;
    }
    public Notificacion(String nombre, long fecha, String descripcion, int id, String foto) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id = id;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "nombre='" + nombre + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", id=" + id +
                ", tipo=" + tipo.getTexto() + " " + tipo.getRecurso() +
                '}';
    }
}
