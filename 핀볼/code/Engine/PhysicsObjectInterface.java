package com.example.pinball.Engine;

import android.graphics.Canvas;



public interface PhysicsObjectInterface {
    boolean collisionCheck(PhysicsObject other);
    void addGravitation(Vector2D gravity);
    void act();
    void paint(Canvas c);
    double getRadius();
    Vector2D getMaterialPoint();
}
