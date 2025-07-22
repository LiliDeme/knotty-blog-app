package com.example.laboratoire3;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.laboratoire3.db.DatabaseHelper;

public class WelcomeActivity extends AppCompatActivity {
    private TextView welcomeTextView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeTextView = findViewById(R.id.welcomeTextId);


        //Création d'un comte admin dans le cas où il n'y en a pas
        db = new DatabaseHelper(this);
        if(!db.checkAdminExistance()){
            User userAdmin = new User();
            userAdmin.setUsername("admin");
            userAdmin.setEmail("admin@admin.com");
            userAdmin.setRole("admin");
            userAdmin.setPassword("admin");
            boolean success = db.insertAdmin(userAdmin);
            if(success){
                Toast.makeText(getApplicationContext(), R.string.toast_admin_creation_success_text, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), R.string.toast_admin_creation_data_text, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_admin_creation_failed_text, Toast.LENGTH_LONG).show();
            }
        }

        // Après un délais le texte change puis l'app est démarée
        // handler -> pour exécuter des actions dans le futur ou pour mettre des messages en files d'attente
        // postDelayed -> execute un objet "runnable" après un temps en milisecondes
        // runnable -> le code exécuté après le temps écoulé
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeTextView.setText(R.string.conceptor_text);
                //Change pour l'activité login après 3 secondes
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },2000);
            }
        }, 2000);
    }
}