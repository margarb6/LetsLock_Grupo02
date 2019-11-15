package es.upv.gnd.letslock.model;

import android.util.Log;

public class Notificacion {
    private String nombre;
    private long fecha;
    private String descripcion;
    private int id;
    private TipoNotificacion tipo;


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
