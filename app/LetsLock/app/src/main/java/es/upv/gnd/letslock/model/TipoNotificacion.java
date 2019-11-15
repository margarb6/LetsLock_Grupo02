package es.upv.gnd.letslock.model;

import es.upv.gnd.letslock.R;

public enum TipoNotificacion {

    ALERTA ("Otros", R.drawable.alerta),
    TIMBRE ("Timbre", R.drawable.timbre);

    private final String texto;
    private final int recurso;

    TipoNotificacion(String texto, int recurso) {
        this.texto = texto;
        this.recurso = recurso;
    }

    public String getTexto() { return texto; }
    public int getRecurso() { return recurso; }


}