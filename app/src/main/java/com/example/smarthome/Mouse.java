package com.example.smarthome;

public class Mouse {
    float x,y;

    Mouse(){


    }

    public Mouse(float x, float y) {
        this.x = x-50;
        this.y = y-50;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
