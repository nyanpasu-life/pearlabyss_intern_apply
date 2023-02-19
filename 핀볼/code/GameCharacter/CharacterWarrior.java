package com.example.pinball.GameCharacter;

import android.content.res.Resources;

import com.example.pinball.GameObjectCodes.AccelerationCircle;
import com.example.pinball.GameObjectCodes.Ball;
import com.example.pinball.GameObjectCodes.DeadLine;
import com.example.pinball.GameObjectCodes.DefaultBlock;
import com.example.pinball.GameObjectCodes.Flipper;
import com.example.pinball.GameObjectCodes.ReductionBlock;
import com.example.pinball.GameObjectCodes.ReductionCircle;
import com.example.pinball.GameObjectCodes.WarriorBlock;
import com.example.pinball.Engine.Vector2D;
import com.example.pinball.R;

import java.util.Random;

public class CharacterWarrior extends GameCharacter {

    private int life = 5;
    private Random xgen = new Random();

    final static String LeftFlipCode = "LeftF_4";
    final static String RightFlipCode ="RightF_4";

    public CharacterWarrior(Resources res) {
        super(res);
        xgen.setSeed(1000);

        int bDefault = R.drawable.block_default;      //  f: fixed
//        Bitmap fCir = BitmapFactory.decodeResource(MResource, R.drawable.circle);
        int bWarrior = R.drawable.block_warrior;
        int bReduction = R.drawable.block_reduction;
        int bRefraction = R.drawable.block_refraction;
        int cReduction = R.drawable.circle_reduction;
        int cAccel = R.drawable.circle_acceleration;
        int cBall = R.drawable.ball;

        int leftFlipper = R.drawable.flipper_left;
        int rightFlipper = R.drawable.flipper_right;

       /* PolygonPhysicsObject rect1 = new DefaultBlock(new Vector2D(500, 300), 50, 150, fRect);

        PolygonPhysicsObject rect2 = new DefaultBlock(new Vector2D(1000, 900), 200, 100, fRect);

        CirclePhysicsObject circle1 = new CirclePhysicsObject(new Vector2D(550, 300), 25, fCir);
        CirclePhysicsObject circle2 = new CirclePhysicsObject(new Vector2D(550, 1000), 100, fCir);
        CirclePhysicsObject circle3 = new CirclePhysicsObject(new Vector2D(300, 700), 100, fCir);*/

        Ball ball = new Ball(new Vector2D(xgen.nextInt(1240)+100,1290), 48, cBall, MResource);

        flipper1 = new Flipper(new Vector2D(540, 2350), 0 , 250, 75, leftFlipper, MResource);
        flipper2 = new Flipper(new Vector2D(1440-540, 2350), 1 , 250, 75, rightFlipper, MResource);

        DefaultBlock obj1 = new DefaultBlock(new Vector2D(0-50,2400), 800,800, bDefault, MResource, false);
        obj1.setRotation(40);
        DefaultBlock obj2 = new DefaultBlock(new Vector2D(350,1950), 400,100, bDefault, MResource, false);
        obj2.setRotation(40);
        DefaultBlock obj3 = new DefaultBlock(new Vector2D(1440-400+50,2235), 400,100, bDefault, MResource, false);
        obj3.setRotation(-40);
        DefaultBlock obj4 = new DefaultBlock(new Vector2D(1480,1985), 200,200, bDefault, MResource, false);
        obj4.setRotation(-40);
        DefaultBlock obj5 = new DefaultBlock(new Vector2D(1480-50,1985 + 800), 800,800, bDefault, MResource, false);
        obj5.setRotation(-40);

//        RefractionBlock obj6 = new RefractionBlock(new Vector2D(100,1450), 200,50,bRefraction, MResource, false);
//        RefractionBlock obj7 = new RefractionBlock(new Vector2D(1440-150,1600), 300,50,bRefraction, MResource, false);

        AccelerationCircle obj8 = new AccelerationCircle(new Vector2D(550,1280+130), 100,cAccel, MResource, false);
        AccelerationCircle obj9 = new AccelerationCircle(new Vector2D(1440-200,1280+130), 75,cAccel, MResource, false);
        AccelerationCircle obj10 = new AccelerationCircle(new Vector2D(300,1280+550), 60,cAccel, MResource,false);

        ReductionCircle obj11 = new ReductionCircle(new Vector2D(1440 - 500,1500), 50,cReduction, MResource, false);
        ReductionBlock obj12 = new ReductionBlock(new Vector2D(1440 - 375,1900), 300,75,bReduction, MResource, false);
        obj12.setRotation(-40);

        WarriorBlock obj13 = new WarriorBlock(new Vector2D(355, 2290), 300, 100, bWarrior, MResource, false);
        obj13.setRotation(40);

        DeadLine deadLine = new DeadLine(new Vector2D(750, -200), 1500, 150, bDefault, MResource, false);
        DefaultBlock outColliderLeft = new DefaultBlock(new Vector2D(0-75, 1700), 150, 1186, bDefault, MResource, false);
        DefaultBlock outColliderRight = new DefaultBlock(new Vector2D(1440+75, 1700), 150, 1186, bDefault, MResource,  false);
//        PolygonPhysicsObject floor4 = new DefaultBlock(new Vector2D(750, -100), 1500, 200, fRect, false);

        GameObjectList.add(ball);
//        GameObjectList.add(rect1);
//        GameObjectList.add(rect2);
//        GameObjectList.add(circle1);
//        GameObjectList.add(circle2);
//        GameObjectList.add(circle3);
        GameObjectList.add(obj1);
        GameObjectList.add(obj2);
        GameObjectList.add(obj3);
        GameObjectList.add(obj4);
        GameObjectList.add(obj5);
//        GameObjectList.add(obj6);
//        GameObjectList.add(obj7);
        GameObjectList.add(obj8);
        GameObjectList.add(obj9);
        GameObjectList.add(obj10);
        GameObjectList.add(obj11);
        GameObjectList.add(obj12);
        GameObjectList.add(obj13);
        GameObjectList.add(outColliderLeft);
        GameObjectList.add(outColliderRight);
        GameObjectList.add(deadLine);
        GameObjectList.add(flipper1);
        GameObjectList.add(flipper2);
//        GameObjectList.add(floor4);

        interactWithUser.add(flipper1);
        interactWithUser.add(flipper2);
        interactWithUser.add(obj13);
        interactWithUser.add(null);
        interactWithUser.add(deadLine);
}

    public int getLife(){
        return life;
    }
    public void loseLife(){
        life--;
        playView.updateLife(life, this);
    }

    public void charAct(String key) {
        if(key.equals(LeftFlipCode)){
            flipper1.powered();
        }
        if(key.equals(RightFlipCode)){
            flipper2.powered();
        }
    }

    @Override
    public String getLeftFlipCode() {
        return LeftFlipCode;
    }

    @Override
    public String getRightFlipCode() {
        return RightFlipCode;
    }
}
