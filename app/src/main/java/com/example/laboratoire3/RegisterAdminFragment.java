package com.example.laboratoire3;



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
public class RegisterAdminFragment extends Fragment{
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passConfirmEditText;
    private Button registerButton;
    private RelativeLayout backToListTextView;
    private Boolean isSamePassword, userNameExist, emailExist;
    private ImageView checkImageView, checkConfirmImageView;
    private DatabaseHelper db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_admin, container, false);
        backToListTextView = view.findViewById(R.id.backToUserListAddUserId);
        usernameEditText = view.findViewById(R.id.usernameRegisterAdminTextID);
        emailEditText = view.findViewById(R.id.emailRegisterAdminTextId);
        passwordEditText = view.findViewById(R.id.passwordRegisterAdminTextID);
        passConfirmEditText = view.findViewById(R.id.passwordConfirmAdminTextID);
        registerButton = view.findViewById(R.id.registerButtonAdminID);
        checkImageView = view.findViewById(R.id.registerCheckImageAdminView);
        checkConfirmImageView = view.findViewById(R.id.registerConfirmCheckAdminImageView);

        db = new DatabaseHelper(getActivity());

        backToListTextView.setOnClickListener(v -> {backToUsersList();});


        isSamePassword = false;
        userNameExist = true;
        emailExist = true;

        passConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emailIsBanned = true;
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                userNameExist = db.checkUsername(username);
                emailExist = db.checkEmail(email);
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
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    boolean success = db.insertUser(user);
                    if(success){
                        Toast.makeText(getActivity(), R.string.toast_register_success_text, Toast.LENGTH_LONG).show();
                        usernameEditText.setText("");
                        emailEditText.setText("");
                        passwordEditText.setText("");
                        passConfirmEditText.setText("");
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_register_failed_text, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private void backToUsersList(){
        ListUsersFragment listUsersFragment = new ListUsersFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_user_admin, listUsersFragment);
        transaction.commit();
    }
}
