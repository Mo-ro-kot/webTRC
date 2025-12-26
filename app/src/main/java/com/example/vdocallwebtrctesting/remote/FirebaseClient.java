package com.example.vdocallwebtrctesting.remote;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vdocallwebtrctesting.utils.DataModel;
import com.example.vdocallwebtrctesting.utils.ErrorCallBack;
import com.example.vdocallwebtrctesting.utils.NewEventCallBack;
import com.example.vdocallwebtrctesting.utils.SuccessCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class FirebaseClient {
    private final Gson gson = new Gson();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String currentUsername;

    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";

    public void login(String username, SuccessCallBack callBack){
      dbRef.child(username).setValue("").addOnCompleteListener(task ->{
          currentUsername = username;
          callBack.onSuccess();
      });
    }
    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if the model data reach the target model then retrun connect call
                if (dataSnapshot.child(dataModel.getTarget()).exists()){
                    dbRef.child(dataModel.getTarget()).child(LATEST_EVENT_FIELD_NAME)
                            .setValue(gson.toJson(dataModel));
                }else {
                    errorCallBack.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
           errorCallBack.onError();
            }
        });

    }
    public void observeIncomingLatestEvent(NewEventCallBack callBack){

        dbRef.child(currentUsername).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try{
                        String data = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                        DataModel dataModel = gson.fromJson(data,DataModel.class);
                        callBack.onNewEventRecieved(dataModel);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }
}
