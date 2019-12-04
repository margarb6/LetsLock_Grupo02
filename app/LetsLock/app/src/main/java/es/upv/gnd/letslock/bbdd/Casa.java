package es.upv.gnd.letslock.bbdd;

import java.util.ArrayList;

public class Casa {

    private ArrayList<String> idUsuarios;

    public Casa(ArrayList<String> idUsuarios) {

        this.idUsuarios = idUsuarios;
    }

    public Casa() {

        this.idUsuarios = new ArrayList<>();
    }

    public ArrayList<String> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(ArrayList<String> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }
}
