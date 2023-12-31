package com.example.chat0.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat0.MainActivity;
import com.example.chat0.MessageActivity;
import com.example.chat0.Model.Chat;
import com.example.chat0.Model.User;
import com.example.chat0.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List <User> mUser;
    private boolean ischat;
    String theLastMessage;

    String receiver;
    String sender;

    public UserAdapter (Context mContext, List<User> mUser, boolean ischat){
        this.mUser = mUser;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUser.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if(ischat) {
            lastMessage(user.getId(), holder.last_mgs, holder.user);
        } else {
            holder.last_mgs.setVisibility(View.GONE);
            holder.user.setVisibility(View.GONE);
        }

        if(ischat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public ImageView img_on;
        public ImageView img_off;
        public TextView last_mgs;
        public TextView user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_mgs = itemView.findViewById(R.id.last_mgs);
            user = itemView.findViewById(R.id.user);
        }
    }

    private String encrypt(String message, String secretKey) {
        try {
            SecretKeySpec secretKeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String decrypt(String encryptedMessage, String secretKey) {
        try {
            SecretKeySpec secretKeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKeySpec generateKey(String secretKey) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES");
    }

    private void lastMessage (String userid, TextView last_msg, TextView user) {

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        String secretKey = "8-+@83&Ox"; // Use the same secret key used for encryption
                        String decryptedMessage = decrypt(chat.getMessage(), secretKey);
                        theLastMessage = decryptedMessage;
                        if(theLastMessage != null) {
                            last_msg.setText(theLastMessage);
                        } else{
                            last_msg.setText("Нет сообщений");
                        }
                        receiver = chat.getReceiver();
                        sender = chat.getSender();

                        /*DatabaseReference userSen = FirebaseDatabase.getInstance().getReference("User").child(chat.getReceiver());
                        userSen.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                User senderUser = userSnapshot.getValue(User.class);
                                if (senderUser != null) {
                                    user.setText(senderUser.getUsername()); // Set the username of the sender
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Failed to read user data", error.toException());
                            }
                        });*/

                        DatabaseReference userRec = FirebaseDatabase.getInstance().getReference("User").child(chat.getSender());
                        userRec.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                User senderUser = userSnapshot.getValue(User.class);
                                if (senderUser != null) {
                                    user.setText(senderUser.getUsername() + ":"); // Set the username of the sender
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Failed to read user data", error.toException());
                            }
                        });
                    }
                }
                /*switch (theLastMessage) {
                    case "default" :
                        last_msg.setText("Нет сообщений");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        *//*if(theLastMessage == receiver) {
                            user.setText(receiver);
                        } else  {
                            user.setText(sender);
                        }*//*
                        break;
                }*/
                //theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
