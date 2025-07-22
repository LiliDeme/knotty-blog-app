package com.example.laboratoire3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends ToolbarSharedActivities {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // -- initialiser viewpager et tablayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        PageAdapter pageAdapter = new PageAdapter(this);
        viewPager.setAdapter(pageAdapter);



        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(R.string.menu_home_text);
                    tab.setIcon(R.drawable.ic_list);
                    break;
                case 1:
                    tab.setText(R.string.tab_library_text);
                    tab.setIcon(R.drawable.ic_library_black);
                    break;
                case 2:
                    tab.setText(R.string.menu_profil_text);
                    tab.setIcon(R.drawable.ic_profil_black);
                    break;
            }
        })).attach();
    }
}