package com.example.divisions;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.divisions.ui.home.ClasificacionFragment;
import com.example.divisions.ui.home.PartidosFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    String idLiga;
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,String idLiga) {
        super(fragmentActivity);
        this.idLiga=idLiga;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new ClasificacionFragment(idLiga);
            case 1:
                return new PartidosFragment(idLiga);
            default:
                return new ClasificacionFragment(idLiga);
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
