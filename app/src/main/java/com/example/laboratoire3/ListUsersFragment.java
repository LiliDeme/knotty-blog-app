package com.example.laboratoire3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoire3.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ListUsersFragment extends Fragment {
    private RelativeLayout quitLink;
    private Button createUser;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    List<User> userList;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);
        recyclerView = view.findViewById(R.id.recycleViewId);
        quitLink = view.findViewById(R.id.closeUserList);
        createUser = view.findViewById(R.id.addUserButtonAdminID);
        userList = new ArrayList<>();
        db = new DatabaseHelper(getActivity());

        createUser.setVisibility(View.VISIBLE);
        quitLink.setVisibility(View.VISIBLE);
        quitLink.setOnClickListener(v -> {getActivity().finish();});
        createUser.setOnClickListener(v -> {createUserFragment();});

        userAdapter = new UserAdapter(userList, getChildFragmentManager(), getActivity());

        loadUsersFromDatabase();
        //LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //pas de barre de division mais sinon:
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        // attacher l adapter a notre recyclerview
        recyclerView.setAdapter(userAdapter);
        return view;
    }

    public void loadUsersFromDatabase(){
        userList.clear();
        Cursor cursor = db.getAllUsers();

        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);
                String role = cursor.getString(4);

                if (role.equals("user")) {
                    User user = new User();
                    user.setRole(role);
                    user.setId(id);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    userList.add(user);
                }
            }
            cursor.close();
            //Notifier l'adapteur que les données ont changées
            userAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsersFromDatabase();
    }

    private void createUserFragment() {
        RegisterAdminFragment registerAdminFragment = new RegisterAdminFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_user_admin, registerAdminFragment);
        transaction.commit();
    }
}
