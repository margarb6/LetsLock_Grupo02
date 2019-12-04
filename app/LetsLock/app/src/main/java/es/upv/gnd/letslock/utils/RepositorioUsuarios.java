package es.upv.gnd.letslock.utils;

import es.upv.gnd.letslock.bbdd.Usuario;

public interface RepositorioUsuarios {
        Usuario elemento(int id); //Devuelve el elemento dado su id
        void anyade(Usuario usuario); //Añade el elemento indicado
        int nueva(); //Añade un elemento en blanco y devuelve su id
        void borrar(int id); //Elimina el elemento con el id indicado
        int tamanyo(); //Devuelve el número de elementos
        void actualiza(int id, Usuario usuario); //Reemplaza un elemento
}
