package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pinball.Engine.RectanglePhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class DefaultBlock extends RectanglePhysicsObject {
    /*public DefaultBlock(Vector2D position, double width, double height, Bitmap bitmap) {
        super(position, width, height, bitmap);
    }
    public DefaultBlock(Vector2D position, double width, double height, Bitmap bitmap, boolean move) {
        super(position, width, height, bitmap, move);
    }*/

    public DefaultBlock(Vector2D position, double width, double height, int id, Resources resources) {
        super(position, width, height, id, resources);
    }
    public DefaultBlock(Vector2D position, double width, double height, int id, Resources resources, boolean move) {
        super(position, width, height, id, resources, move);
    }

    public void setRotation(double r){
        super.setRotation(r);
    }
}
