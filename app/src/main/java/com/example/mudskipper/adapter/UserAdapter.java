package com.example.mudskipper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.model.Chat;
import com.example.mudskipper.activity.MessageActivity;
import com.example.mudskipper.R;
import com.example.mudskipper.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mUsers;
    String theLastMessage;
    public UserAdapter(Context context, List<User> mUsers){
        this.context = context;
        this.mUsers = mUsers;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.profilePic.setImageResource(R.mipmap.ic_launcher);
        getLastMsg(user.getUid(), holder.lastMsg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getUid());
                context.startActivity(intent);
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public CircleImageView profilePic;
        private TextView lastMsg;

        public ViewHolder( View itemView) {
            super(itemView);
            username  =itemView.findViewById(R.id.message_username);
            profilePic = itemView.findViewById(R.id.user_item_profile_pic);
            lastMsg = itemView.findViewById(R.id.last_msg);
        }
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void getLastMsg(String userId, TextView last_msg){
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference lastMsgRef = FirebaseDatabase.getInstance().getReference("chats");
        lastMsgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   Chat chat = dataSnapshot.getValue(Chat.class);
                   if (chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userId)
                   ||chat.getReceiver().equals(userId)&&chat.getSender().equals(firebaseUser.getUid())){
                       theLastMessage = chat.getMessage();
                   }
                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
