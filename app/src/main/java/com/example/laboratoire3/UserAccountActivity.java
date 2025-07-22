package com.example.laboratoire3;

import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE_KEY_USER;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

public class UserAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        //Initialisation de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState == null) {
            UserUpdateFragment userUpdateFragment = new UserUpdateFragment();

            // utilisation de bundle pour passer de l'information à un fragment. fonctionne similaire aux intent.putExtra
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_USER_SOURCE,BUNDLE_USER_SOURCE_KEY_USER);
            userUpdateFragment.setArguments(bundle);

            // Demarrer un transcation pour ajouter le fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.accountFragmentContainer, userUpdateFragment);
            transaction.commit();
        }
    }
}