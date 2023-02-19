package com.example.pinball.GameObjectCodes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.pinball.GameCharacter.CharacterMagician;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.RectanglePhysicsObject;
import com.example.pinball.Engine.Vector2D;

public class MagicianBlock extends RectanglePhysicsObject implements CharacterObj{

    CharacterMagician user;

   /* public MagicianBlock(Vector2D position, double width, double height, Bitmap bitmap) {
        super(position, width, height, bitmap);
    }*/
   public MagicianBlock(Vector2D position, double width, double height, int id, Resources resources, boolean move) {
       super(position, width, height, id, resources, move);
   }
   public void setRotation(double r){
       super.setRotation(r);
   }
   public void skill(){
   }
   public void skill(PhysicsObject other){
       int x = 50;
       if(other.getMaterialPoint().getX() > 720){
           other.setMaterialPoint(new Vector2D(x, other.getMaterialPoint().getY()-150));
       }
       else
           other.setMaterialPoint(new Vector2D(1440-x, other.getMaterialPoint().getY()-150));
   }

    @Override
    public void setUser(GameCharacter u) {
        user = (CharacterMagician) u;
    }

    public void gameCollided(PhysicsObject other){
        super.gameCollided(other);
        skill(other);
    }
}
