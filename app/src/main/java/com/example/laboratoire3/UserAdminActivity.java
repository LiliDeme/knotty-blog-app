package com.example.laboratoire3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class UserAdminActivity extends ToolbarSharedActivities {
    public static final String BUNDLE_ADMIN_VIEW = "admin_view";
    public static final String BUNDLE_ADMIN = "bundle_admin";
    public static final String BUNDLE_USER_ID_MANAGE = "bundle_user_id";
    public static final String BUNDLE_USER_ITEM = "bundle_user_item";
    public static final String BUNDLE_USER_SOURCE = "bundle_user_source";
    public static final String BUNDLE_USER_SOURCE_KEY_ADMIN = "admin";
    public static final String BUNDLE_USER_SOURCE_KEY_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);

        //Initialisation de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState == null) {
            ListUsersFragment listUsersFragment = new ListUsersFragment();

            // Demarrer un transcation pour ajouter le fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_user_admin, listUsersFragment);
            transaction.commit();
        }

    }
}