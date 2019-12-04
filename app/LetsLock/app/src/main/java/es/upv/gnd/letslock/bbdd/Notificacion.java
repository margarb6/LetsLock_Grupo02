package es.upv.gnd.letslock.bbdd;

public class Notificacion {

    private String tipo;
    private long hora;
    private String idCasa;

    public Notificacion(String tipo, long hora, String idCasa) {

        this.tipo = tipo;
        this.hora = hora;
        this.idCasa = idCasa;
    }

    public Notificacion(){

        this.tipo= "";
        this.hora= 0;
        this.idCasa= "";
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
}
