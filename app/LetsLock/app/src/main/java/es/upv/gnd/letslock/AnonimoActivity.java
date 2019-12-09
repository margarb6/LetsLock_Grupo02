package es.upv.gnd.letslock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

import es.upv.gnd.letslock.Fragments.EditarPerfilFragment;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;

public class AnonimoActivity extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private EditText correo;
    private EditText password;
    private Button registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.anonimo_login);

        correo = findViewById(R.id.correoAnonimo);
        password = findViewById(R.id.passwordAnonimo);
        registrarse = findViewById(R.id.Registrarse);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthCredential credential = EmailAuthProvider.getCredential(correo.getText().toString(), password.getText().toString());

                usuario.linkWithCredential(credential).addOnCompleteListener(AnonimoActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Usuarios userBD = new Usuarios();
                            Random rand = new Random();
                            userBD.setUsuario(new Usuario(user.getDisplayName(), false, String.format("%04d", rand.nextInt(10000))));
                            SharedPreferences prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("anonimo", false);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Log.w("Login", "linkWithCredential:failure", task.getException());
                            Toast.makeText(AnonimoActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }                                // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
            }
        });
    }

    //Cuando hace click en volver hacia atras checkea el item anterior y establece ese fragment
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
