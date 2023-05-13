package de.prototype74.neoinfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.prototype74.neoinfo.fragments.CameraFragment;
import de.prototype74.neoinfo.fragments.GeneralFragment;
import de.prototype74.neoinfo.fragments.MiscFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GeneralFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new MiscFragment();
        }
        return new GeneralFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
