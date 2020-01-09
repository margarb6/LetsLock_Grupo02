package es.upv.gnd.letslock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import es.upv.gnd.letslock.Fragments.ChatFragment;
import es.upv.gnd.letslock.Fragments.PersonasFragment;
import es.upv.gnd.letslock.Fragments.InicioFragment;
import es.upv.gnd.letslock.Fragments.NotificacionesFragment;
import es.upv.gnd.letslock.Fragments.TimbreFragment;
import es.upv.gnd.letslock.bbdd.Usuario;
import es.upv.gnd.letslock.bbdd.Usuarios;
import es.upv.gnd.letslock.bbdd.UsuariosCallback;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Fragment> fragments;
    public static BottomNavigationView navigation;

    private boolean anonimo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        changeTheme();

        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if (savedInstanceState == null) {

            //Establecemos la página de inicio como la primera
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentUtilizado, new InicioFragment()).commit();
            fragments = new ArrayList<>();
            fragments.add(new InicioFragment());
        }

        navigation = findViewById(R.id.BotomNavigationView);

        Usuarios userBD = new Usuarios();

        userBD.getUsuario(new UsuariosCallback() {
            public void getUsuariosCallback(Usuario usuarioBD) {

                //Si no tiene permisos no puede ver el fragment
                if (!usuarioBD.isPermisos() || anonimo) {
                    navigation.getMenu().findItem(R.id.menu_inferior_personas).setVisible(false);
                }

                //Creamos el eventListener que nos permite cambiar de fragment
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            }
        });

        //Inicializamos la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void changeTheme() {

        // configuracion inicial
        SharedPreferences preferenciaNoche = PreferenceManager.getDefaultSharedPreferences(this);

        boolean estaModoNoche = preferenciaNoche.getBoolean("modo_noche", true);


        if (estaModoNoche) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        // leo configuracion y cambio de tema
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        Log.e("QUÉ TEMA MAIN","TEMA:"+ currentNightMode);
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're in day time
                setTheme(R.style.LightTheme);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                setTheme(R.style.DarkTheme);
                break;
        }
    }



    //Inicializa el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Accion que realiza cuando haga click en uno de los elementos del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_cerrarsesion:

                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        MainActivity.this.finish();
                    }
                });
                break;

            case R.id.nav_perfil:

                Intent intent;
                if (!anonimo) {

                    intent = new Intent(this, TabsActivity.class);

                } else {

                    intent = new Intent(this, AnonimoActivity.class);
                }

                startActivity(intent);
                break;


            case R.id.nav_ajustes:

                Intent intent2 = new Intent(this, PreferenciasActivity.class);
                startActivity(intent2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Menú inferior, cambia de fragment cada vez que toque un icono
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragSeleccionado = null;

            switch (item.getItemId()) {

                case R.id.menu_inferior_inicio:

                    fragSeleccionado = new InicioFragment();
                    break;
                case R.id.menu_inferior_timbre:

                    fragSeleccionado = new TimbreFragment();
                    break;
                case R.id.menu_inferior_notificaciones:

                    fragSeleccionado = new NotificacionesFragment();
                    break;
                case R.id.menu_inferior_personas:

                    fragSeleccionado = new PersonasFragment();
                    break;
                case R.id.menu_inferior_chat:

                    fragSeleccionado = new ChatFragment();
                    break;
            }

            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentUtilizado, fragSeleccionado).addToBackStack(null).commit();
            fragments.add(fragSeleccionado);
            return true;
        }
    };

    @Override
    public void onBackPressed() {

        fragments.remove(fragments.size() - 1);

        //Cuando hace click en volver hacia atras checkea el item anterior y establece ese fragment
        if (!fragments.isEmpty()) {

            Fragment fragmentAnterior = fragments.get(fragments.size() - 1);

            if (fragmentAnterior instanceof InicioFragment) {

                Log.i("aa", String.valueOf(navigation.getMenu().findItem(R.id.menu_inferior_inicio).setChecked(true)));

            } else if (fragmentAnterior instanceof TimbreFragment) {

                Log.i("aa", String.valueOf(navigation.getMenu().findItem(R.id.menu_inferior_timbre).setChecked(true)));

            } else if (fragmentAnterior instanceof NotificacionesFragment) {

                Log.i("aa", String.valueOf(navigation.getMenu().findItem(R.id.menu_inferior_notificaciones).setChecked(true)));

            } else if (fragmentAnterior instanceof PersonasFragment) {

                Log.i("aa", String.valueOf(navigation.getMenu().findItem(R.id.menu_inferior_personas).setChecked(true)));

            } else if (fragmentAnterior instanceof PersonasFragment) {

                Log.i("aa", String.valueOf(navigation.getMenu().findItem(R.id.menu_inferior_chat).setChecked(true)));
            }

            super.onBackPressed();

            //Si no hay item anterior cierra la aplicación
        } else {

            MainActivity.this.finishAffinity();
        }

    }

}
