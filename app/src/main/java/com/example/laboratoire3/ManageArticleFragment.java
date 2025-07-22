package com.example.laboratoire3;

import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_SHOW;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_ADD;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_MODIFY;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ID;

import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ROLE;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.laboratoire3.db.DatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//add, modify or show article
public class ManageArticleFragment extends Fragment {
    private final String ADMIN_ROLE = "admin";
    private String imgPath;
    private ActivityResultLauncher<Intent> resultLauncher;
    private SharedPreferences userSharedPref;
    private int activeUserId;
    private String activeUserRole;
    private String articleAction = null;
    private int articleId = -1;
    //layout propre à add/modify article
    private Button imgSelectButton, articleConfirmButton;
    private EditText articlePreviewEditText, articleTitleEditText, articleBodyEditText;

    //layout show article
    private TextView publishedDate
            , viewNbr
            , articleTitleTextView
            , articleBodyTextView
            , articleAuthorTextView;
    private ImageView updateArticleImageView, deleteArticleImageView;
    private RelativeLayout closeArticle;

    //widget commun
    private ImageView articleImage;

    //database
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = new DatabaseHelper(getContext());
        userSharedPref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        activeUserId = userSharedPref.getInt(SHARED_PREF_KEY_USER_ID, -1);
        activeUserRole = userSharedPref.getString(SHARED_PREF_KEY_USER_ROLE, "user");
        //Récupérer les données du bundle (add, modify, show, article_id)
        Bundle bundleData = getArguments();
        if (bundleData != null) {
            articleAction = bundleData.getString(ARTICLE_ACTION);
            articleId = bundleData.getInt(ARTICLE_ID);
        }
        // initialisation de "onActivityResult"
        registerResult();
        View view;
        if(articleAction.equals(ARTICLE_ACTION_VALUE_SHOW) && articleId != -1){
            //show article
            view = inflater.inflate(R.layout.fragment_article, container, false);
            articleImage = view.findViewById(R.id.detailedArticleImageID);
            publishedDate = view.findViewById(R.id.detailedArticleDateID);
            viewNbr = view.findViewById(R.id.detailedArticleViewNumberId);
            articleTitleTextView = view.findViewById(R.id.detailedArticleTitleID);
            articleBodyTextView = view.findViewById(R.id.detailedArticleBodyID);
            articleAuthorTextView = view.findViewById(R.id.detailedArticleAuthorID);
            updateArticleImageView = view.findViewById(R.id.articleUpdateIconId);
            deleteArticleImageView = view.findViewById(R.id.articleDeleteIconId);
            closeArticle = view.findViewById(R.id.closeArticle);

        } else {
            //add + update article
            view = inflater.inflate(R.layout.fragment_manage_article, container, false);
            imgSelectButton = view.findViewById(R.id.selectArticleImageBtn);
            articleConfirmButton = view.findViewById(R.id.selectArticleBtn);
            articlePreviewEditText = view.findViewById(R.id.selectArticlePreviewID);
            articleTitleEditText = view.findViewById(R.id.selectArticleTitleID);
            articleBodyEditText = view.findViewById(R.id.selectArticleBodyID);
            articleImage = view.findViewById(R.id.selectArticleImageView);
        }

        switch (articleAction) {
            case ARTICLE_ACTION_VALUE_SHOW:showArticle(articleId);break;
            case ARTICLE_ACTION_VALUE_ADD:addArticle();break;
            case ARTICLE_ACTION_VALUE_MODIFY:modifyArticle(articleId);break;
        }

