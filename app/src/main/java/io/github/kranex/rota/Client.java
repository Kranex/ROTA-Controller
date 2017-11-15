/*
** Copyright 2017 Oliver Strik
** oliverstrik@gmail.com
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**    http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

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

    private Boolean running = false;

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
            socket = null;
        }catch(Exception e) {
            //TODO
        }
        setRunning(false);
    }
    /* returns if the client is connected. */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean isRunning(){
        return running;
    }
    public void setRunning(Boolean bol){
        running = bol;
    };
}
