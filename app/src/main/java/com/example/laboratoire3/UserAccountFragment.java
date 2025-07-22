package com.example.laboratoire3;

import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_ID_MANAGE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE_KEY_ADMIN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.laboratoire3.db.DatabaseHelper;

public class UserAccountFragment extends Fragment {
private TextView usernameTextView;
private TextView emailTextView;
private TextView username;
private TextView emailLabel;
private ImageView userIcon;
private int userIdToShow;
private User userToShow;
private RelativeLayout backToListTextView;
private SharedPreferences userSharedPref;
private Button updateUserBtn;
private Button deleteUserBtn;
private DatabaseHelper db;

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_user_account, container, false);
    backToListTextView = view.findViewById(R.id.backToUserListId);
    usernameTextView = view.findViewById(R.id.accountUserName);
    emailTextView = view.findViewById(R.id.accountEmail);
    updateUserBtn = view.findViewById(R.id.accountUpdateUserBtn);
    deleteUserBtn = view.findViewById(R.id.accountDeleteUserBtn);
    username = view.findViewById(R.id.accountUserNameText);
    emailLabel = view.findViewById(R.id.accountEmailText);
    userIcon = view.findViewById(R.id.accountPicture);

    db = new DatabaseHelper(getActivity());

    Bundle bundle = getArguments();
    if (bundle != null) {
        if (BUNDLE_USER_SOURCE_KEY_ADMIN.equals(bundle.getString(BUNDLE_USER_SOURCE))) {
            deleteUserBtn.setVisibility(View.VISIBLE);
            backToListTextView.setVisibility(View.VISIBLE);
            backToListTextView.setOnClickListener(v -> {
                backToUserList();
            });
            updateUserBtn.setOnClickListener(v -> {
                switchFromAdmin();
            });
            userIdToShow = bundle.getInt(BUNDLE_USER_ID_MANAGE);
        }
    } else {
        userIcon.setImageResource(R.drawable.ic_account_white);
        username.setTextColor(getResources().getColor(R.color.white));
        emailLabel.setTextColor(getResources().getColor(R.color.white));
        usernameTextView.setTextColor(getResources().getColor(R.color.white));
        emailTextView.setTextColor(getResources().getColor(R.color.white));
        updateUserBtn.setBackgroundColor(getResources().getColor(R.color.red_brown));
        updateUserBtn.setTextColor(getResources().getColor(R.color.white));

        userSharedPref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userIdToShow = userSharedPref.getInt(SHARED_PREF_KEY_USER_ID, -1);
        updateUserBtn.setOnClickListener(v -> {
            switchFromUser();
        });
    }

        userToShow = db.getUserById(userIdToShow);
        if (userToShow != null) {
            usernameTextView.setText(userToShow.getUsername());
            emailTextView.setText(userToShow.getEmail());
        } else {
            Toast.makeText(getActivity(), R.string.toast_user_not_found_text, Toast.LENGTH_SHORT).show();
        }

        deleteUserBtn.setOnClickListener(v -> {
            showDeleteConfirmDialog(v, userIdToShow);
        });

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        User user = db.getUserById(userIdToShow);
        //getActivity().getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.white));
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
    }

    private void switchFromAdmin () {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_USER_ID_MANAGE, userIdToShow);
        bundle.putString(BUNDLE_USER_SOURCE, BUNDLE_USER_SOURCE_KEY_ADMIN);
        UserUpdateFragment userUpdateFragment = new UserUpdateFragment();
        userUpdateFragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_user_admin, userUpdateFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void switchFromUser () {
        Intent intent = new Intent(requireContext(), UserAccountActivity.class);
        startActivity(intent);
    }

    private void backToUserList () {
        ListUsersFragment listUsersFragment = new ListUsersFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_user_admin, listUsersFragment);
        transaction.commit();
    }

    private void showDeleteConfirmDialog (View view,int userId){
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
                            if (success) {
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
                        backToUserList();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_negative_button_text, null);
        AlertDialog dialog = deleteDialogBuilder.create();
        dialog.show();
    }
}
