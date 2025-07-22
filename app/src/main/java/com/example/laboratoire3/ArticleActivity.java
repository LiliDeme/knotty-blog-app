package com.example.laboratoire3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class ArticleActivity extends ToolbarSharedActivities {
    public static final String ARTICLE_ACTION = "article_action";
    public static final String ARTICLE_ID = "article_id";
    public static final String ARTICLE_ACTION_VALUE_ADD = "add";
    public static final String ARTICLE_ACTION_VALUE_SHOW = "show";
    public static final String ARTICLE_ACTION_VALUE_MODIFY = "modify";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        //Initialisation de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState == null) {
            ManageArticleFragment manageArticleFragment = new ManageArticleFragment();

            // peu importe la source, on récupère l'action passé en intent, ce qui déterminera comment le fragment
            // manageArticleFragment se comportera
            Intent intent = getIntent();
            String articleAction = intent.getStringExtra(ARTICLE_ACTION);
            int articleId = intent.getIntExtra(ARTICLE_ID, -1);//-1 si aucun id n'ont été donné


            // utilisation de bundle pour passer de l'information à un fragment. fonctionne similaire aux intent.putExtra
            Bundle bundle = new Bundle();
            bundle.putString(ARTICLE_ACTION,articleAction);
            bundle.putInt(ARTICLE_ID, articleId);
            manageArticleFragment.setArguments(bundle);

            // Demarrer un transcation pour ajouter le fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.articleFragmentContainer, manageArticleFragment);
            transaction.commit();
        }
    }
}