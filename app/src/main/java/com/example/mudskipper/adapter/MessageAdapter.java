package com.example.mudskipper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.model.Chat;
import com.example.mudskipper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> mChat;
    private String imageURL;

    FirebaseUser firebaseUser;
    public MessageAdapter(Context context, List <Chat> mChat, String imageURL){
        this.context = context;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        System.out.println(chat.getSender());
        System.out.println(chat.getMessage());
        holder.show_message.setText(chat.getMessage());
        holder.profilePic.setImageResource(R.mipmap.ic_launcher);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public CircleImageView profilePic;
        public ViewHolder( View itemView) {
            super(itemView);
            show_message  =itemView.findViewById(R.id.show_msg);
            profilePic = itemView.findViewById(R.id.profile_image);
        }
    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position){
        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else return MSG_TYPE_LEFT;
    }
}
