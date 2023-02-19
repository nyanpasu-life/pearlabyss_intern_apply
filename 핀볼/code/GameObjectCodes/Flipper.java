package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;

import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.RectanglePhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class Flipper extends RectanglePhysicsObject {
    final static int COUNTER_CLOCK = 0;
    final static int CLOCK = 1;
    final static double POWER = 20000;
    final static int ROTATE_SPEED = 2;
    final static int ROTATE_ANGLE = 60;
    final static double PivotDistanceRate = 0.8;


    boolean PoweredBool = false;
    boolean Falling = false;

    int PivotStatus; //0 반시계 회전 (회전 음수변화) , 1 시계회전 (회전 양수변화)
    Vector2D PivotPoint; //회전 점

    public Flipper(Vector2D position, int pivotStatus, double width, double height, int id, Resources resources) {
        super(position, width, height, id, resources, false);
        PivotStatus = pivotStatus;
        makePivotPoint();
        setOriginMPoint(getMaterialPoint());
    }

    public void makePivotPoint() {
        if(PivotStatus ==0){
            leftPivotPoint();
        }
        else { //PivotStatus ==1
           rightPivotPoint();
        }
    }

    public void leftPivotPoint() {
        double width = getBitmap().getWidth();
        double pMove;
        pMove =  -width/2 *PivotDistanceRate;
        PivotPoint = getMaterialPoint().plus(new Vector2D(pMove,0));

    }
    public void rightPivotPoint() {
        double width = getBitmap().getWidth();
        double pMove;
        pMove =  width/2 *PivotDistanceRate;
        PivotPoint = getMaterialPoint().plus(new Vector2D(pMove,0));
    }

    public void powered() {
        if (! Falling) {
            PoweredBool = true;

        }
    }

    public void inversePivotStatus(){
        if(PivotStatus ==1){
            PivotStatus =0;
        }
        if(PivotStatus ==0){
            PivotStatus =1;
        }
    }

    @Override
    public void everyTick() {
        rotateWhenPowered();
    }

    private void rotateWhenPowered() {
        if (PoweredBool) {
            double nowR = getRotation();
            if (nowR > ROTATE_ANGLE || nowR < -ROTATE_ANGLE) {
                PoweredBool = false;
                Falling = true;
                return;
            }
            if (PivotStatus == 0) {
                nowR -= ROTATE_SPEED * 3;
            } else if (PivotStatus == 1) {
                nowR += ROTATE_SPEED * 3;
            }
            rotateByPivot(nowR, PivotPoint);
        }
        if(Falling) {
            double nowR = getRotation();
            if (PivotStatus == 0 && nowR > 0) {
                Falling = false;
                nowR = 0;
                return;
            }
            if (PivotStatus == 1 && (nowR < 0)) {
                Falling = false;
                nowR = 0;
                return;
            }
            if (PivotStatus == 0) {
                nowR += ROTATE_SPEED;
            } else { //PivotStatus ==1;
                nowR -= ROTATE_SPEED;
            }
            rotateByPivot(nowR, PivotPoint);
        }
    }

    @Override
    public void gameCollided(PhysicsObject other) {
        if(PoweredBool){
            double angle = getRotation();
            Vector2D n = new Vector2D(0,-1).rotate(angle);
            Vector2D powerVector = n.constantProduct(POWER).constantProduct(other.getInverseOfMass());
            Vector2D newVelocity = other.getVelocity().plus(powerVector);
            other.setVelocity(newVelocity);
        }

    }
    public void refreshOriginPoint(){
        setOriginMPoint(getMaterialPoint());
    }


}