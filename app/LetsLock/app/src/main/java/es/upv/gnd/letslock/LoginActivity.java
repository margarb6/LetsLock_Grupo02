package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 123;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {

        boolean anonimo = true;

        //Si está logueado
        if (usuario != null) {

            List<? extends UserInfo> infos = usuario.getProviderData();

            for (UserInfo ui : infos) {

                if (!ui.getProviderId().equals("firebase")) {

                    anonimo = false;

                    switch (ui.getProviderId()) {

                        //Si se está logueando con contraseña verifica el email
                        case EmailAuthProvider.PROVIDER_ID:

                            verificarEmail();
                            break;

                        default:

                            entrar();
                            break;
                    }
                }
            }
            if (anonimo) cambioActivity("como usuario anónimo");

            //Si no crea la interfaz de login
        } else {

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.applogonombre)
                    .setTheme(R.style.FirebaseUITema)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.AnonymousBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build())).build(), RC_SIGN_IN);

        }
    }

    public void verificarEmail() {

        //Si ya está verificado el email entras en la aplicación
        if (usuario.isEmailVerified()) {

            entrar();

            //Si no envía un correo de verificación
        } else {

            usuario.sendEmailVerification();
            Toast.makeText(this, "Se ha mandado un correo de verificación a " + usuario.getEmail(), Toast.LENGTH_LONG).show();
        }
    }

    public void entrar() {

        final Usuarios userBD = new Usuarios();
        final Casas casaBD = new Casas();

        //Buscamos si existe ese usuario en la base de datos
        userBD.getUsuario(new UsuariosCallback() {
            public void getUsuariosCallback(final Usuario usuarioBD) {

                Random rand = new Random();
                String nombre = usuario.getDisplayName();

                SharedPreferences prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("idCasa", "0");

                //Si no existe lo creamos
                if (usuarioBD.getPin().equals("") && usuarioBD.getNombre().equals("")) userBD.setUsuario(new Usuario(nombre, false, String.format("%04d", rand.nextInt(10000))));
                else nombre = usuarioBD.getNombre();

                editor.putBoolean("permisos", usuarioBD.isPermisos());
                editor.commit();

                casaBD.setCasa(usuario.getUid(), getApplicationContext());

                cambioActivity(nombre);
            }
        });
    }

    void cambioActivity(String nombre) {

        Toast.makeText(LoginActivity.this, "Has iniciado sesion " + nombre, Toast.LENGTH_LONG).show();
        Intent i = new Intent(LoginActivity.this, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {

                login();
                finish();

            } else {

                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (response == null) {

                    Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
                    return;

                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_LONG).show();
                    return;

                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {

                    Toast.makeText(this, "Error desconocido", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

}
