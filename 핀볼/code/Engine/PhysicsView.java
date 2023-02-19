package com.example.pinball.Engine;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.pinball.GameObjectCodes.Ball;
import com.example.pinball.GameObjectCodes.CharacterObj;
import com.example.pinball.GameObjectCodes.Flipper;

import java.util.ArrayList;

public class PhysicsView extends SurfaceView implements SurfaceHolder.Callback{

    boolean Run = false;
    protected DrawEngine DrawThread;
    public PhysicsEngine PhysicsThread;
    public GameData Data = new GameData();

    public PhysicsView(Context context){
        super(context);
    }
    public PhysicsView(Context context, AttributeSet attr){
        super(context, attr);
    }

    public void startThreads(){
        if(DrawThread ==null) {
            PhysicsThread = new PhysicsEngine(Data);
            PhysicsThread.setRunning(true);
            PhysicsThread.start();

            SurfaceHolder holder = getHolder();
            holder.addCallback(this);
            DrawThread = new DrawEngine(holder, Data);
            DrawThread.setRunning(true);
            DrawThread.start();
        }
    }

    public DrawEngine getDrawEngine(){
        return DrawThread;
    }

    public PhysicsEngine getPhysicsEngine(){
        return PhysicsThread;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        closeTread(DrawThread);
        closeTread(PhysicsThread);
    }

    protected void closeTread(CustomThread thread) {
        thread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                ((Thread)thread).join();
                retry = false;
            } catch (InterruptedException e) { }
        }
    }

    public void pushObjects(ArrayList<PhysicsObject> list) {
        synchronized (Data) {
            for (PhysicsObject e : list) {
                if (e.MovingObject == true) {
                    Data.getGameObjectsList().add(e);
                    Data.getMovableList().add(e);
                } else if (e.MovingObject == false) {
                    Data.getGameObjectsList().add(e);
                }
                if (e instanceof Ball) {
                    Log.d("공의 위치", "" + e.getMaterialPoint().getX() + "//" + e.getMaterialPoint().getY());
                }
            }
        }
    }

    public void setFlipper(Flipper flipperL, Flipper flipperR){}

    public void setCharacterObject(CharacterObj physicsObject, CharacterObj physicsObject1, CharacterObj deadLine) {
    }

}
