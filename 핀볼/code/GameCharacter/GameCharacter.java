package com.example.pinball.GameCharacter;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.example.pinball.GameObjectCodes.CharacterObj;
import com.example.pinball.GameObjectCodes.Flipper;
import com.example.pinball.GameView;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.PhysicsView;
import com.example.pinball.Engine.Vector2D;

import java.util.ArrayList;

/** 캐릭터가 공통으로 갖는 속성 정의 **/

public abstract class GameCharacter {

    private int life;

    ArrayList<PhysicsObject> GameObjectList = new ArrayList<>();
    Resources MResource;
    ArrayList<PhysicsObject> interactWithUser = new ArrayList<>(); // 0: Flipper left, 1: Flipper Right, 2: character block

    GameView playView;

    GameCharacter(Resources res){
        MResource = res;
    }

    Flipper flipper1;
    Flipper flipper2;

    public void skill(){}
    public int getLife(){
        return life;
    }
    public void loseLife(){
        life--;
    }

    private ArrayList<PhysicsObject> getOtherObjectList(){
        ArrayList<PhysicsObject> otherObjects = new ArrayList<>();
        for(PhysicsObject obj : GameObjectList){
            double tmpx = obj.getMaterialPoint().getX();
            double tmpy = obj.getMaterialPoint().getY();
            tmpx = 1440 - tmpx;
            tmpy = 2560 - tmpy;
            Vector2D tmpVector = new Vector2D(tmpx, tmpy);
            obj.setMaterialPoint(tmpVector);
            otherObjects.add(obj);
        }
//        Flipper tmp = flippers.get(0);
//        tmp.setRotation(tmp.getRotation() + 180);
//        tmp = flippers.get(1);
//        tmp.setRotation(tmp.getRotation() + 180);
        return otherObjects;
    }

    public void setCharOnView(int position, PhysicsView view){
        playView = (GameView)view;
        if(position == 0) {
            view.pushObjects(getOtherObjectList());
            view.setCharacterObject((CharacterObj)interactWithUser.get(2), (CharacterObj)interactWithUser.get(3), (CharacterObj)interactWithUser.get(4));
            changeFlipper();
        }
        else if(position == 1){
            view.pushObjects(GameObjectList);
            view.setFlipper((Flipper)interactWithUser.get(0), (Flipper)interactWithUser.get(1));
            view.setCharacterObject((CharacterObj)interactWithUser.get(2), (CharacterObj)interactWithUser.get(3), (CharacterObj)interactWithUser.get(4));
        }
    }

    protected void changeFlipper(){
        Bitmap f1BitMap = flipper1.getBitmap();
        Bitmap f2BitMap = flipper2.getBitmap();
        flipper1.setBitmap(f2BitMap);
        flipper2.setBitmap(f1BitMap);

        flipper2.refreshOriginPoint();
        flipper2.leftPivotPoint();

        flipper1.refreshOriginPoint();
        flipper1.rightPivotPoint();
    }

    public ArrayList<PhysicsObject> getInteractWithUsers(){
        return interactWithUser;
    }

    public abstract void charAct(String key);

    public abstract String getLeftFlipCode();

    public abstract String getRightFlipCode();
}
