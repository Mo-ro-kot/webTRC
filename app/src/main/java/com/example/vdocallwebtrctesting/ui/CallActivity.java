package com.example.vdocallwebtrctesting.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vdocallwebtrctesting.R;
import com.example.vdocallwebtrctesting.databinding.ActivityCallBinding;
import com.example.vdocallwebtrctesting.repository.MainRepository;
import com.example.vdocallwebtrctesting.utils.DataModelType;

public class CallActivity extends AppCompatActivity {

    private ActivityCallBinding views;
    private MainRepository mainRepository;//create mainrepo varible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCREATE test log", "onCreate works!");

        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        init();
    }
    private  void init(){
        mainRepository = MainRepository.getInstance();
    views.callBtn.setOnClickListener(v-> {

        // start a call request here
     mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString(),()->{
         Toast.makeText(this, "couldn't find target", Toast.LENGTH_SHORT).show();
     });
    });
    mainRepository.subscribeForLatestEvent(data ->{
        Log.d("CALL_EVENT", "event received: " + data.getType());
        if (data.getType()== DataModelType.StartCall){
            runOnUiThread(()->{
                // when user recieved call they will get pop up for accept call or reject button
                views.incomingNameTV.setText(data.getSender()+"is calling you");
                views.incomingCallLayout.setVisibility(View.VISIBLE);
                views.incomingCallLayout.bringToFront();
                views.acceptButton.setOnClickListener(v->{
//                    start the call here
                    views.incomingCallLayout.setVisibility(View.GONE);
                });
                views.rejectButton.setOnClickListener(v->{
                    //call got rejected
                    views.incomingCallLayout.setVisibility(View.GONE);
                });
            });
        }
    });
    }
}