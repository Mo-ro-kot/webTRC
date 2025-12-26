package com.example.vdocallwebtrctesting.repository;

import com.example.vdocallwebtrctesting.remote.FirebaseClient;
import com.example.vdocallwebtrctesting.utils.DataModel;
import com.example.vdocallwebtrctesting.utils.DataModelType;
import com.example.vdocallwebtrctesting.utils.ErrorCallBack;
import com.example.vdocallwebtrctesting.utils.NewEventCallBack;
import com.example.vdocallwebtrctesting.utils.SuccessCallBack;

public class MainRepository {
    private FirebaseClient firebaseClient;
    private String currentUsername;
    private void updateCurrentUsername(String username){
        this.currentUsername = username;
    }
    private static MainRepository instance;
    private MainRepository (){
        this.firebaseClient = new FirebaseClient();
    }

    public  static MainRepository getInstance(){
        if (instance == null){
            instance = new MainRepository();
        }
        return instance;
    }
    public void login (String username, SuccessCallBack callBack){
        firebaseClient.login(username, ()->{
            updateCurrentUsername(username);
            callBack.onSuccess();
        });
    }
    public void sendCallRequest(String target, ErrorCallBack errorCallBack){
        firebaseClient.sendMessageToOtherUser(
                new DataModel(currentUsername,target,null, DataModelType.StartCall),errorCallBack
        );
    }
      public void subscribeForLatestEvent(NewEventCallBack callBack){
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()){
                case Offer:

                    break;
                case Answer:
                    break;
                case IceCandidate:
                    break;
                case StartCall:
                    callBack.onNewEventRecieved(model);
                    break;
            }


      });
      }
}
