package es.upv.gnd.letslock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioDatosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_datos);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {

            Button cambiarEditar = findViewById(R.id.editar);

            cambiarEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i= new Intent(getApplicationContext(), UsuarioEditarActivity.class);
                    startActivity(i);
                }
            });

            TextView nombreTxt = findViewById(R.id.nombre);
            String nombre = usuario.getDisplayName();
            nombreTxt.setText(nombre);

            TextView correoTxt = findViewById(R.id.correo);
            String correo = usuario.getEmail();
            nombreTxt.setText(correo);

            TextView telefonoTxt = findViewById(R.id.telefono);
            String telefono = usuario.getPhoneNumber();
            telefonoTxt.setText(telefono);

            //Queda convertir la uri a foto

            Uri urlFoto = usuario.getPhotoUrl();
            ImageView foto = findViewById(R.id.foto);

            //Poner logos guays

            String proveedor = usuario.getProviderId();

            ImageView logo = findViewById(R.id.logo);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) logo.getLayoutParams();

            switch (proveedor){

                case "facebook.com":

                    logo.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                    params.width = 120;
                    logo.setLayoutParams(params);
                    return;

                case "google.com":

                    logo.setImageDrawable(getResources().getDrawable(R.drawable.google_icon));
                    params.width= 120;
                    logo.setLayoutParams(params);
                    return;

                case "twitter.com":

                    logo.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon));
                    params.width = 120;
                    logo.setLayoutParams(params);
                    return;

                case "phone":
//buscar logo
                    logo.setImageDrawable(getResources().getDrawable(R.drawable.common_full_open_on_phone));
                    params.width = 120;
                    logo.setLayoutParams(params);
                    return;

                default:
//Correo anónimo (buscar logo)
                    logo.setImageDrawable(getResources().getDrawable(R.drawable.gmail_icon));
                    params.width = 120;
                    logo.setLayoutParams(params);
                    return;
            }
        }

        ImageView logo = findViewById(R.id.logo);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) logo.getLayoutParams();

        String proveedor= "facebook.com";

        switch (proveedor){

            case "facebook.com":

                logo.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                params.width = 120;
                logo.setLayoutParams(params);
                return;

            case "google.com":

                logo.setImageDrawable(getResources().getDrawable(R.drawable.google_icon));
                params.width= 120;
                logo.setLayoutParams(params);
                return;

            case "twitter.com":

                logo.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon));
                params.width = 120;
                logo.setLayoutParams(params);
                return;

            case "phone":
//buscar logo
                logo.setImageDrawable(getResources().getDrawable(R.drawable.common_full_open_on_phone));
                params.width = 120;
                logo.setLayoutParams(params);
                return;

            default:
//Correo anónimo (buscar logo)
                logo.setImageDrawable(getResources().getDrawable(R.drawable.gmail_icon));
                params.width = 120;
                logo.setLayoutParams(params);
                return;
        }
    }
}

