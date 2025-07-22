package com.example.laboratoire3;

import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ACTION_VALUE_SHOW;
import static com.example.laboratoire3.ArticleActivity.ARTICLE_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ROLE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_ID_MANAGE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_ITEM;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE_KEY_ADMIN;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE_KEY_USER;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoire3.db.DatabaseHelper;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private SharedPreferences userSharedPref;
    private String activeUserRole;
    private FragmentManager fragmentManager;
    private FragmentActivity fragmentActivity;

    public UserAdapter(List<User> userList, FragmentManager fragmentManager, FragmentActivity fragmentActivity) {
        this.userList = userList;
        this.fragmentManager = fragmentManager;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        userSharedPref = view.getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        activeUserRole = userSharedPref.getString(SHARED_PREF_KEY_USER_ROLE, "");

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUsername.setText(user.getUsername());
        holder.textViewEmail.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_USER_ID_MANAGE, user.getId());
            bundle.putString(BUNDLE_USER_SOURCE, BUNDLE_USER_SOURCE_KEY_ADMIN);
            UserAccountFragment userAccountFragment = new UserAccountFragment();
            userAccountFragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_user_admin, userAccountFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        holder.iconUpdate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_USER_ID_MANAGE, user.getId());
            bundle.putString(BUNDLE_USER_SOURCE, BUNDLE_USER_SOURCE_KEY_ADMIN);
            UserUpdateFragment userUpdateFragment = new UserUpdateFragment();
            userUpdateFragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_user_admin, userUpdateFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        holder.iconDelete.setOnClickListener(v -> {
            showDeleteConfirmDialog(v, user.getId());
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUsername;
        TextView textViewEmail;
        ImageView iconUpdate;
        ImageView iconDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.userItemUsername);
            textViewEmail = itemView.findViewById(R.id.userItemEmailId);
            iconUpdate = itemView.findViewById(R.id.iconUpdateUserAdminID);
            iconDelete = itemView.findViewById(R.id.iconDeleteUserAdminId);
        }
    }

    private void showDeleteConfirmDialog(View view, int userId) {
        // Options possible (delete ou ban)
        // autre possibilité intéressante: faire un setMultiChoiceItems avec option delete, bannir seulement ou delete + banir
        String[] deleteOptions = {
                view.getContext().getString(R.string.alert_dialog_option_delete_user_text),
                view.getContext().getString(R.string.alert_dialog_option_delete_ban_user_text)
        };

        // choix sélectionnée
        final int[] selectedOption = {0}; // Un seul choix sélectionné

        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(view.getContext());
        deleteDialogBuilder.setTitle(R.string.alert_dialog_delete_user_title_text)
                .setIcon(R.drawable.ic_warning)
                .setSingleChoiceItems(deleteOptions, selectedOption[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // which -> pointe à l'index du choix sélectionné correspondant à l'index des items dans le tableau deleteOptions
                        selectedOption[0] = which;
                    }
                })
                .setPositiveButton(R.string.alert_dialog_positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper db = new DatabaseHelper(view.getContext());

                        boolean success;
                        if (selectedOption[0] == 0) {
                            success = db.deleteUser(userId);
                            if (success){
                                Toast.makeText(view.getContext(), R.string.toast_delete_user_success_text, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(view.getContext(), R.string.toast_delete_user_failed_text, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            success = db.banUser(userId);
                            if (success) {
                                Toast.makeText(view.getContext(), R.string.toast_ban_user_success_text, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(view.getContext(), R.string.toast_delete_user_failed_text, Toast.LENGTH_SHORT).show();
                            }
                        }
                        fragmentActivity.recreate();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_negative_button_text, null);
        AlertDialog dialog = deleteDialogBuilder.create();
        dialog.show();
    }
}
