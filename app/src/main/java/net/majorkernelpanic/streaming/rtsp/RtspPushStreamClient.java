package net.majorkernelpanic.streaming.rtsp;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.rtsp.RtspClient.Callback;

import android.os.Handler;
import android.util.Log;

import java.util.Random;

/**
 * Created by aaa on 2015/11/25.
 */
public class RtspPushStreamClient implements Callback {
    private static final String TAG = "RtspPushStreamClient";
    //    private static int localPort = 8100;
    RtspPushClient client;
    String mSdp;
    Handler mUserHandler;//

    public RtspPushStreamClient(Handler handler) {
        client = new RtspPushClient(this);
        mUserHandler = handler;
    }

    public void startRtspClient(String ip, String rtmp_path) {
        client.start_connect(ip, 554, rtmp_path);
    }

    public void switchCamera() {
        client.switchCamera();
    }

    public void setFlash(boolean mode) {
        client.setFlash(mode);
    }

    public void stopRtspClient() {
        client.stop_connect();
    }

    public static class RtspPushClient {
        private RtspClient client;
        private String request_uri;
        String mSdp;

        public RtspPushClient(RtspPushStreamClient streamClient) {
            client = new RtspClient();
            client.setCallback(streamClient);
        }

        public void start_connect(String ip, int port, String path) {
            //"rtsp://192.168.0.5:9010/"
            client.setServerAddress(ip, port);
            client.setStreamPath("/" + path);//"/live/rtsp_test"

            //setCamera --CAMERA_FACING_FRONT
            SessionBuilder builder = SessionBuilder.getInstance().clone();
//            builder.setCamera(CameraInfo.CAMERA_FACING_FRONT);
//            builder.setAudioEncoder(SessionBuilder.AUDIO_AAC).setVideoEncoder(SessionBuilder.VIDEO_H264);
            builder.setAudioEncoder(SessionBuilder.AUDIO_AAC).setVideoEncoder(SessionBuilder.VIDEO_H264);
            SessionBuilder b = SessionBuilder.getInstance();

            Random rand = new Random();
            int localPort = rand.nextInt(1000) + 8100; //8100-9100

            localPort = (localPort & 0xFFFE); /* turn to even number */
            Session session = builder.build();
            client.setSession(session);
            client.startStream();
        }

        public void switchCamera() {
//            client.switchCamera();
        }

        public void setFlash(boolean mode) {
//            client.setFlash(mode);
        }

        public void stop_connect() {
            client.stopStream();
        }
    }

    @Override
    public void onRtspUpdate(int message, Exception exception) {
        // TODO Auto-generated method stub
        Log.d(TAG, "message:" + message);

        mUserHandler.sendEmptyMessage(message);
    }

}

