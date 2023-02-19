package com.example.pinball.Engine.Network;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.pinball.Engine.DrawEngine;
import com.example.pinball.Engine.PhysicsView;
import com.example.pinball.GameCharacter.GameCharacter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NetPhysicsView extends PhysicsView {
    NetPushEngine PushThread;
    NetListenEngine ListenTread;
    GameCharacter MyCharacter =null;
    GameCharacter EnemyCharacter =null;
    Socket sock =null;

    public NetInputSet getNetInputSet() {
        return netInputSet;
    }

    NetInputSet netInputSet;

    public NetPhysicsView(Context context){
        super(context);
    }
    public NetPhysicsView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void runThreads(boolean isPacemaker, ObjectOutputStream writer,ObjectInputStream reader, ArrayList<GameCharacter> gameChars){
        if(isPacemaker){
        }

        netInputSet = new NetInputSet();

        startPushTread(writer, netInputSet);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startDrawThread();
        startListenThread(isPacemaker, reader, netInputSet, PushThread.PushHandler);
        startPhysicsThread(isPacemaker, netInputSet, gameChars, PushThread.PushHandler);
    }

    private void startPhysicsThread(boolean isPaceMaker, NetInputSet netInputSet, ArrayList<GameCharacter> gameChars, Handler pushHandler) {

        PhysicsThread  = new NetPhysicsEngine(isPaceMaker, Data, netInputSet, gameChars, pushHandler);
        PhysicsThread.setRunning(true);
        PhysicsThread.start();
    }
    private void startDrawThread() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        DrawThread = new DrawEngine(holder, Data);
        DrawThread.setRunning(true);
        DrawThread.start();
    }
    private void startListenThread(boolean isPacemaker, ObjectInputStream reader, NetInputSet netInputSet, Handler pushHandler) {
        if (isPacemaker == true) {
            ListenTread = new NetListenPaceEngine( this, reader, netInputSet,pushHandler);
        } else { //isServer == false
            ListenTread = new NetListenEngine(this, reader, netInputSet,pushHandler);
        }
        ListenTread.setRunning(true);
        ListenTread.start();
    }
    private void startPushTread(ObjectOutputStream writer, NetInputSet netInputSet) {
        PushThread = new NetPushEngine(this, writer, netInputSet);
        PushThread.setRunning(true);
        PushThread.start();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
       super.surfaceDestroyed(holder);
        closeTread(PushThread);
        closeTread(ListenTread);
    }

    public NetPhysicsEngine getNetPhysicsEngine() {
        return (NetPhysicsEngine) PhysicsThread;
    }

    long getFrame(){
        return getNetPhysicsEngine().FrameCount;
    }
}
