package com.example.laboratoire3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.example.laboratoire3.ArticleItem;
import com.example.laboratoire3.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    // nom et version de la base de donnees
    private static final String DATABASE_NAME = "knotty_blog.db";
    private static final int DATABASE_VERSION = 1;

    // tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ARTICLES = "articles";
    private static final String TABLE_ARTICLE_VIEWS = "article_views";
    private static final String TABLE_COMMENTS = "comments";
    private static final String TABLE_BANNED_USERS = "banned_users";
    //TODO Create Favorite table (many to many with user and articles)

    // colonne user
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_ROLE = "role";

    // colonne article
    private static final String COLUMN_ARTICLE_ID = "id";
    private static final String COLUMN_ARTICLE_USER_ID = "user_id";
    private static final String COLUMN_ARTICLE_TITLE = "title";
    private static final String COLUMN_ARTICLE_BODY = "content";
    private static final String COLUMN_ARTICLE_IMAGE_PATH = "image_path";
    private static final String COLUMN_ARTICLE_PREVIEW = "preview";
    private static final String COLUMN_ARTICLE_PUBLICATION_DATE = "publication_date";
    private static final String COLUMN_ARTICLE_VIEWS = "views";

    // colonne view
    private static final String COLUMN_VIEW_ID = "id";
    private static final String COLUMN_VIEW_USER_ID = "user_id";
    private static final String COLUMN_VIEW_ARTICLE_ID = "article_id";

    // colonne comment
    private static final String COLUMN_COMMENT_ID = "id";
    private static final String COLUMN_COMMENT_USER_ID = "user_id";
    private static final String COLUMN_COMMENT_ARTICLE_ID = "article_id";
    private static final String COLUMN_COMMENT_CONTENT = "content";
    private static final String COLUMN_COMMENT_PUBLICATION_DATE = "publication_date";

    // colonne banned users
    private static final String COLUMN_BANNED_ID = "id";
    private static final String COLUMN_BANNED_EMAIL = "email";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE,"
                + COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_USER_ROLE + " TEXT DEFAULT 'user'"
                + ")";

        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + TABLE_ARTICLES + "("
                + COLUMN_ARTICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ARTICLE_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_ARTICLE_TITLE + " TEXT NOT NULL,"
                + COLUMN_ARTICLE_BODY + " TEXT NOT NULL,"
                + COLUMN_ARTICLE_PREVIEW + " TEXT,"
                + COLUMN_ARTICLE_PUBLICATION_DATE + " TEXT DEFAULT (datetime('now')),"
                + COLUMN_ARTICLE_IMAGE_PATH + " TEXT,"
                + COLUMN_ARTICLE_VIEWS + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_ARTICLE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";

        String CREATE_ARTICLE_VIEWS_TABLE = "CREATE TABLE " + TABLE_ARTICLE_VIEWS + "("
                + COLUMN_VIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VIEW_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_VIEW_ARTICLE_ID + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_VIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_VIEW_ARTICLE_ID + ") REFERENCES " + TABLE_ARTICLES + "(" + COLUMN_ARTICLE_ID + ")"
                + ")";


        String CREATE_COMMENT_TABLE = "CREATE TABLE " + TABLE_COMMENTS + "("
                + COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COMMENT_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_COMMENT_ARTICLE_ID + " INTEGER NOT NULL,"
                + COLUMN_COMMENT_CONTENT + " TEXT NOT NULL,"
                + COLUMN_COMMENT_PUBLICATION_DATE + " TEXT DEFAULT (datetime('now')),"
                + "FOREIGN KEY(" + COLUMN_COMMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_COMMENT_ARTICLE_ID + ") REFERENCES " + TABLE_ARTICLES + "(" + COLUMN_ARTICLE_ID + ")"
                + ")";

        String CREATE_BANNED_USERS_TABLE = "CREATE TABLE " + TABLE_BANNED_USERS + "("
                + COLUMN_BANNED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BANNED_EMAIL + " TEXT NOT NULL UNIQUE"
                + ")";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(CREATE_ARTICLE_VIEWS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_BANNED_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    //User methods

    public boolean insertAdmin(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_USERNAME, user.getUsername());
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_USER_ROLE, user.getRole());
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;  // retourne true si l'enregistrement a ete fait avec sucess
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_USERNAME, user.getUsername());
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;  // retourne true si l'enregistrement a ete fait avec sucess
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public int getUserId(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USERNAME + "=? AND " + COLUMN_USER_PASSWORD + "=?", new String[]{user.getUsername(), user.getPassword()});
        if (cursor.getCount() == 0) {
            return -1; //si utilisateur non-trouvé
        } else {
            cursor.moveToFirst();
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
    }

    public String getUserName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.getCount() == 0) {
            return null; //si utilisateur non-trouvé
        } else {
            cursor.moveToFirst();
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            String userName = cursor.getString(1);
            String email = cursor.getString(2);
            String password = cursor.getString(3);
            String role = cursor.getString(4);

            user.setId(userId);
            user.setUsername(userName);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
        }
        cursor.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_USERNAME, user.getUsername());
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
        long result = db.update(TABLE_USERS, contentValues, "id=?", new String[]{String.valueOf(user.getId())});
        return result > 0;  // retourne true si la mise à jour a ete fait
    }

    public boolean checkUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USERNAME + "=? AND " + COLUMN_USER_PASSWORD + "=?", new String[]{user.getUsername(), user.getPassword()});
        return cursor.getCount() > 0;
    }

    public boolean isAdmin(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_USER_ROLE + "='admin'", new String[]{String.valueOf(userId)});
        return cursor.getCount() > 0;
    }

    public boolean checkAdminExistance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ROLE + "= 'admin'", null);
        return cursor.getCount() > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USERNAME + "=?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public void deleteUserViews(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTICLE_VIEWS, COLUMN_VIEW_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void deleteUserArticles(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTICLES, COLUMN_ARTICLE_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public Boolean deleteUser(int userId) {
        deleteUserViews(userId);
        deleteUserArticles(userId);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    public boolean banUser(int userId) {
        // SUPPRIMER UN USER AVEC TOUT SON CONTENUE ET LE BANIR
        boolean resultInsertBan = false, test;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_BANNED_EMAIL, email);
            long result = db.insert(TABLE_BANNED_USERS, null, contentValues);
            if (result != -1) {
                resultInsertBan = true;
                test = deleteUser(userId);
            }
        }
        db.close();
        return resultInsertBan;
    }

    // ARTICLES METHOD

    public long insertArticle(ArticleItem article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ARTICLE_USER_ID, article.getUserId());
        contentValues.put(COLUMN_ARTICLE_TITLE, article.getTitle());
        contentValues.put(COLUMN_ARTICLE_PREVIEW, article.getPreview());
        contentValues.put(COLUMN_ARTICLE_BODY, article.getContent());
        contentValues.put(COLUMN_ARTICLE_IMAGE_PATH, article.getImagePath());
        long id = db.insert(TABLE_ARTICLES, null, contentValues);
        return id;  // retourne l'id de l'article
    }

    public boolean updateArticle(ArticleItem article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ARTICLE_TITLE, article.getTitle());
        contentValues.put(COLUMN_ARTICLE_PREVIEW, article.getPreview());
        contentValues.put(COLUMN_ARTICLE_BODY, article.getContent());
        contentValues.put(COLUMN_ARTICLE_IMAGE_PATH, article.getImagePath());
        long result = db.update(TABLE_ARTICLES, contentValues, "id=?", new String[]{String.valueOf(article.getId())});
        return result > 0;  // retourne true si la mise à jour a ete fait
    }

    public Cursor getAllArticles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ARTICLES, null);
    }

    public ArticleItem getArticleById(int articleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArticleItem article = new ArticleItem();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTICLES + " WHERE " + COLUMN_ARTICLE_ID + "=?", new String[]{String.valueOf(articleId)});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(1);
            String title = cursor.getString(2);
            String content = cursor.getString(3);
            String preview = cursor.getString(4);
            String publicationDate = cursor.getString(5);
            String imagePath = cursor.getString(6);
            int views = cursor.getInt(7);

            article.setId(articleId);
            article.setUserId(userId);
            article.setTitle(title);
            article.setContent(content);
            article.setPreview(preview);
            article.setPublicationDate(publicationDate);
            article.setImagePath(imagePath);
            article.setViewNumber(views);
        }

        cursor.close();
        return article;
    }

    public boolean deleteArticle(int articleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(TABLE_ARTICLES, "id=?", new String[]{String.valueOf(articleId)});
        db.close();
        return result > 0;
    }

    public boolean incrementArticle_ArticleViews(int articleId) { //incrémente vues de la table Article
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ARTICLE_VIEWS + " FROM " + TABLE_ARTICLES + " WHERE " + COLUMN_ARTICLE_ID + "=?", new String[]{String.valueOf(articleId)});
        if (cursor.getCount() == 0) {
            return false;
        } else {
            cursor.moveToFirst();
            int viewNbr = cursor.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ARTICLE_VIEWS, viewNbr + 1);
            long result = db.update(TABLE_ARTICLES, contentValues, COLUMN_ARTICLE_ID + "=?", new String[]{String.valueOf(articleId)});
            cursor.close();
            return result > 0;
        }
    }

    // ARTICLES VIEWS METHOD

    public boolean incrementArticleViews(int userId, int articleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTICLE_VIEWS + " WHERE " + COLUMN_VIEW_USER_ID + "=? AND " + COLUMN_VIEW_ARTICLE_ID + "=?", new String[]{String.valueOf(userId), String.valueOf(articleId)});
        if (cursor.getCount() == 0) {
            // Aucun enregistrement de ce user pour cet article -> on enregistre sa nouvelle vue
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_VIEW_USER_ID, userId);
            contentValues.put(COLUMN_VIEW_ARTICLE_ID, articleId);
            long resultInsert = db.insert(TABLE_ARTICLE_VIEWS, null, contentValues);
            if (resultInsert > 0) {
                //incremente dans le nombre de vue dans table 'articles'
                // Note: je réutilise la première méthode que j'avais fait au départ,
                // elle ne faisait qu'incrémenter le nombre de vue chaque fois qu'on cliquait
                // sur un article même si le user l'avait déjà vue au déépart.
                boolean resultUpdate = incrementArticle_ArticleViews(articleId);
                cursor.close();
                return resultUpdate;
            }
        }
        cursor.close();
        return false;
    }

    // BANNED METHOD
    public boolean checkBannedEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BANNED_USERS + " WHERE " + COLUMN_BANNED_EMAIL + "=?", new String[]{email});
        return cursor.getCount() > 0;
    }




}


