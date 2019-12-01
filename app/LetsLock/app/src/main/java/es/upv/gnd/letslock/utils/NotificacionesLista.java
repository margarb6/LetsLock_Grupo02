package es.upv.gnd.letslock.utils;

import java.util.ArrayList;
import java.util.List;

import es.upv.gnd.letslock.model.Notificacion;


public class NotificacionesLista implements RepositorioNotificaciones {
    protected List<Notificacion> listaNotificaciones;

    public NotificacionesLista() {
        listaNotificaciones = new ArrayList<Notificacion>();
    }

    public Notificacion elemento(int id) {
        return listaNotificaciones.get(id);
    }

    public void anyade(Notificacion notificacion) {
        listaNotificaciones.add(notificacion);
    }

    public int nueva() {
        Notificacion notificacion = new Notificacion();
        listaNotificaciones.add(notificacion);
        return listaNotificaciones.size() - 1;
    }

    public void borrar(int id) {
        listaNotificaciones.remove(id);
    }

    public int tamanyo() {
        return listaNotificaciones.size();
    }

    public void actualiza(int id, Notificacion notificacion) {
        listaNotificaciones.set(id, notificacion);
    }

    public void anyadeEjemplos() {
        anyade(new Notificacion("Timbre", "Alguien ha llamado al timbre", 1));
        anyade(new Notificacion("Alerta", "La puerta no se ha cerrado correctamente", 2));
        anyade(new Notificacion("Alerta", "El sistema RFID ha fallado", 3));
    }
}

