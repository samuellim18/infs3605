package com.example.mudskipper.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.R;
import com.example.mudskipper.model.User;
import com.example.mudskipper.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatUserFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    EditText searchUsers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_user, container, false);
        recyclerView = view.findViewById(R.id.message_RV);
        searchUsers = view.findViewById(R.id.search_users_msg);
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recyclerView.setHasFixedSize(true);
        readusers();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();

        return view;
    }

    private void searchUsers(String string) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                .startAt(string)
                .endAt(string+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    System.out.println(user);
                    if (!searchUsers.getText().toString().equals("")) {
                        if (!user.getUid().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readusers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (searchUsers.getText().toString().equals("")) {
                    mUsers.clear();
                    System.out.println(snapshot + "snapshot");
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        System.out.println(user.getUid() + "userid");
                        System.out.println(firebaseUser.getUid());
                        if (!user.getUid().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error + "error");
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}