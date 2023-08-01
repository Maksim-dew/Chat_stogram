package com.example.chat0;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat0.Adapter.MessageAdapter;
import com.example.chat0.Adapter.SpacingItemDecorator;
import com.example.chat0.Model.Chat;
import com.example.chat0.Model.Chatlist;
import com.example.chat0.Model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    EditText textField;
    TextView user_name;
    FirebaseUser fuser;
    DatabaseReference reference;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recycler_view;
    Intent intent;
    ValueEventListener seenListener;
    private FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#C1D2E2"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //вернул из-за вылета в сообщениях
            }
        });

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler_view.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        user_name = (TextView)findViewById(R.id.user_name);
        sendBtn = findViewById(R.id.btnSend);
        textField = findViewById(R.id.messageFiled);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = textField.getText().toString();
                if(!msg.equals(" ")){
                    sendMessage(fuser.getUid(), userid, msg);
                    /*if(key != null) {
                        Toast.makeText(view.getContext(), "Ура ключ скопирован" + key, Toast.LENGTH_SHORT).show();
                    }
                    String key = reference.push().getKey();
                    if(key != null) {
                        Toast.makeText(view.getContext(), "Ура ключ скопирован" + key, Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    Toast.makeText(MessageActivity.this, "Вы не ввели текст сообщения", Toast.LENGTH_LONG).show();
                }
                textField.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("User").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user_name.setText(user.getUsername());
                Log.d(TAG, "user_name" + user_name.toString());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    //Меняю это на это
                    //Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);

                }
                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        seenMessage (userid);
    }

    private void seenMessage (final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

    private void sendMessage(String sender, final String receiver, String message) { //какая то дроч с userid

        final String userid = intent.getStringExtra("userid"); //какая то пизда
        String MessageID = reference.child("Chats").push().getKey();

        reference = FirebaseDatabase.getInstance().getReference();

        String secretKey = "8-+@83&Ox";
        String encryptedMessage = encrypt(message, secretKey);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", MessageID);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", encryptedMessage);
        hashMap.put("isseen", false);

        //код Саши
        /*String MessageID = reference.child("Chats").push().getKey();
        reference.child("Chats").setValue(hashMap);*/

        //то что было раньше
        reference.child("Chats").push().setValue(hashMap);

        /*String key = reference.push().getKey();
        Log.d("Post Key" , reference.getKey());*/

        //вся эта тема добавляет пользователя во фрагмент чата
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid); //какая то дроч с userid
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMesagges(String myid, String userid, String imageurl) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        String secretKey = "8-+@83&Ox";
                        String decryptedMessage = decrypt(chat.getMessage(), secretKey);
                        chat.setMessage(decryptedMessage);
                        mChat.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                recycler_view.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //reference.removeEventListener(seenListener);
        status("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reference.removeEventListener(seenListener);
    }

}