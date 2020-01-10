package es.upv.gnd.letslock.bbdd;

import java.util.ArrayList;

public interface UsuariosCallback {

    void getUsuariosCallback(Usuario usuario);
    void getAllUsuariosCallback(ArrayList<String> idUsuarios, ArrayList<Usuario> usuarios);
}
