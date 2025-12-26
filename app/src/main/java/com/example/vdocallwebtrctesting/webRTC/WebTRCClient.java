//package com.example.vdocallwebtrctesting.webRTC;
//
//import android.content.Context;
//
//import org.webrtc.DefaultVideoDecoderFactory;
//import org.webrtc.DefaultVideoEncoderFactory;
//import org.webrtc.EglBase;
//import org.webrtc.PeerConnection;
//import org.webrtc.PeerConnectionFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class WebTRCClient {
//  private final Context context;
//  private  final String username;
//  private  PeerConnectionFactory peerConnectionFactory;
//  private PeerConnection peerConnection;
//  private List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//  private EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
//   public WebTRCClient(Context context, PeerConnection.Observer observer,String username){
//    this.context= context;
//    this.username= username;
//    initPeerConnectionFactory();
//    peerConnectionFactory = createPeerConnectionFactory();
//    // add configuration to ice server
////     iceServers.add(PeerConnection.IceServer.builder("turn:a.relay.metered.ca:443?transport=tcp")
////             .setUsername("")
////     )
//
//  }
//  private  void initPeerConnectionFactory(){
//     PeerConnectionFactory.InitializationOptions options =
//             PeerConnectionFactory.InitializationOptions.builder(context)
//                             .setFieldTrials("WebTRC-H264HighProfile/Enabled/")
//                                     .setEnableInternalTracer(true).createInitializationOptions();
//
//    PeerConnectionFactory.initialize(options);
//  }
//
////  private PeerConnectionFactory createPeerConnectionFactory (){
////     PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
////     options.disableEncryption= false;
////     options.disableNetworkMonitor= false;
////     return  PeerConnectionFactory.builder()
////             .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
////             .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
////             .setOptions(options).createPeerConnectionFactory();
////
////  }
////   private PeerConnection createPeerConnection (PeerConnection.Observer observer){
////     return peerConnectionFactory.createPeerConnection(,observer)
////   }
////}
//
