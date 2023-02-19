package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pinball.Engine.CirclePhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class ReductionCircle extends CirclePhysicsObject {
    /*public ReductionCircle(Vector2D position, int r, Bitmap bitmap) {
        super(position, r, bitmap);
    }
    public ReductionCircle(Vector2D position, int r, Bitmap bitmap, boolean move) {
        super(position, r, bitmap, move);
    }*/
    public ReductionCircle(Vector2D position, int r, int id, Resources resources) {
        super(position, r, resources);
    }
    public ReductionCircle(Vector2D position, int r, int id, Resources resources, boolean move) {
        super(position, r, id, resources, move);
    }

//    public void gameCollided(PhysicsObject other){
//        super.gameCollided(other);
//        other.getVelocity().reSize(-10);
//        Log.d("d", other.getClass().getName());
//    }
}
