package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pinball.GameCharacter.CharacterArcher;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.Engine.CirclePhysicsObject;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class ArcherCircle extends CirclePhysicsObject implements CharacterObj{

    CharacterArcher user;

    /*public ArcherCircle(Vector2D position, int r, Bitmap bitmap) {
        super(position, r, bitmap);
    }*/

    public ArcherCircle(Vector2D position, int r, int id, Resources resources) {
        super(position, r, id, resources);
    }

    public void act (){
        super.act();
        skill();
    }

    public void skill(){

    }
    public void skill(PhysicsObject other) {
        if (other instanceof Ball) {
            Ball bOther = (Ball) other;
            bOther.resize(bOther.getRadius() * 0.8);
        }
    }

    @Override
    public void setUser(GameCharacter u) {
        user = (CharacterArcher) u;
    }

    public void gameCollided(PhysicsObject other){
        super.gameCollided(other);
        skill(other);
    }
}
