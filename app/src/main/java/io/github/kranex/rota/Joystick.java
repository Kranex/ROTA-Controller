package io.github.kranex.rota;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;

public class Joystick extends Drawable{


    private int alpha;
    private float x,y, x0, y0, d;
    private Paint stick;
    private Paint bounds;

    public Joystick(float x, float y, float diameter){
        // set the origin and the hat location to x,y;
        this.x = this.x0 = x;
        this.y = this.y0 = y;
        d = diameter;

        // set hat(stick) and the body colours.
        alpha = 100;
        stick = new Paint();
        bounds = new Paint();
        stick.setARGB(alpha,0,0,120);
        bounds.setARGB(alpha,100,120,150);
    }
    /* when a draw is called */
    @Override
    public void draw(Canvas canvas){
        /* if it's visible */
        if(isVisible()) {
            //canvas.drawCircle(x0, y0, d/2, bounds); // i like circles but the current rota won't work well with them.
            // draw a rectangle body, from the origin.
            canvas.drawRect(x0-d/2,y0-d/2, x0+d/2, y0+d/2, bounds);
            // draw a circular hat from the hat location.
            canvas.drawCircle(x, y, d/4, stick);

        }
    }

    /* sets the visibility of the joy */
    public void setVisible(boolean val){
        x0 = y0 = x = y = 0;    // set the hat and the origin to the same location,
                                // thus get influence will return 0;
        this.setVisible(val, false);
    }
    /* returns the -127 to 127 as the distance of the hat from the center in the x axis */
    public int getInfluenceX(){
        int inf = (int)((2*(getX()-getX0()))/d*127);
        return inf;
    }
    /* returns the -127 to 127 as the distance of the hat from the center in the y axis */
    public int getInfluenceY(){
        int inf = (int)((2*(getY0()-getY()))/d*127);
        return inf;
    }
    /* sets the origin of the joy */
    public void setOriginXY(float x, float y){
        x0 = x;
        y0 = y;
    }

    /* returns X, Y, X0 or Y) */
    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }
    public float getX0(){
        return this.x0;
    }
    public float getY0(){
        return this.y0;
    }

    /* set the hat location, use math to prevent the hat from being outside the body */
    public void setXY(float x, float y){
        if(x >= x0-d/2 && x <= x0+d/2) {
            this.x = x;
        }else{
            if(x > x0)this.x = this.x0 + d/2;
            else this.x = this.x0 - d/2;

        }
        if(y >= y0-d/2 && y <= y0+d/2){
            this.y = y;
        }else{
            if(y > y0)this.y = this.y0 + d/2;
            else this.y = this.y0 - d/2;
        }

    }
    // the same thing but for circles.
    /*public void setXY(float x, float y){
        float r = d/2;
        float x1 = getX0()-x;
        float y1 = getY0()-y;
        float l = (float) Math.sqrt(x1*x1 + y1*y1);
        if(l > r){

            double a = Math.atan(Math.abs(x1)/Math.abs(y1));

            if(x1 != 0){
                this.x = (float) (getX0()+(((d)*Math.sin(a))/2)*(x1/Math.abs(x1)*-1));
            }else{
                this.x = 0;
            }
            if(y1 != 0){
                this.y = (float) (getY0()+(((d)*Math.cos(a))/2)*(y1/Math.abs(y1)*-1));
            }else{
                this.y = 0;
            }
        }else{
            this.x = x;
            this.y = y;
        }
    }*/

    // set the alpha, required by Drawable.
    public void setAlpha(int a){
        alpha = a;
    }
    // set the colorFilter, required by Drawable.
    public void setColorFilter(ColorFilter filter){
        stick.setColorFilter(filter);
        bounds.setColorFilter(filter);
    }
    // required by Drawable.
    public int getOpacity(){
        return alpha;
    }
}
