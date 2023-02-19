package com.example.pinball;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.example.pinball.Engine.Network.NetPhysicsView;
import com.example.pinball.GameCharacter.CharacterArcher;
import com.example.pinball.GameCharacter.CharacterMagician;
import com.example.pinball.GameCharacter.CharacterPriest;
import com.example.pinball.GameCharacter.CharacterWarrior;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.GameObjectCodes.CharacterObj;
import com.example.pinball.GameObjectCodes.Flipper;
import com.example.pinball.Engine.PhysicsObjectInterface;
import com.example.pinball.Engine.PhysicsView;

import java.util.ArrayList;

/**     GamePlayActivity 위에 올릴 Game 화면    **/

public class GameView extends NetPhysicsView {
    protected GameCharacter player, otherOne;
    ArrayList<PhysicsObjectInterface> gameObjectSet = new ArrayList<>();
    private GamePlayActivity gamePlayActivity;

    int playerCharacterId;
    int otherCharacterId;

    private Flipper left, right;

    private ArrayList<CharacterObj> characterObjs = new ArrayList<>();
    Handler handler;

    public GameView(Context context){
        super(context);
    }

    public GameView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void pushObject(ArrayList<PhysicsObjectInterface> objectSet){
        gameObjectSet = objectSet;
    }

    public void setCharacterId(int playerCharacter, int otherCharacter) {
        playerCharacterId = playerCharacter;
        otherCharacterId = otherCharacter;
        viewSetting();
    }

    private void viewSetting(){
        switch(playerCharacterId){
            case 1:
                player = new CharacterWarrior(getResources());
                break;
            case 2:
                player = new CharacterMagician(getResources());
                break;
            case 3:
                player = new CharacterArcher(getResources());
                break;
            case 4:
                player = new CharacterPriest(getResources());
                break;
            default:
                Log.d("debug", "select error");
                break;
        }
        switch(otherCharacterId){
            case 1:
                otherOne = new CharacterWarrior(getResources());
                break;
            case 2:
                otherOne = new CharacterMagician(getResources());
                break;
            case 3:
                otherOne = new CharacterArcher(getResources());
                break;
            case 4:
                otherOne = new CharacterPriest(getResources());
                break;
            default:
                Log.d("debug", "select error");
        }

        player.setCharOnView(0, this);
        otherOne.setCharOnView(1, this);

        characterObjs.get(0).setUser(player);
        characterObjs.get(2).setUser(player);
        characterObjs.get(3).setUser(otherOne);
        characterObjs.get(5).setUser(otherOne);

        handler = gamePlayActivity.lifeTextHandler;
    }

    public void setFlipper(Flipper flipperL, Flipper flipperR){
        left = flipperL;
        right = flipperR;
    }

    public Flipper getLeftFlipper() {
        return left;
    }

    public Flipper getRightFlipper(){
        return right;
    }

    public void setCharacterObject(CharacterObj a, CharacterObj b, CharacterObj deadLine){
        characterObjs.add(a);
        characterObjs.add(b);
        characterObjs.add(deadLine);
    }

    public void setActivity(GamePlayActivity activity){
        gamePlayActivity = activity;
    }

    public void updateLife(int newLife, GameCharacter character){
//        Message message = handler.obtainMessage(newLife, character);
//        message.sendToTarget();
        Message message = new Message();
        message.arg1 = newLife;
        message.obj = character;
        handler.sendMessage(message);
        Log.d("view", Integer.toString(newLife));
    }
}