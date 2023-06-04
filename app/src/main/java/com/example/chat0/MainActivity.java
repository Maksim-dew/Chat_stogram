package com.example.chat0;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.example.chat0.Fragments.ChatsFragment;
import com.example.chat0.Fragments.ProfileFragment;
import com.example.chat0.Fragments.UsersFragment;
import com.example.chat0.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView user_name;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private RelativeLayout activity_main;
/*
    public String txt_username;
    public void txt_username () {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           txt_username = extras.getString("key");
        }
        txt_username = findViewById(R.id.user_nikname);
        String result = txt_username.toString();
        txt_username.setText(result);
    }
 */
    private FirebaseListAdapter<Message> adapter;
    private FirebaseListOptions<Message> options;
    private FloatingActionButton sendBtn;

    @Override
    protected void onStart() {
        super.onStart();
        displayAllMessages();
    }
    /*
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            AuthUI.getInstance().createSignInIntentBuilder().build();
                        } else {
                            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                            displayAllMessages();
                        }
                    }
                });

     */
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SING_IN_CODE) {
            if(resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            } else {
                Snackbar.make(activity_main, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }
*/
    private void bdlistener () {
        profile_image = findViewById(R.id.profile_image);
        user_name = (TextView)findViewById(R.id.user_name);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user_name.setText(user.getUsername());
                Log.d(TAG, "user_name" + user_name.toString());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/*        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userid = null;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userid = extras.getString("key");
        }*/
/*
        db.collection("users")
                .whereEqualTo("username", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User user = documentSnapshot.toObject(User.class);
                                user_nikname.setText(user.getUsername());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

 */
/*
        assert userid != null;
        db.collection("users").document(userid)
        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                User user = documentSnapshot.toObject(User.class);
                user_nikname.setText(user.getUsername());
                Log.d(TAG, "usernikname" + user_nikname.toString());
                if (user.getImageURL().equals("default")) {

                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                }

            }
        });

 */
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bdlistener ();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#7E9C8D"));
        getSupportActionBar().setTitle("");

        activity_main = findViewById(R.id.activity_main);
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

        TabLayout tabLayout = findViewById(R.id. tab_layout);
        ViewPager viewPager = findViewById(R.id. view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
/*
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            AuthUI.getInstance().createSignInIntentBuilder().build();
                        } else {
                            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                            displayAllMessages();
                        }
                    }
                });

 */
    }
/*
        //Проверка авторизации пользователя
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SING_IN_CODE);
        else {
            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            displayAllMessages();
        }
*/
    private void displayAllMessages() {
        /*ListView messages = findViewById(R.id.chat_messages_list);
        FirebaseListOptions.Builder<Message> builder = new FirebaseListOptions.Builder<>();
        builder
                .setLayout(R.layout.chat_list_item)
                .setQuery(FirebaseDatabase.getInstance().getReference(), Message.class)                 .setLifecycleOwner(this.getActivity());

        adapter = new FirebaseListAdapter<Message>(builder.build())

         FirebaseListOptions<Message> options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(FirebaseDatabase.getInstance().getReference(), Message.class)
                        .setLayout(R.layout.list_item)
                        .setLifecycleOwner(this)
                        .build();

        */
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    /*private void displayAllMessages() {
        ListView listofMessages = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {

            }
        };

    }

         @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
    */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment (Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}