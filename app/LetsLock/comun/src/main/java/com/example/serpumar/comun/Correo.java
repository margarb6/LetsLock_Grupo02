package com.example.serpumar.comun;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class Correo {

    int codigo = 0;
    String mensaje_confirmacion="";

    public Correo (){}

    public int randomCode (int codigo) {
        int min = 1001;
        int max = 9998;
        codigo = new Random().nextInt((max - min) + 1) + min;
        return codigo;

    }

    public String enviarCorreo(Context context, EditText correo) {
        //super.enviarCorreo();
        if (correo.getText().toString().isEmpty()){
            Toast.makeText(context, "Inserte un correo valido", Toast.LENGTH_SHORT).show();
        }else{
            String listaCorreos = correo.getText().toString().trim();
            String [] correos = listaCorreos.split(",");
            //fabio@gmail.com, david@gmail.com
            String asunto = "Letslock: codigo de acceso";
            int codigo_enviado = randomCode(codigo);
            mensaje_confirmacion = "El codigo "+codigo_enviado+ " ha sido enviado a "+listaCorreos;
            String mensaje = "Tu codigo de entrada es "+codigo_enviado;

            JavaMailAPI javaMailAPI = new JavaMailAPI(context, listaCorreos
                    ,asunto,mensaje);
            javaMailAPI.execute();

       /* Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Elige una forma"));*/
        }


        return String.valueOf(codigo_enviado);
    }
}
