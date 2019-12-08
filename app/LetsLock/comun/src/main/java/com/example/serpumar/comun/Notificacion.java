package com.example.serpumar.comun;

import java.util.ArrayList;

public class Notificacion {

    private String id;
    private String tipo;
    private long hora;
    private String idCasa;
    private ArrayList<String> idUsuarios;
    private int position;

    public Notificacion(String id, String tipo, long hora, String idCasa, ArrayList<String> idUsuarios, int position) {

        this.id= id;
        this.tipo = tipo;
        this.hora = hora;
        this.idCasa = idCasa;
        this.idUsuarios = idUsuarios;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public String getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(String idCasa) {
        this.idCasa = idCasa;
    }

    public ArrayList<String> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(ArrayList<String> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
