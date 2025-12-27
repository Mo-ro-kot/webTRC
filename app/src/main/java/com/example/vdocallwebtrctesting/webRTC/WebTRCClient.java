package com.example.vdocallwebtrctesting.webRTC;

import android.content.Context;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class WebTRCClient {
    private final Context context;
    private final String username;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private CameraVideoCapturer videoCapturer;
    private VideoSource localVideoSource;
    private AudioSource localAudioSource;
    private String localTrackId = "local_track";
    private String localStreamId = "local_stream";
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;

    private MediaStream localStream;
    private List<PeerConnection.IceServer> iceServer = new ArrayList<>();
    private EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();

    public WebTRCClient(Context context, PeerConnection.Observer observer, String username) {
        this.context = context;
        this.username = username;
        initPeerConnectionFactory();
        peerConnectionFactory = createPeerConnectionFactory();
        //add configuration to ice server
        iceServer.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
        iceServer.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());
        peerConnection = createPeerConnection(observer);
        localVideoSource = peerConnectionFactory.createVideoSource(false);
        localAudioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());

    }

    // ini peer connection section
    private void initPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions options =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setFieldTrials("WebTRC-H264HighProfile/Enabled/")
                        .setEnableInternalTracer(true).createInitializationOptions();

        PeerConnectionFactory.initialize(options);
    }

    private PeerConnectionFactory createPeerConnectionFactory() {
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption = false;
        options.disableNetworkMonitor = false;
        return PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setOptions(options).createPeerConnectionFactory();

    }

    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
        return peerConnectionFactory.createPeerConnection(iceServer, observer);
    }

// innit ui like surface view renders

    public void initSurfaceViewRendered(SurfaceViewRenderer viewRenderer) {
        //It initializes the video view so WebRTC can draw video frames on it.
        viewRenderer.setEnableHardwareScaler(true);
        viewRenderer.setMirror(true);
        viewRenderer.init(eglBaseContext, null);

    }
    //vdo view your own camera feed
   public  void initLocalSurfaceView(SurfaceViewRenderer view){
    initSurfaceViewRendered(view);
    startLocalvideoStreaming(view);
   }
// staart the local stream and prepare to start the vdo
    private void startLocalvideoStreaming(SurfaceViewRenderer view) {
        SurfaceTextureHelper helper = SurfaceTextureHelper.create(
                Thread.currentThread().getName(), eglBaseContext
        );

        //for video
        videoCapturer = getVideoCapturer();
        videoCapturer.initialize(helper, context, localVideoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 360,15);
        localVideoTrack = peerConnectionFactory.createVideoTrack(
                localTrackId+ "video", localVideoSource
        );
        localVideoTrack.addSink(view);

        // for audio
        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId+"audio", localAudioSource);
        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
        localStream.addTrack(localVideoTrack);
        localStream.addTrack(localAudioTrack);
        peerConnection.addStream(localStream);

    }
    // to start streaming we capture our vdo and then send it to other peer
    private CameraVideoCapturer getVideoCapturer(){
        Camera2Enumerator enumerator = new Camera2Enumerator(context);

        String[] deviceNames = enumerator.getDeviceNames();
        for (String device: deviceNames){
            if (enumerator.isFrontFacing(device)){
                return enumerator.createCapturer(device, null);
            }
        }
        throw new IllegalStateException("front camera is not found");
    }

    //vdo view from the other device
   public  void initRemoteSurfaceView(SurfaceViewRenderer view){
        initSurfaceViewRendered(view);
   }

}
