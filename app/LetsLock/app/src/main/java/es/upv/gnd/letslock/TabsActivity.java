package es.upv.gnd.letslock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

import es.upv.gnd.letslock.Fragments.EditarPerfilFragment;
import es.upv.gnd.letslock.Fragments.PerfilFragment;

public class TabsActivity extends AppCompatActivity {

    //Request code de las fotos
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        //Inicializamos la toolbar

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(getSupportFragmentManager()));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    //Inicializa el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Cuando hace click en volver hacia atras checkea el item anterior y establece ese fragment
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    public class MiPagerAdapter extends FragmentPagerAdapter {

        public MiPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override public Fragment getItem(int position) {

            Fragment fragment = null;
            switch (position) {

                case 0: fragment = new PerfilFragment();
                    break;

                case 1: fragment = new EditarPerfilFragment();
                    break;
            }

            return fragment;
        }

        @Override public int getCount() {

            return 2;
        }

        @Override public CharSequence getPageTitle(int position) {

            switch (position) {

                case 0: return "Perfil";
                case 1: return "Editar Perfil";
            }
            return null;
        }
    }

    //TODO recibe data vacía con la foto de la cámara

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri foto= Uri.parse("content://");
        if (requestCode == RESULTADO_FOTO) { if (resultCode == Activity.RESULT_OK && foto!=null) {

            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }

        //Si funciona coge una foto de la galería y se realiza correctamente establecemos la uri de la foto a la de la galería
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                foto = data.getData();

            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        }

        try {

            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),foto);
            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(this.getResources(), bitmap);
            roundedDrawable.setCornerRadius(bitmap.getHeight());
            roundedDrawable.setCircular(true);
            ImageView a= findViewById(R.id.FotoEditar);
            a.setImageDrawable(roundedDrawable);
            SharedPreferences prefs = getSharedPreferences("Foto_perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("image", "" + foto);
            editor.commit();

        }catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }
}
