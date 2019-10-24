package es.upv.gnd.letslock;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import es.upv.gnd.letslock.ui.gallery.GalleryFragment;
import es.upv.gnd.letslock.ui.home.HomeFragment;
import es.upv.gnd.letslock.ui.slideshow.SlideshowFragment;

public class tabPagerAdapter extends FragmentStatePagerAdapter {

    String[] tabarray = new String[] {"Inicio","Notificaciones","Camara"};
    Integer tabnumber = 3;

    public tabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabarray[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                HomeFragment inicio = new HomeFragment();
                return inicio;
            case 1:
                GalleryFragment notificaciones = new GalleryFragment();
                return notificaciones;
            case 2:
                SlideshowFragment camara = new SlideshowFragment();
                return camara;
        }


        return null;
    }

    @Override
    public int getCount() {
        return tabnumber;
    }
}
