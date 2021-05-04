package com.khn.game;

import android.graphics.Bitmap;

public class BallF {
    int ball_width, ball_height, status;
    float ball_x, ball_y, ball_to_x, ball_to_y, ball_speed_y;
    Bitmap ball_info;

    BallF(float x, float y, float to_x, float to_y, int[] speed_y, Bitmap bitmap, int status){
        this.ball_y = y;
        this.ball_to_x = to_x;
        this.ball_to_y = to_y;
        this.ball_info = bitmap;
        this.ball_width = ball_info.getWidth();
        this.ball_height = ball_info.getHeight();
        this.ball_x = x - (ball_width/2);
        this.status = status;
        this.ball_speed_y = speed_y[status];
    }

    public int get_width() {
        return this.ball_width;
    }
    public int get_height() {
        return this.ball_height;
    }
    public void set_x(float input) {
        this.ball_x = input;
    }
    public float get_x() {
        return this.ball_x;
    }
    public void set_y(float input) {
        this.ball_y = input;
    }
    public float get_y() {
        return this.ball_y;
    }
    public void set_to_x( float input) {
        this.ball_to_x = input;
    }
    public float get_to_x () {
        return this.ball_to_x;
    }
    public void set_to_y( float input) {
        this.ball_to_y = input;
    }
    public float get_to_y () {
        return this.ball_to_y;
    }
    public float get_speed_y () {
        return this.ball_speed_y;
    }
    public int get_status () {
        return this.status;
    }

    public boolean collision (float x1, float y1, int w1, int h1, float x2, float y2, int w2, int h2){
        if(x1 + w1 >= x2 && x1 <= x2 + w2 && y1 + h1 >= y2 && y1 <= y2 + h2) {
            return true;
        }
        return false;
    }
}
