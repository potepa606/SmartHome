package com.example.smarthome;

public class Device {
    private String name;
    private String id;
    private String stan;

    public Device() {
        //empty constructor needed
    }

    public Device(String Nazwa, String ID,String Stan) {
        if (Nazwa.trim().equals("")) {
            Nazwa = "No Name";
        }

        this.name = Nazwa;
        this.id = ID;
        this.stan=Stan;
    }

    public String getName() {
        return name;
    }

    public void setName(String Nazwa) {
        this.name = Nazwa;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        id = ID;
    }

    public String getStan() {
        return stan;
    }
    public void setStan(String stan) {
        this.stan = stan;
    }
}
