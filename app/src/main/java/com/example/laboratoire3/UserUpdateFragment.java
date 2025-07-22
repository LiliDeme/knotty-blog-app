package com.example.laboratoire3;

import static com.example.laboratoire3.LoginFragment.SHARED_PREF_KEY_USER_ID;
import static com.example.laboratoire3.LoginFragment.SHARED_PREF_NAME;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_ID_MANAGE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE;
import static com.example.laboratoire3.UserAdminActivity.BUNDLE_USER_SOURCE_KEY_ADMIN;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.laboratoire3.db.DatabaseHelper;

public class UserUpdateFragment extends Fragment {


    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passConfirmEditText;
    private Button updateButton;
    private RelativeLayout backToListOrAccountTextView;
    private Boolean isSamePassword, userNameExist, emailExist;
    private ImageView checkImageView, checkConfirmImageView;
    private DatabaseHelper db;
    private int userIdToUpdate;
    private int activeUserId;
    private User userToUpdate;
    private SharedPreferences userSharedPref;
    private String pastUsername;
    private String pastPassword;
    private String pastEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_account, container, false);

        backToListOrAccountTextView = view.findViewById(R.id.backToUserAccountId);
        usernameEditText = view.findViewById(R.id.usernameUpdateTextID);
        emailEditText = view.findViewById(R.id.emailUpdateTextId);
        passwordEditText = view.findViewById(R.id.passwordUpdateTextID);
        passConfirmEditText = view.findViewById(R.id.passwordConfirmUpdateTextID);
        updateButton = view.findViewById(R.id.updateButtonID);
        checkImageView = view.findViewById(R.id.UpdateCheckImageView);
        checkConfirmImageView = view.findViewById(R.id.updateConfirmCheckImageView);

        db = new DatabaseHelper(getActivity());

        Bundle bundleData = getArguments();
        if (bundleData != null) {
            if (BUNDLE_USER_SOURCE_KEY_ADMIN.equals(bundleData.getString(BUNDLE_USER_SOURCE))) {
                userIdToUpdate = bundleData.getInt(BUNDLE_USER_ID_MANAGE);
                //todo valide pour le retour au fragment précédent mais qu'en est-il de l'activité?
                backToListOrAccountTextView.setOnClickListener(v -> {requireActivity().getSupportFragmentManager().popBackStack();});
            } else {

                userSharedPref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                userIdToUpdate = userSharedPref.getInt(SHARED_PREF_KEY_USER_ID, -1);
                backToListOrAccountTextView.setOnClickListener(v -> {requireActivity().finish();});
            }
        }

        userToUpdate = db.getUserById(userIdToUpdate);
        //retour au dernier fragment mis dans le stack sans fermer l'activité

        //todo filler les edit texte, changer le back pour le user, verifier si le mail exist (if mail is same as before, if mail exist)
        pastUsername = userToUpdate.getUsername();
        pastEmail = userToUpdate.getEmail();
        pastPassword = userToUpdate.getPassword();

        usernameEditText.setText(pastUsername);
        emailEditText.setText(pastEmail);
        passwordEditText.setText(pastPassword);
        passConfirmEditText.setText(pastPassword);

        isSamePassword = false;
        userNameExist = true;
        emailExist = true;

        passConfirmEditText.addTextChangedListener(textChangeListener());
        passwordEditText.addTextChangedListener(textChangeListener());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emailIsBanned = true;
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean usernameUnchanged = pastUsername.equals(username);
                boolean emailUnchanged = pastEmail.equals(email);
                boolean passwordUnchanged = pastPassword.equals(password);

                userNameExist = !usernameUnchanged && db.checkUsername(username);
                emailExist = !emailUnchanged && db.checkEmail(email);

                emailIsBanned = db.checkBannedEmail(email);

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(), R.string.toast_register_empty_field_text, Toast.LENGTH_LONG).show();
                } else if (emailIsBanned) {
                    Toast.makeText(getActivity(), R.string.toast_register_banned_mail_text, Toast.LENGTH_LONG).show();
                } else if (!isSamePassword || userNameExist || emailExist) {
                    if(!isSamePassword){
                        Toast.makeText(getActivity(), R.string.toast_register_different_password_text, Toast.LENGTH_LONG).show();
                    }
                    if(userNameExist){
                        Toast.makeText(getActivity(), R.string.toast_register_username_exist_field_text, Toast.LENGTH_LONG).show();
                    }
                    if (emailExist){
                        Toast.makeText(getActivity(), R.string.toast_register_email_exist_text, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //TODO UPDATE USER
                    User user = new User();
                    user.setId(userIdToUpdate);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    boolean success = db.updateUser(user);
                    if(success){
                        Toast.makeText(getActivity(), R.string.toast_update_success_text, Toast.LENGTH_LONG).show();
                        if (BUNDLE_USER_SOURCE_KEY_ADMIN.equals(bundleData.getString(BUNDLE_USER_SOURCE))) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            requireActivity().finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_register_failed_text, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private TextWatcher textChangeListener(){
        return new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String password = passwordEditText.getText().toString().trim();
                String passwordConfirm = passConfirmEditText.getText().toString().trim();
                if(password.equals(passwordConfirm)){
                    passwordEditText.setTextColor(getResources().getColor(R.color.green));
                    passConfirmEditText.setTextColor(getResources().getColor(R.color.green));
                    checkImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    checkConfirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    isSamePassword = true;
                } else {
                    passwordEditText.setTextColor(getResources().getColor(R.color.black));
                    passConfirmEditText.setTextColor(getResources().getColor(R.color.black));
                    checkImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_transparent));
                    checkConfirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_transparent));
                    isSamePassword = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString().trim();
                String passwordConfirm = passConfirmEditText.getText().toString().trim();
                if(password.equals(passwordConfirm)){
                    passwordEditText.setTextColor(getResources().getColor(R.color.green));
                    passConfirmEditText.setTextColor(getResources().getColor(R.color.green));
                    checkImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    checkConfirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    isSamePassword = true;
                } else {
                    passwordEditText.setTextColor(getResources().getColor(R.color.black));
                    passConfirmEditText.setTextColor(getResources().getColor(R.color.black));
                    checkImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_transparent));
                    checkConfirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_transparent));
                    isSamePassword = false;
                }
            }
        };

    }
}
