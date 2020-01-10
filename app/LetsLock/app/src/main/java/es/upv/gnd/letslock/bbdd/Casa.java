package es.upv.gnd.letslock.bbdd;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Casa {

    private ArrayList<String> idUsuarios;
    private LatLng localizacion;



    public Casa(ArrayList<String> idUsuarios, LatLng localizacion) {

        this.idUsuarios = idUsuarios;
        this.localizacion = localizacion;
    }

    public Casa() {

        this.localizacion = new LatLng(0,0);
        this.idUsuarios = new ArrayList<>();
    }

    public ArrayList<String> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(ArrayList<String> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }


    public LatLng getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(LatLng localizacion) { this.localizacion = localizacion; }

    public void addId(String id){
        this.idUsuarios.add(id);
    }
}
