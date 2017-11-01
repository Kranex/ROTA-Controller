package io.github.kranex.rota;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket; // the socket to the server.

    public OutputStream out; // the output stream.
    public BufferedReader in; // the input stream.

    public Client(String host, int port){

        openSocket(host, port);
    }

    /* opens a socket to host:port, and initialises in and out */
    private boolean openSocket(String host, int port){
        try {
            socket = new Socket(host, port);
            Log.d("CLIENT", "isConnected: " + socket.isConnected());
            out = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(Exception e){
            Log.e("ERROR",e.getMessage());
            kill();
            return false;
        }
        return true;
    }

    /* closes out, in and socket */
    public void kill(){
        try{
            out.close();
        }catch(Exception e){
            //TODO
        }
        try {
            in.close();
        }catch(Exception e){
            //TODO
        }
        try {
            socket.close();
        }catch(Exception e) {
            //TODO
        }
    }
    /* returns if the client is connected. */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}
