package com.example.pinball.Engine.Network;


import android.os.Handler;
import android.util.Log;

import com.example.pinball.Engine.CustomThread;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.Vector2D;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class NetListenEngine extends Thread implements CustomThread {
    Handler PushHandler;
    boolean Run = false;

    NetPhysicsView NetView;
    NetInputSet Inputs;
    ObjectInputStream Reader;


    NetListenEngine(NetPhysicsView netView, ObjectInputStream reader, NetInputSet netInputSet,  Handler pushHandler){
        setInstanceVar(netView, reader, netInputSet, pushHandler);

    }

    private void setInstanceVar(NetPhysicsView netView, ObjectInputStream reader, NetInputSet netInputSet, Handler pushHandler) {
        NetView = netView;
        Inputs = netInputSet;
        PushHandler = pushHandler;
        Reader = reader;
    }

    public void setRunning(boolean run) {
        Run = run;
    }

    @Override
    public void run() {
        while (Run){
            listen();
        }
    }


    void listen() {
        LinkedHashMap listenData = getListenData();
        if (listenData!=null){
                LinkedHashMap afterTrickSync = ballSyncTrick(listenData);
                stackToPaced(afterTrickSync);
        }
    }

    private LinkedHashMap ballSyncTrick(LinkedHashMap listenData) {
        try {
            double ball1Pos_x = (double) listenData.get("B1_Pos_X");
            listenData.remove("B1_Pos_X");
            double ball1Pos_y = (double) listenData.get("B1_Pos_Y");
            listenData.remove("B1_Pos_Y");
            double ball1Vel_x = (double) listenData.get("B1_Vel_X");
            listenData.remove("B1_Vel_X");
            double ball1Vel_y = (double) listenData.get("B1_Vel_Y");
            listenData.remove("B1_Vel_Y");
            double ball2Pos_x = (double) listenData.get("B2_Pos_X");
            listenData.remove("B2_Pos_X");
            double ball2Pos_y = (double) listenData.get("B2_Pos_Y");
            listenData.remove("B2_Pos_Y");
            double ball2Vel_x = (double) listenData.get("B2_Vel_X");
            listenData.remove("B2_Vel_X");//TODO 동기화 임시제작
            double ball2Vel_y = (double) listenData.get("B2_Vel_Y");
            listenData.remove("B2_Vel_Y");
            ArrayList<PhysicsObject> tempList = NetView.Data.getMovableList();
            tempList.get(0).setMaterialPoint(new Vector2D(1440.0d -ball1Pos_x,2560.0d - ball1Pos_y));
            tempList.get(0).setVelocity(new Vector2D(ball1Vel_x,ball1Vel_y).rotate(180));
            tempList.get(1).setMaterialPoint(new Vector2D(1440.0d -ball2Pos_x,2560.0d - ball2Pos_y));
            tempList.get(1).setVelocity(new Vector2D(ball2Vel_x,ball2Vel_y).rotate(180));
        }catch (NullPointerException e){e.printStackTrace();Log.w("공 동기화", "데이터 동기화 실패");}
        return listenData;
    }

    private void stackToPaced(LinkedHashMap listenData) {
        Inputs.PacedInput = listenData;
        Log.d("클라이언트", "데이터 수신");
    }

    protected LinkedHashMap getListenData() {
        LinkedHashMap listenData =null;
        try {
            listenData =(LinkedHashMap) Reader.readObject();
        } catch (ClassNotFoundException e) {} catch (IOException e) {} catch (NullPointerException e) {}
        return listenData;
    }

}
