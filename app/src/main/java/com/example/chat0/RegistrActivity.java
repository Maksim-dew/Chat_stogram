package com.example.chat0;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrActivity extends AppCompatActivity {

    public EditText user_name;
    public String userid;
    private EditText email_register;
    private EditText password_register;
    private Button button_register;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#7E9C8D"));
        getSupportActionBar().setTitle("");


        mAuth = FirebaseAuth.getInstance();

        user_name = findViewById(R.id.user_name);
        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        button_register = findViewById(R.id.button_register);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_username = user_name.getText().toString();
                String txt_email = email_register.getText().toString();
                String txt_password = password_register.getText().toString();

                if(txt_email.isEmpty() || txt_password.isEmpty() || txt_username.isEmpty()){
                    //Происходит проверка пустоты полей
                    Toast.makeText(RegistrActivity.this, "Заполните поля ввода", Toast.LENGTH_LONG).show();
                }else if (txt_password.length() < 6) {
                    Toast.makeText(RegistrActivity.this, "Длинна пароля не должна быть меньше 6 символов", Toast.LENGTH_LONG).show();
                } else {
                    // Это должно находиться в register()
                    //txt_username, txt_email, txt_password
                    register(txt_email, txt_password);
                }
            }
        });
    }

    public void niknam(View view) {
        user_name.getText().toString();
        EditText txt_username = user_name;

        Intent intent = new Intent (RegistrActivity.this, MainActivity.class);
        intent.putExtra("key", txt_username.getText());
        startActivity(intent);
    }
    // Это должно находиться в register()
    //String user_name, String password_register, String txt_password
    private void register(String txt_email,String txt_password){
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            userid = firebaseUser.getUid(); // Тут метод, который проверяет пользователей
                            /*Intent intent = new Intent (RegistrActivity.this, MainActivity.class);
                            intent.putExtra("key", userid);
                            startActivity(intent); // Пока не нужно
                             */

                            reference = FirebaseDatabase.getInstance().getReference("User").child(userid); // эта строка записывает пользователь в realtime firebase

                            //нах это?
                            //FirebaseFirestore db = FirebaseFirestore.getInstance();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("imageURL", "default");
                            hashMap.put("username", user_name.getText().toString());
                            hashMap.put("email", email_register.getText().toString());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task){
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent (RegistrActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException()); // Это я делаю для себя для записи логов на случай ошибки
                            Toast.makeText(RegistrActivity.this, "Неправильно введен Email или пароль", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
/*
    //нах это?
    private void reload() {
        Toast.makeText(RegistrActivity.this, "Вы уже авторизированы", Toast.LENGTH_LONG).show();
    }

 */
}