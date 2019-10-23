package es.upv.gnd.letslock;

import android.app.ProgressDialog;
<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> fabio
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText TextEmail;
    private EditText TextPassword;
    private ProgressDialog progressDialog;
    private Button btnLogin;


    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        TextEmail = (EditText) findViewById(R.id.TxtEmail);
        TextPassword = (EditText) findViewById(R.id.TxtPassword);

        Button btnRegistrar = (Button) findViewById(R.id.botonRegistrar);
        Button btnLogin = (Button) findViewById(R.id.botonIniciarSesion);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        btnRegistrar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void registrarUsuario(){

        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = TextEmail.getText().toString().trim();
        String password  = TextPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Falta insertar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta escribir la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Procesando nuevo usuario...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this,"Se ha registrado el usuario con el email: "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                        }else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) { //si ya existe el user
                                Toast.makeText(LoginActivity.this, "Este usuario ya está registrado", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(LoginActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void loguearUsuario (){

        //Obtenemos el email y la contraseña desde las cajas de texto
<<<<<<< HEAD
        String email = TextEmail.getText().toString().trim();
=======
        final String email = TextEmail.getText().toString().trim();
>>>>>>> fabio
        String password  = TextPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Falta insertar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta escribir la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


<<<<<<< HEAD
        progressDialog.setMessage("Procesando nuevo usuario...");
=======
        progressDialog.setMessage("Cargando...");
>>>>>>> fabio
        progressDialog.show();

        //loging a new user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this,"Bienvenido "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
<<<<<<< HEAD
=======
                            Intent intent = new Intent (getApplication(),MainActivity.class);
                            intent.putExtra(MainActivity.user, email);
                            startActivity(intent);

>>>>>>> fabio
                        }else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) { //si ya existe el user
                                Toast.makeText(LoginActivity.this, "Este usuario ya está registrado", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(LoginActivity.this, "No se pudo loguear el usuario ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.botonRegistrar:
                //Invocamos al método:
                registrarUsuario();
                break;
        }
        switch (view.getId()){
            case R.id.botonIniciarSesion:
                //Invocamos al método:
                loguearUsuario();
                break;
        }



    }
}