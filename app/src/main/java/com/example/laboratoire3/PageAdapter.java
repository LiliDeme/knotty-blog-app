package com.example.laboratoire3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PageAdapter extends FragmentStateAdapter {


    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return  new ListArticlesFragment();
            case 1:
                return new ListArticlesPersoFragment();
            case 2:
                return new UserAccountFragment();
            default:
                return new ListArticlesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
