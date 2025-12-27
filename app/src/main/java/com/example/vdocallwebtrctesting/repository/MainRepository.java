package com.example.vdocallwebtrctesting.repository;

import android.content.Context;
import android.util.Log;

import com.example.vdocallwebtrctesting.remote.FirebaseClient;
import com.example.vdocallwebtrctesting.utils.DataModel;
import com.example.vdocallwebtrctesting.utils.DataModelType;
import com.example.vdocallwebtrctesting.utils.ErrorCallBack;
import com.example.vdocallwebtrctesting.utils.NewEventCallBack;
import com.example.vdocallwebtrctesting.utils.SuccessCallBack;
import com.example.vdocallwebtrctesting.webRTC.MyPeerConnectionObserver;
import com.example.vdocallwebtrctesting.webRTC.WebRTCClient;
import com.google.android.material.animation.AnimatableView;
import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

public class MainRepository implements WebRTCClient.Listener {
    private FirebaseClient firebaseClient;
    public Listener listener;
    private final Gson gson = new Gson();
    private WebRTCClient webRTCClient;
    private String currentUsername;
    private SurfaceViewRenderer remoteView;
    private void updateCurrentUsername(String username){
        this.currentUsername = username;
    }
    private static MainRepository instance;
    private String target;
    private MainRepository (){
        this.firebaseClient = new FirebaseClient();
    }

    public  static MainRepository getInstance(){
        if (instance == null){
            instance = new MainRepository();
        }
        return instance;


    }
    public void login (String username, Context context, SuccessCallBack callBack){
        firebaseClient.login(username, ()->{
            updateCurrentUsername(username);
            this.webRTCClient = new WebRTCClient(context, new MyPeerConnectionObserver(){
                @Override
                public void onAddStream(MediaStream mediaStream) {
                    super.onAddStream(mediaStream);
                   try{
                       mediaStream.videoTracks.get(0).addSink(remoteView);
                   }catch (Exception e){
                       e.printStackTrace();
                   }

                }

                @Override
                public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                    super.onConnectionChange(newState);
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED && listener != null){
                        listener.webRTCConnected();
                    }

                    if (newState == PeerConnection.PeerConnectionState.CLOSED ||
                    newState == PeerConnection.PeerConnectionState.DISCONNECTED)
                    {
                        if (listener != null){
                            listener.webRTCClosed();
                        }
                    }
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {
                    super.onIceCandidate(iceCandidate);
                    webRTCClient.sendIceCandidate(iceCandidate,target);
                }
            }, username);
            webRTCClient.listener= this;
            callBack.onSuccess();
        });
    }

    public void initLocalView(SurfaceViewRenderer view){
        webRTCClient.initLocalSurfaceView(view);
    }
    public void initRemoteView(SurfaceViewRenderer view){
        webRTCClient.initRemoteSurfaceView(view);
        this.remoteView = view;
    }

    public void startCall(String target){
        webRTCClient.call(target);
    }
    public void switchCamera( ){
        webRTCClient.switchCamera();
    }
    public void toggleAudio(Boolean shouldBeMuted){
        webRTCClient.toggleAudio(shouldBeMuted);
    }
    public void toggleVideo(Boolean shouldBeMuted){
        webRTCClient.toggleVideo(shouldBeMuted);
    }
    public void sendCallRequest(String target, ErrorCallBack errorCallBack){
        firebaseClient.sendMessageToOtherUser(
                new DataModel(currentUsername,target,null, DataModelType.StartCall),errorCallBack
        );
    }

    public void endCall(){
        webRTCClient.closeConnectio();
    }
      public void subscribeForLatestEvent(NewEventCallBack callBack){
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()){
                case Offer:
                    Log.d("LOG_offer_main_122", "jenh offer ot te");

                    this.target = model.getSender();
                   webRTCClient.onRemoteSessionRecieved(new SessionDescription(
                           SessionDescription.Type.OFFER,model.getData()
                   ));
                   webRTCClient.answer(model.getSender());
                    break;
                case Answer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionRecieved(new SessionDescription(
                            SessionDescription.Type.ANSWER, model.getData()
                    ));
                    Log.d("LOG_answer_main_135", "jenh answer ot te");
                    break;
                case IceCandidate:
                    try{
                        IceCandidate candidate = gson.fromJson(model.getData(),IceCandidate.class);
                        webRTCClient.addIceCandidate(candidate);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d("LOG_IceCandidate_main_144", "jenh ICE ot te");
                    break;
                case StartCall:

                    this.target = model.getSender();
                    callBack.onNewEventRecieved(model);
                    Log.d("LOG_StartCall_main_149", "start call");
                    break;
            }


      });
      }

    @Override
    public void onTransferDatatoOtherPeer(DataModel model) {
        firebaseClient.sendMessageToOtherUser(model, ()->{});
    }

    public interface Listener{
        void webRTCConnected();
        void webRTCClosed();
    }
}
