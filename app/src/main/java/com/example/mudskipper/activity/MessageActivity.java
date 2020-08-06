package com.example.mudskipper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.model.Chat;
import com.example.mudskipper.R;
import com.example.mudskipper.model.User;
import com.example.mudskipper.adapter.MessageAdapter;
import com.example.mudskipper.notifications.APIService;
import com.example.mudskipper.notifications.Client;
import com.example.mudskipper.notifications.Data;
import com.example.mudskipper.notifications.MyResponse;
import com.example.mudskipper.notifications.Sender;
import com.example.mudskipper.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profilePic;
    String userID;
    ImageButton btn_send;
    EditText msg_text;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    APIService apiService;
    boolean notify  =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.message_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        apiService  = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        profilePic =  findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send_msg);
        msg_text = findViewById(R.id.et_type_msg);
        recyclerView = findViewById(R.id.msg_area_RV);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        intent = getIntent();
        userID  = intent.getStringExtra("userId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                username.setText(user.getUsername());}
                else{username.setText("null username");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = msg_text.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userID,msg);
                }
                else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                msg_text.setText("");
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                readMessages(firebaseUser.getUid(),userID,"default");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }



    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        Log.d("MessageActivity", "Current Timestamp: " + format);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", format);
        msgRef.child("chats").push().setValue(hashMap);

        //add user to chat fragment
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(firebaseUser.getUid())
                .child(userID);

        DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(userID)
                .child(firebaseUser.getUid());
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    HashMap hashMap1 = new HashMap();
                    hashMap1.put("receiver_id",userID);
                    hashMap1.put("sender_id", firebaseUser.getUid());
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("sender_id",userID);
                    hashMap2.put("receiver_id", firebaseUser.getUid());
                    chatReference.setValue(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("sender_id",userID);
                    hashMap2.put("receiver_id", firebaseUser.getUid());
                    chatReference1.setValue(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg = "message";

        DatabaseReference notref  = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        notref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notify){
                sendNotification(receiver, user.getUsername(),msg);}
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String username, String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher, username+": " + message, "New Message",userID);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code()==200){
                                        Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages(String myID, String userID, String imgURL){
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myID)&&chat.getSender().equals(userID)||chat.getReceiver().equals(userID)&&chat.getSender().equals(myID)){
                        mChat.add(chat);

                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imgURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    public void addChatList(){
        DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(userID)
                .child(firebaseUser.getUid());
        chatReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("sender_id",userID);
                    hashMap2.put("receiver_id", firebaseUser.getUid());
                    chatReference1.setValue(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}