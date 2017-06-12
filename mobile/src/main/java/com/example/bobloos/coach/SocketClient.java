package com.example.bobloos.coach;

/**
 * Created by raoul on 07/06/17.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Switch;

import com.example.bobloos.model.PhysStateModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import static java.lang.Integer.*;

public class SocketClient  extends WebSocketClient {

    private final MainActivity mainact;


    public SocketClient(URI serverUri, MainActivity main, Draft draft) {
        super(serverUri, draft);
        mainact = main;

    }

    public SocketClient(URI serverURI, MainActivity main) {
        super(serverURI);
        mainact = main;


    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("LOG", "socket connected");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        String[] data = message.split(",");
        Log.d("message", String.valueOf(data[0]));
        if (Objects.equals(data[0], "OK")) {
            try {

                int state = Integer.parseInt(data[1]);
                if (state != 0 ){
                    mainact.defineMoment(state);
                }
            } catch (NumberFormatException e) {
                Log.d("message", "not a number");
                mainact.toggleCoach(true);

            }
        }
        if (Objects.equals(data[0], "START")){
            mainact.toggleCoach(true);


        }

        if (Objects.equals(data[0],"CANCEL")) {
            Log.d("toggle","false");
            mainact.toggleCoach(false);
        }

    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}