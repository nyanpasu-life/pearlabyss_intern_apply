package com.example.pinball.Engine;

import java.util.ArrayList;

public class GameData{
    public void setMovableList(ArrayList<PhysicsObject> movableList) {
        MovableList = movableList;
    }

    private ArrayList<PhysicsObject> MovableList = new ArrayList<>();
    private ArrayList<PhysicsObject> GameObjectsList = new ArrayList<>();

    public String getMyIp() {
        return myIp;
    }

    public void setMyIp(String myIp) {
        this.myIp = myIp;
    }

    public String getOtherIP() {
        return OtherIP;
    }

    public void setOtherIP(String otherIP) {
        OtherIP = otherIP;
    }

    private String myIp;
    private String OtherIP;


    public ArrayList<PhysicsObject> getGameObjectsList() {
        return GameObjectsList;
    }

    public ArrayList<PhysicsObject> getMovableList() {
        return MovableList;
    }

}