        return view;
    }

    private void showArticle(int articleId){
        ArticleItem article = db.getArticleById(articleId);
        closeArticle.setOnClickListener(v -> {getActivity().finish();});

        //montrer l'option delete et update si propriétaire de l'article ou si admin
        if (activeUserId == article.getUserId() || activeUserRole.equals(ADMIN_ROLE) ){
            updateArticleImageView.setVisibility(View.VISIBLE);
            deleteArticleImageView.setVisibility(View.VISIBLE);

            updateArticleImageView.setOnClickListener(v -> {switchToUpdate(articleId);});
            deleteArticleImageView.setOnClickListener(v -> {showDeleteConfirmDialog(articleId);});
        }

        String imagePath = article.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()){
            articleImage.setImageURI(Uri.parse(imagePath));
        }
        publishedDate.setText(article.getPublicationDate());
        articleAuthorTextView.setText(db.getUserName(article.getUserId()));
        viewNbr.setText(String.valueOf(article.getViewNumber()));
        articleTitleTextView.setText(article.getTitle());
        articleBodyTextView.setText(article.getContent());
    }

    private void addArticle(){
        articleConfirmButton.setText(R.string.add_article_btn_text);
        imgSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        articleConfirmButton.setOnClickListener(v -> {
            String title = articleTitleEditText.getText().toString().trim();
            String preview = articlePreviewEditText.getText().toString().trim();
            String body = articleBodyEditText.getText().toString().trim();
            if (title.isEmpty() || body.isEmpty()){
                Toast.makeText(requireContext(), R.string.toast_add_article_missing_field_text, Toast.LENGTH_LONG).show();
            } else {
                ArticleItem article = new ArticleItem();
                article.setUserId(activeUserId);
                article.setTitle(title);
                article.setContent(body);
                if (preview.isEmpty()){
                    preview = fisrtSentencePreview(article.getContent());
                    article.setPreview(preview);
                } else {
                    article.setPreview(preview);
                }
                if (imgPath != null && !imgPath.isEmpty()) {
                    article.setImagePath(imgPath);
                }
                long articleId = db.insertArticle(article);
                if (articleId != -1){
                    Toast.makeText(requireContext(), R.string.toast_add_article_success_text, Toast.LENGTH_LONG).show();
                    switchToShow((int )articleId);
                } else {
                    Toast.makeText(requireContext(), R.string.toast_add_article_failed_text, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void modifyArticle(int articleId){
        articleConfirmButton.setText(R.string.modify_article_btn_text);
        ArticleItem articleItem = db.getArticleById(articleId);

        if (articleItem != null){
            imgPath = articleItem.getImagePath();
            if (imgPath == null){
                imgPath = ""; // Pour éviter d'avoir une erreur lorsqu'on fait imgPath.isEmpty()
            }
            if (!imgPath.isEmpty()) {
                // on récupère une image exisatante
                articleImage.setImageURI(Uri.parse(imgPath));
            }

            imgSelectButton.setOnClickListener(v -> {
                pickImage();
            });

            articleTitleEditText.setText(articleItem.getTitle());
            articlePreviewEditText.setText(articleItem.getPreview());
            articleBodyEditText.setText(articleItem.getContent());

            articleConfirmButton.setOnClickListener(v -> {
                String title = articleTitleEditText.getText().toString().trim();
                String preview = articlePreviewEditText.getText().toString().trim();
                String body = articleBodyEditText.getText().toString().trim();

                if (title.isEmpty() || body.isEmpty()){
                    Toast.makeText(requireContext(), R.string.toast_add_article_missing_field_text, Toast.LENGTH_LONG).show();
                } else {
                    ArticleItem article = new ArticleItem();
                    article.setId(articleId);
                    article.setTitle(title);
                    article.setContent(body);
                    if (preview.isEmpty()){
                        preview = fisrtSentencePreview(article.getContent());
                        article.setPreview(preview);
                    } else {
                        article.setPreview(preview);
                    }
                    if (imgPath != null && !imgPath.isEmpty()) {
                        article.setImagePath(imgPath);
                    } else {
                        article.setImagePath(""); // Set to an empty string or handle it accordingly
                    }
                    boolean success = db.updateArticle(article);
                    if (success){
                        Toast.makeText(requireContext(), R.string.toast_update_article_success_text, Toast.LENGTH_LONG).show();
                        switchToShow(article.getId());

                    } else {
                        Toast.makeText(requireContext(), R.string.toast_add_article_failed_text, Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(requireContext(), R.string.toast_load_article_failed_text, Toast.LENGTH_LONG).show();
        }
    }

    private String fisrtSentencePreview(String body){
        int maxLength =190;
        int firstDotSentenceIndex = body.indexOf('.');
        if (firstDotSentenceIndex != -1 && firstDotSentenceIndex < maxLength){
            return body.substring(0, firstDotSentenceIndex + 1).trim();
        }
        if (body.length() < 200){
            return body;
        }
        return body.substring(0,maxLength) + "...";
    }

    private void switchToShow(int articleId){
        // Réinitialiser le fragment en mode "show"
        Bundle bundle = new Bundle();
        bundle.putString(ARTICLE_ACTION, ARTICLE_ACTION_VALUE_SHOW);
        bundle.putInt(ARTICLE_ID, articleId);
        ManageArticleFragment showFragment = new ManageArticleFragment();
        showFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.articleFragmentContainer, showFragment);
        transaction.commit();
    }

    private void switchToUpdate(int articleId){
        // Réinitialiser le fragment en mode "show"
        Bundle bundle = new Bundle();
        bundle.putString(ARTICLE_ACTION, ARTICLE_ACTION_VALUE_MODIFY);
        bundle.putInt(ARTICLE_ID, articleId);
        //nouvelle instance du fragment ManageArticleFragment
        ManageArticleFragment updateFragment = new ManageArticleFragment();
        updateFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.articleFragmentContainer, updateFragment);
        transaction.commit();
    }

    private void returnToMain() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void registerResult(){ //à partir d'un tutorial youtube
        imgPath = "";
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            articleImage.setImageURI(imageUri);
                            //sauvegarde interne de l'image et de son chemin
                            String path = saveToInternalStorage(imageUri);
                            if (!path.isEmpty()) { //si chemin est non null
                                imgPath = path;
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), R.string.image_chooser_no_selection_text, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);
    }

    private String saveToInternalStorage(Uri imageUri){ //NOTE: trouvée sur stackoverflow modifié un peu
        //obtenir un bitmap à partir d'un uri (from stack overflow)
        Bitmap bitmapImage = null;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), R.string.exception_image_not_found_text, Toast.LENGTH_SHORT).show();
            return "";
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(requireContext(), R.string.exception_image_error_loading_text, Toast.LENGTH_SHORT).show();
            return "";
        }

        // chemin vers /data/data/yourapp/app_data/imageDir
        File directory = requireContext().getDir("imageDir", Context.MODE_PRIVATE);
        //generer un String unique pour le nom du fichier image
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddsshhmmss", Locale.getDefault());
        String date = sdf.format(new Date());
        String fileName = String.format("%s_%s.png", "imageName_", date);
        // Create imageDir
        File mypath=new File(directory, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return mypath.toString();
    }

    private void showDeleteConfirmDialog(int articleId){
        AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(requireContext());
        logoutDialogBuilder.setTitle(R.string.alert_dialog_delete_article_title_text)
                .setMessage(R.string.alert_dialog_delete_article_message_text)
                .setPositiveButton(R.string.alert_dialog_positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = db.deleteArticle(articleId);
                        if(success){
                            Toast.makeText(requireContext(), R.string.toast_delete_article_success_text, Toast.LENGTH_SHORT).show();
                            returnToMain();
                        } else {
                            Toast.makeText(requireContext(), R.string.toast_delete_article_failed_text, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.alert_dialog_negative_button_text, null);

        AlertDialog dialog = logoutDialogBuilder.create();
        dialog.show();
    }
}
