package com.example.laboratoire3;

import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_SHOW;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ID;

import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoire3.db.DatabaseHelper;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    private List<ArticleItem> articleList;
    private int activeUserId;
    private SharedPreferences userSharedPref;

    public ArticleAdapter(List<ArticleItem> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        userSharedPref = view.getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        activeUserId = userSharedPref.getInt(SHARED_PREF_KEY_USER_ID, -1);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleItem articleItem = articleList.get(position);
        String imgPath = articleItem.getImagePath();
        holder.textViewArticleTitle.setText(articleItem.getTitle());
        holder.textViewPreview.setText(articleItem.getPreview());
        holder.textViewVisitNbr.setText(String.valueOf(articleItem.getViewNumber()));
        if (imgPath == null){
            imgPath = "";
        }
        if (!imgPath.isEmpty()) {
            holder.ImageViewArticleImage.setImageURI(Uri.parse(imgPath));
        } else {
            holder.ImageViewArticleImage.setImageResource(R.drawable.default_background);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // RECUPERER L'ID DE L'ARTICLE EN QUESTION et afficher l'article qui est dans le fragment ManageArticleFragment
                // lui même étant dans l'activité ArticleActivity.
                // Incrémenter le nombre de visite
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.incrementArticleViews(activeUserId, articleItem.getId());
                Intent intent = new Intent(view.getContext(), ArticleActivity.class);
                intent.putExtra(ARTICLE_ACTION, ARTICLE_ACTION_VALUE_SHOW);
                intent.putExtra(ARTICLE_ID, articleItem.getId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
    public static class ArticleViewHolder extends RecyclerView.ViewHolder{
        TextView textViewArticleTitle;
        TextView textViewPreview;
        TextView textViewVisitNbr;
        ImageView ImageViewArticleImage;


        public ArticleViewHolder(@NonNull View itemView){
            super(itemView);
            textViewArticleTitle = itemView.findViewById(R.id.articleCardTitleId);
            textViewPreview = itemView.findViewById(R.id.articleCardPreviewId);
            textViewVisitNbr = itemView.findViewById(R.id.articleViewNumberId);
            ImageViewArticleImage = itemView.findViewById(R.id.articleImageID);
        }
    }
}
