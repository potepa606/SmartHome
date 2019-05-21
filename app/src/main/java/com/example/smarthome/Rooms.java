package com.example.smarthome;

import android.graphics.Bitmap;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Rooms {

    private String Name;
    private Bitmap bitmap;
    private List<Button> buttons;

    Rooms(){
    //empty
    }

    public Rooms(Bitmap bitmap, List<Button> buttons) {
        this.bitmap = bitmap;
        this.buttons = buttons;
    }

    public Rooms(Bitmap bitmap, List<Button> buttons,String name) {
        this.bitmap = bitmap;
        this.buttons = buttons;
        this.Name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
