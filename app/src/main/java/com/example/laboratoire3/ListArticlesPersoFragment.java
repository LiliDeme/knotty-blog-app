package com.example.laboratoire3;

import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoire3.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ListArticlesPersoFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    List<ArticleItem> articleItemList;
    private DatabaseHelper db;
    private int activeUserId;
    private SharedPreferences userSharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);
        recyclerView = view.findViewById(R.id.recycleViewId);
        userSharedPref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        activeUserId = userSharedPref.getInt(SHARED_PREF_KEY_USER_ID, -1);
        //liste d'articles et database
        articleItemList = new ArrayList<>();
        db = new DatabaseHelper(getActivity());


        //adapteur
        articleAdapter = new ArticleAdapter(articleItemList);

        //récupérer tous les articles
        loadArticlesFromDatabase();

        //LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // attacher l adapter a notre recyclerview
        recyclerView.setAdapter(articleAdapter);
        return view;
    }

    public void loadArticlesFromDatabase(){
        articleItemList.clear();
        Cursor cursor = db.getAllArticles();

        if(cursor.getCount() == 0){
            //Toast.makeText(getActivity(),R.string.toast_articles_not_found_text, Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()){

                int userId = cursor.getInt(1);
                if (userId == activeUserId){
                    int id = cursor.getInt(0);
                    String title = cursor.getString(2);
                    String body = cursor.getString(3);
                    String preview = cursor.getString(4);
                    String date = cursor.getString(5);
                    String imagePath = cursor.getString(6);
                    int viewNbr = cursor.getInt(7);

                    ArticleItem article = new ArticleItem();
                    article.setId(id);
                    article.setUserId(userId);
                    article.setTitle(title);
                    article.setContent(body);
                    article.setPreview(preview);
                    article.setPublicationDate(date);
                    article.setImagePath(imagePath);
                    article.setViewNumber(viewNbr);

                    articleItemList.add(article);
                }
            }
            cursor.close();
            //Notifier l'adapteur que les données ont changées
            articleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadArticlesFromDatabase();
    }
}
