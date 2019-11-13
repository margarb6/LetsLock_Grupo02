package es.upv.gnd.letslock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import es.upv.gnd.letslock.Fragments.EditarPerfilFragment;
import es.upv.gnd.letslock.Fragments.PerfilFragment;

public class TabsActivity extends AppCompatActivity {

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
}
