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

public class CallActivity extends AppCompatActivity implements MainRepository.Listener {

    private ActivityCallBinding views;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicroPhoneMuted = false;

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

    mainRepository.initLocalView(views.localView);
    mainRepository.initRemoteView(views.remoteView);
    mainRepository.listener = this;

    mainRepository.subscribeForLatestEvent(data ->{
        Log.d("LOG_CALL_EVENT", "event received: " + data.getType());
        if (data.getType()== DataModelType.StartCall){
            runOnUiThread(()->{
                // when user recieved call they will get pop up for accept call or reject button
                views.incomingNameTV.setText(data.getSender()+"is calling you");
                views.incomingCallLayout.setVisibility(View.VISIBLE);
                views.incomingCallLayout.bringToFront();
                views.acceptButton.setOnClickListener(v->{
//                    start the call here
                    Log.d("LOG_Startcall_callact_60", "Your message here");

                    mainRepository.startCall(data.getSender());
                    views.incomingCallLayout.setVisibility(View.GONE);
                });
                views.rejectButton.setOnClickListener(v->{
                    //call got rejected
                    views.incomingCallLayout.setVisibility(View.GONE);
                });
            });
        }
    });

    views.switchCameraButton.setOnClickListener(v->{
        mainRepository.switchCamera();
    });

    views.micButton.setOnClickListener(v->{
    if(isMicroPhoneMuted){
        views.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
    }else {
        views.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
    }
    mainRepository.toggleAudio(isMicroPhoneMuted);
    isMicroPhoneMuted =! isMicroPhoneMuted;
      
    });
     views.videoButton.setOnClickListener(v->{
   if(isCameraMuted){
       views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
   }else{
       views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
   }
   mainRepository.toggleVideo(isCameraMuted);
    isCameraMuted =! isCameraMuted;
     });
    views.endCallButton.setOnClickListener(v->{
   mainRepository.endCall();
   finish();
    });
    }



    @Override
    public void webRTCConnected() {
    runOnUiThread(()->{
        views.incomingCallLayout.setVisibility(View.GONE);
        views.callLayout.setGravity(View.VISIBLE);
        views.whoToCallLayout.setVisibility(View.GONE);
    });
    }

    @Override
    public void webRTCClosed() {
    runOnUiThread(this:: finish);
    }
}