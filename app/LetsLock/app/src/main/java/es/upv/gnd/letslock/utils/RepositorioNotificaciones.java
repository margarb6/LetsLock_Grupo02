package es.upv.gnd.letslock.utils;

import es.upv.gnd.letslock.model.Notificacion;

public interface RepositorioNotificaciones {
    Notificacion elemento(int id); //Devuelve el elemento dado su id
    void anyade(Notificacion notificacion); //Añade el elemento indicado
    int nueva(); //Añade un elemento en blanco y devuelve su id
    void borrar(int id); //Elimina el elemento con el id indicado
    int tamanyo(); //Devuelve el número de elementos
    void actualiza(int id, Notificacion notificacion); //Reemplaza un elemento
    void vaciarNotificaciones(); // vacia la lista de notificaciones
}

