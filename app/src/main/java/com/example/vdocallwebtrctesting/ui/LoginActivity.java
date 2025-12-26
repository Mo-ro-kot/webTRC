package com.example.vdocallwebtrctesting.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vdocallwebtrctesting.databinding.ActivityLoginBinding;
import com.example.vdocallwebtrctesting.repository.MainRepository;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding views;
    private MainRepository mainRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        init();

    }
     private void init (){
        mainRepository = MainRepository.getInstance();
   views.enterBtn.setOnClickListener(v->{
           // login to firebase
       mainRepository.login(
               views.username.getText().toString(),()->{
                   // success navigate to call activity
                   startActivity(new Intent(LoginActivity.this, CallActivity.class));
               }

       );

     });

     }
}