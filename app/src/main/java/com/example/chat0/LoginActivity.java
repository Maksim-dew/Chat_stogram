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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    private EditText email_login;
    private EditText password_login;
    private Button button_login;
    private TextView register_txt;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

/*        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#7E9C8D"));
        getSupportActionBar().setTitle("Login");

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        button_login = findViewById(R.id.button_login);
        register_txt = findViewById(R.id.register_txt);

        mAuth = FirebaseAuth.getInstance();

        register_txt.setOnClickListener(new View.OnClickListener() {
            //тут происходит обработка нажатия на текст с регистрацией и переход на неё
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrActivity.class);
                startActivity(intent);
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_email = email_login.getText().toString();
                String txt_password = password_login.getText().toString();

                if (txt_email.isEmpty() || txt_password.isEmpty()){
                    //Тут происходит проверка на пустоту ввода
                    Toast.makeText(LoginActivity.this, "Заполните поля ввода", Toast.LENGTH_LONG).show();
                } else {
                    //Если пользователь ввел коректыне данные происходит вход в приложении и запрос в БД
                    mAuth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:success");
                                        //FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Неправильно введен Email или пароль", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}