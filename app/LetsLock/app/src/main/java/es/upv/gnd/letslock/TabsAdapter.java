package es.upv.gnd.letslock;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import es.upv.gnd.letslock.ui.editar_perfil.EditarPerfilFragment;
import es.upv.gnd.letslock.ui.perfil.PerfilFragment;

public class TabsAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){

        switch (position){
            case 0:
                PerfilFragment perfil = new PerfilFragment();
                return perfil;
            case 1:
                EditarPerfilFragment editarPerfil = new EditarPerfilFragment();
                return editarPerfil;

            default:
                return null;
        }
    }
}
