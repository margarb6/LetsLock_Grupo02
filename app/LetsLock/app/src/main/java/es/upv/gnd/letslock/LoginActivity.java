package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        //Si está logueado
        if (usuario != null) {

            List<? extends UserInfo> infos = usuario.getProviderData();
            for (UserInfo ui : infos) {

                if(ui.getProviderId()!= "firebase"){

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

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

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

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String nombre= usuario.getDisplayName();

        if(nombre == null)nombre="";

        Toast.makeText(this, "Has iniciado sesion " + nombre, Toast.LENGTH_LONG).show();

        Intent i = new Intent(this, SplashActivity.class);
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
