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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class JoystickView extends View {

    public Joystick leftJoy, rightJoy;
    private boolean connected = false;
    private Paint text;

    public JoystickView(Context context) {
        super(context);
        // init joysticks to 0,0, with a size of half the screen height, and set invisible.
        leftJoy = new Joystick(0, 0, getScreenHeight() / 2);
        leftJoy.setVisible(false);
        rightJoy = new Joystick(0, 0, getScreenHeight() / 2);
        rightJoy.setVisible(false);

        // set the text colour to a semi-transparent dark red and it's size to a 16th of
        // the screen height.
        text = new Paint();
        text.setARGB(200, 160, 0, 0);
        text.setTextSize(getScreenHeight() / 16);

    }
    /* on a draw update */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // if client is not connected, we're connecting right?
        if(!connected){
            canvas.drawText("Connecting to ROTA...",10,getScreenHeight()/16+10,text);
        }else{
            // draw the joysticks.
            leftJoy.draw(canvas);
            rightJoy.draw(canvas);
        }
    }

    /* set's if the client is connected or not. */
    public void setConnected(boolean connected){

        this.connected = connected;
    }
    /* returns the screen height */
    private int getScreenHeight(){

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    /* returns the screen width */
    private int getScreenWidth(){

        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
