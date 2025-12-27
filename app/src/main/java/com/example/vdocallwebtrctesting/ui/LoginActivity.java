package com.example.vdocallwebtrctesting.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vdocallwebtrctesting.databinding.ActivityLoginBinding;
import com.example.vdocallwebtrctesting.repository.MainRepository;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.security.Permission;
import java.util.List;

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
       PermissionX.init(this)
               .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                       .request((allGranted, grantedList, deniedList) -> {
                           if(allGranted){
                               mainRepository.login(
                                       views.username.getText().toString(),getApplicationContext(),()->{
                                           // success navigate to call activity
                                           startActivity(new Intent(LoginActivity.this, CallActivity.class));
                                       }

                               );
                           }
                       });
           // login to firebase


     });

     }
}