package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FormularioActivity extends Activity {


    static public EditText edit_nombre;
    static public EditText edit_asunto;
    static public EditText edit_mensaje;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_email);
        edit_nombre = findViewById(R.id.edit_correo);
        edit_asunto = findViewById(R.id.edit_asunto);
        edit_mensaje = findViewById(R.id.edit_mensaje);

        Button boton_enviar = findViewById(R.id.boton_enviar);
        boton_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCorreo();
            }
        });

    }

    public void enviarCorreo() {
        String listaCorreos = edit_nombre.getText().toString();
        String[] correos = listaCorreos.split(",");
        //fabio@gmail.com, david@gmail.com
        String asunto = edit_asunto.getText().toString();
        String mensaje = edit_mensaje.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Elige una forma"));
    }
}