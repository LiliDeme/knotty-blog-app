package com.example.laboratoire3;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoire3.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ListArticlesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    List<ArticleItem> articleItemList;
    private DatabaseHelper db;
    private RelativeLayout mainBackground;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);
        recyclerView = view.findViewById(R.id.recycleViewId);
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
        //pas de barre de division mais sinon:
        // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        // recyclerView.addItemDecoration(dividerItemDecoration);
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
                int id = cursor.getInt(0);
                int userId = cursor.getInt(1);
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

