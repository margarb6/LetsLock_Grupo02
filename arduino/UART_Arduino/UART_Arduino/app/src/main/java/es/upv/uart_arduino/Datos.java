package es.upv.uart_arduino;

public class Datos {

    private String distancia;
    private String presencia;
    private String tag;

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getPresencia() {
        return presencia;
    }

    public void setPresencia(String presencia) {
        this.presencia = presencia;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Datos (String distancia, String presencia, String tag) {
        this.distancia = distancia;
        this.presencia = presencia;
        this.tag = tag;
    }
}
