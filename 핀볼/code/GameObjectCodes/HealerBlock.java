package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.pinball.GameCharacter.CharacterPriest;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.RectanglePhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class HealerBlock extends RectanglePhysicsObject implements CharacterObj {
    /*public HealerBlock(Vector2D position, double width, double height, Bitmap bitmap) {
        super(position, width, height, bitmap);
    }
    public HealerBlock(Vector2D position, double width, double height, Bitmap bitmap, boolean move) {
        super(position, width, height, bitmap, move);
    }*/

    int healCount = 0;
    CharacterPriest user;

    public HealerBlock(Vector2D position, double width, double height, int id, Resources resources) {
        super(position, width, height, id, resources);
    }
    public HealerBlock(Vector2D position, double width, double height, int id, Resources resources, boolean move) {
        super(position, width, height, id, resources, move);
    }

    public void setRotation(double r){
        super.setRotation(r);
    }

    public void gameCollided(PhysicsObject other){
        super.gameCollided(other);
        skill();
    }

    @Override
    public void skill() {
        healCount++;
        if(healCount >= 5){
            user.addLife();
            healCount = 0;
            Log.d("deb", "skill");
        }
    }

    @Override
    public void setUser(GameCharacter u) {
        user = (CharacterPriest)u;
    }
}
