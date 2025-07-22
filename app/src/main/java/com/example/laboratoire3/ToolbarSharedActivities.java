package com.example.laboratoire3;

import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_ADD;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ROLE;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


// La barre d'outils est utilisée partout sauf dans le login, c'est pourquoi elle à sa propre classe pour
// ne pas à avoir à la comfigurer dans chaque activités. On fait simplement extend ToolbarSharedActivities
// dans les Activitée où on la désire. Ici on fait extends AppCompatActivity pour que les classes filles
// (soit, les différentes activité) en hérite.
public class ToolbarSharedActivities extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String activeUserRole = sharedPref.getString(SHARED_PREF_KEY_USER_ROLE, "user");
        MenuItem accountListItem = menu.findItem(R.id.action_manage_users);
        if (!activeUserRole.equals("admin")){
            accountListItem.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add){
            //Ajout d'un article
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ARTICLE_ACTION, ARTICLE_ACTION_VALUE_ADD);
            startActivity(intent);
        }
        if(id == R.id.action_home){
            //retour à la liste
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if(id == R.id.action_profil){

            //TODO doit allez directement dans l'onglet du view pager où le profil est afficher (fragment profil)

        }

        if(id == R.id.action_manage_users){
            Intent intent = new Intent(this, UserAdminActivity.class);
            startActivity(intent);
        }

        if(id == R.id.action_logout){
            showLogoutConfirmDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void showLogoutConfirmDialog(){
        AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(this);
        logoutDialogBuilder.setTitle(R.string.alert_dialog_logout_title_text)
                .setMessage(R.string.alert_dialog_logout_message_text)
                .setPositiveButton(R.string.alert_dialog_logout_positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_logout_negative_button_text, null);

                AlertDialog dialog = logoutDialogBuilder.create();
                dialog.show();
    }
}
