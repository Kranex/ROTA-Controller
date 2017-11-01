package io.github.kranex.rota;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;


public class RotaActivity extends AppCompatActivity {

    private Client client; // the client instance.
    private JoystickView joystickView; // the main view.
    int leftID = -1, rightID = -1; // persistent id's for the left and right touches.

    // Create an anonymous implementation of OnClickListener
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        /* when the touch screen is touched... */
        public boolean onTouch(View v, MotionEvent event) {
            // apparently I need a maskedAction over an Action.
            int maskedAction = event.getActionMasked();

            // Get the index and id of the pointer associated with the action.
            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);

            // switch through all possible actions.
            switch (maskedAction) {

                // ACTION_DOWN and ACTION_POINTER_DOWN, both occur when a finger is placed on the
                // Screen. POINTER_DOWN is called when the second or third or ... is placed.
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    // if the touch occurs on the left and a joystick doesn't already exist.
                    if(event.getX(pointerIndex) < getWindow().getDecorView().getWidth()/2
                            && !joystickView.leftJoy.isVisible()){

                        joystickView.leftJoy.setVisible(true); // show the left joystick.

                        joystickView.leftJoy.setOriginXY( // set the center of the joystick.
                                event.getX(pointerIndex),
                                event.getY(pointerIndex));

                        joystickView.leftJoy.setXY(
                                event.getX(pointerIndex), // set the hat of the joystick.
                                event.getY(pointerIndex));
                        leftID = pointerId;
                    }

                    // if the touch occurs on the right, and a joystick doesn't already exist.
                    if(event.getX(pointerIndex) > getWindow().getDecorView().getWidth()/2
                            && !joystickView.rightJoy.isVisible()){

                        joystickView.rightJoy.setVisible(true);
                        joystickView.rightJoy.setOriginXY(
                                event.getX(pointerIndex),
                                event.getY(pointerIndex));
                        joystickView.rightJoy.setXY(
                                event.getX(pointerIndex),
                                event.getY(pointerIndex));
                        rightID = pointerId;
                    }
                    break;
                }
                // action move is called when ANY current finger is moved.
                case MotionEvent.ACTION_MOVE: {
                    // if it was the finger associated with the left joystick.
                    if(leftID >= 0) {
                        // update the location of the left joy hat.
                        joystickView.leftJoy.setXY(event.getX(
                                event.findPointerIndex(leftID)),
                                event.getY(event.findPointerIndex(leftID)));
                    }
                    // if it was the finger associated with the right joystick.
                    if(rightID >= 0) {
                        // update the location of the right joy hat.
                        joystickView.rightJoy.setXY(
                                event.getX(event.findPointerIndex(rightID)),
                                event.getY(event.findPointerIndex(rightID)));
                    }

                    break;
                }
                // ACTION_UP and ACTION_POINTER_UP are called when a finger is removed from
                // the screen. CANCEL is when a finger is canceled however that happens.
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    // if there is a left joy and if the finger removed was the one associated
                    // with it.
                    if(leftID >= 0 && leftID == pointerId) {
                        // hide the left joy.
                        joystickView.leftJoy.setVisible(false);
                        // remove the left ID.
                        leftID = -1;
                    }
                    // otherwise if there's a right joy and the finger removed was the one
                    // associated with it.
                    else if(rightID >= 0 && rightID == pointerId) {
                        // hide the right joy.
                        joystickView.rightJoy.setVisible(false);
                        // remove the right ID.
                        rightID = -1;
                    }
                    break;
                }
            }
            joystickView.invalidate(); // redraw the joysticks.
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // required.

        // set the screen layout. I.e. fullscreen and hide the top bar.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // initialize the joystick view.
        joystickView = new JoystickView(this);

        // apply the touch listener to the joystick view.
        joystickView.setOnTouchListener(touchListener);

        // set the view to joystick view.
        setContentView(joystickView);

    }
    @Override
    protected void onResume(){
        super.onResume();

        // start a thread to connect to the server as to not hold up the app.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // prevents the client thread from taking higher priority than the app.
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                while(!Thread.interrupted()){
                    // if client is not connected;
                    if(client == null || !client.isConnected()) {
                        startClient();
                    }else {
                        // construct the data to be sent.
                        final byte[] data = {42, (byte) (joystickView.leftJoy.getInfluenceX()),
                                (byte) (joystickView.leftJoy.getInfluenceY()),
                                (byte) (joystickView.rightJoy.getInfluenceX()),
                                (byte) (joystickView.rightJoy.getInfluenceY())};
                        try {
                            client.out.write(data);
                            client.out.flush(); // not strictly required but useful.
                        } catch (IOException e) {
                            Log.e("SEND", e.getMessage());
                        }
                        try {
                            Thread.sleep(5);
                        }catch(Exception e){};
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onStop(){
        super.onStop();
        // if the app is ever frozen, ie. screen locks, or user minimises app, kill it.
        // the client doesn't like that ****.
        finish();
    }

    /* starts the client */
    protected void startClient(){
        joystickView.setConnected(false);
        do {
            Log.d("debug","Connecting to client...");
            client = new Client("192.168.12.1", 55455); // create new client.
            try {
                Thread.sleep(1000);
            }catch(Exception e){}

        }while(!client.isConnected());
        joystickView.setConnected(true);
        joystickView.invalidate();
    }
}
