package com.example.chat0.Adapter;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat0.MessageActivity;
import com.example.chat0.Model.Chat;
import com.example.chat0.Model.User;
import com.example.chat0.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context mContext;
    private final List<Chat> mChat;
    private String imageurl;
    ClipboardManager clipboardManager;
    DatabaseReference reference;
    FirebaseUser fuser;

    public MessageAdapter (Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_rigth, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());


        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Просмотренно");
                holder.txt_seen.setTextColor(Color.parseColor("#5eafbd"));
            } else {
                holder.txt_seen.setText("Доставлено");
            }
        } /*else {
            holder.txt_seen.setText(View.GONE);
        }*/

        clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference = FirebaseDatabase.getInstance().getReference().child("Chats");
                /*String key = reference.push().getKey();
                if(key != null) {
                    Toast.makeText(view.getContext(), "Ура ключ скопирован" + key, Toast.LENGTH_SHORT).show();
                }
*/
                /*reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                            Object obj = Snapshot.getKey();
                            Toast.makeText(view.getContext(), "Ура ебать" + obj, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("Read failed", firebaseError.getMessage());
                        Toast.makeText(view.getContext(), "Пизда ебаная", Toast.LENGTH_SHORT).show();
                    }
                });*/

                /*reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String key = child.getKey();
                            Log.e("Key", key);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                /*Chat chats = mChat.get(position);
                holder.show_message.setText(chat.getMessage());*/

                PopupMenu popup = new PopupMenu(mContext, holder.show_message);
                popup.inflate(R.menu.context_menu);


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item1) {
                        switch (item1.getItemId()) {
                            case R.id.copy:
                                String text = holder.show_message.getText().toString();
                                if(!text.equals("")){
                                    ClipData clipData = ClipData.newPlainText("text", text);
                                    clipboardManager.setPrimaryClip(clipData);
                                    /*Toast.makeText(view.getContext(), "Coped", Toast.LENGTH_SHORT).show();*/
                                }
                                return true;
                           /* case R.id.edit:

                                EditText messageFiled = view.findViewById(R.id.messageFiled);

                                String edit_text = holder.show_message.getText().toString();
                                if(!edit_text.equals("")){
                                    ClipData clipData = ClipData.newPlainText("text", edit_text);
                                    clipboardManager.setPrimaryClip(clipData);
                                    *//*Toast.makeText(view.getContext(), "Coped", Toast.LENGTH_SHORT).show();*//*
                                }
                                    *//*ClipData clipData = ClipData.newPlainText("text", edit_text);*//*
                                    ClipData clipData = clipboardManager.getPrimaryClip();
                                    ClipData.Item item = clipData.getItemAt(0);

                                    messageFiled.setText(item.getText().toString());



                                return true;
                            case R.id.delet:

                                *//*reference = FirebaseDatabase.getInstance().getReference("Chats");*//*
                                *//*reference.child();*//*
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("message", null);
                                            snapshot.getRef().setValue(hashMap);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(view.getContext(), "пздц", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return true;
                            case R.id.select:
                                return true;*/
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);


        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else
            return MSG_TYPE_LEFT;
    }
}