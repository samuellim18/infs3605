package com.example.mudskipper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.adapter.UserAdapter;
import com.example.mudskipper.model.ChatList;
import com.example.mudskipper.R;
import com.example.mudskipper.model.User;
import com.example.mudskipper.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    //private List<String> userList;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference chatListRef;
    private DatabaseReference chatRef;
    private List<ChatList> usersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_message, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_msg_hist);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        chatListRef = FirebaseDatabase.getInstance().getReference("chatlist").child(firebaseUser.getUid());
        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    System.out.println(chatList.getReceiver_id() + "chatlist print");
                    usersList.add(chatList);

                }
                //readChats();
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updateToken(String token){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        tokenRef.child(firebaseUser.getUid()).setValue(token1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

//    private void readChats(){
//        mUsers = new ArrayList<>();
//        databaseReference = FirebaseDatabase.getInstance().getReference("users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    System.out.println(dataSnapshot);
//                    User user = dataSnapshot.getValue(User.class);
//                    for(String id :userList){
//                        if (user.getUid().equals(id)){
//                            if (mUsers.size()!=0){
//                                for (User user1: mUsers){
//                                    if (!user.getUid().equals(user1.getUid())){
//                                        mUsers.add(user);
//                                    }
//                                }
//                            } else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(),mUsers);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void chatList(){
        mUsers = new ArrayList<>();
        userRef  = FirebaseDatabase.getInstance().getReference("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatList chatlist: usersList){
                        System.out.println(chatlist.getSender_id());
                        if(user.getUid().equals(chatlist.getSender_id())||user.getUid()
                                .equals(chatlist.getReceiver_id())){
                            if (!FirebaseAuth.getInstance().getUid().equals(user.getUid())){
                            mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}