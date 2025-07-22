package com.example.laboratoire3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.laboratoire3.db.DatabaseHelper;

public class LoginFragment extends Fragment {
    private TextView registerTextView;
    private Button connexionButton;
    private EditText usernameEditText, passwordEditText;
    private DatabaseHelper db;

    public static final String SHARED_PREF_NAME= "activeUserPref";
    public static final String SHARED_PREF_KEY_USER_ID = "userId";
    public static final String SHARED_PREF_KEY_USER_ROLE = "userRole";
//TODO EVERYTHING
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        registerTextView = view.findViewById(R.id.registerText);
        connexionButton = view.findViewById(R.id.connexionBoutonID);
        usernameEditText = view.findViewById(R.id.usernameTextID);
        passwordEditText = view.findViewById(R.id.passwordTextID);

        db = new DatabaseHelper(getActivity());

        connexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(), R.string.toast_register_empty_field_text, Toast.LENGTH_LONG).show();
                } else {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    boolean success = db.checkUser(user);
                    if (success){

                        int userId = db.getUserId(user);
                        if (userId == -1){
                            Toast.makeText(getActivity(), R.string.toast_get_user_id_failed_text, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.toast_login_success_text, Toast.LENGTH_LONG).show();

                            //On vérifie si l'utilisateur est un admin
                            String userRole = db.isAdmin(userId) ? "admin" : "user";
                            //on sauvegare l'id de l'utilisateur actif et son role
                            SharedPreferences userSharedPref = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor userSharedPrefEditor = userSharedPref.edit();
                            //Si il y avait des données pour x raisons dans le active_user_sharedpref, on les supprimes
                            userSharedPrefEditor.clear();

                            userSharedPrefEditor.putInt(SHARED_PREF_KEY_USER_ID,userId);
                            userSharedPrefEditor.putString(SHARED_PREF_KEY_USER_ROLE,userRole);
                            userSharedPrefEditor.apply();

                            Intent intent = new Intent(requireActivity(), MainActivity.class );
                            startActivity(intent);
                            getActivity().finish();
                        }

                    }
                }

            }
        });


        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedInstanceState == null) {
                    RegisterFragment registerFragment  = new RegisterFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, registerFragment);
                    transaction.commit();
                }
            }
        });

        return view;
    }
}
