package com.example.chat0;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView user_name;
    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;
    private RelativeLayout activity_message;
    FirebaseUser firebaseUser;

    private FirebaseListAdapter<Message> adapter;
    private FirebaseListOptions<Message> options;
    private FloatingActionButton sendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#7E9C8D"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bdlistener ();
        displayAllMessages();

        sendBtn = findViewById(R.id.btnSend);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textField = findViewById(R.id.messageFiled);
                if(textField.getText().toString() == "")
                    return;
                FirebaseDatabase.getInstance().getReference()
                        .push()
                        .setValue(new Message(FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail(), textField.getText().toString()));
                textField.setText("");
            }
        });

    }

    private void bdlistener () {
        profile_image = findViewById(R.id.profile_image);
        user_name = (TextView)findViewById(R.id.user_name);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        //reference = FirebaseDatabase.getInstance().getReference("Users").child(userid); // или то или то

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user_name.setText(user.getUsername());
                Log.d(TAG, "user_name" + user_name.toString());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_item)
                .setQuery(FirebaseDatabase.getInstance().getReference(), Message.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView mess_user, mess_time;
                BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));

            }
        };

        listOfMessages.setAdapter(adapter); //Возможно из-за этого ничего рабоать не будет (Работает)
        adapter.startListening(); // 01.05 вернул, потому что без него не работало
    }
}