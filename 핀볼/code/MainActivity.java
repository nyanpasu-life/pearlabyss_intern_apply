/*
package com.example.pinball;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.pinball.GameCharacter.TestCharacter;
import com.example.pinball.GameObjectCodes.Flipper;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.PhysicsView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Flipper flipper1;
    Flipper flipper2;

    private int screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // get screen size(pixel)
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGameInside();
    }

    void setGameInside() {
        PhysicsView gameView = findViewById(R.id.game_inside);

        ArrayList<PhysicsObject> movePack = new ArrayList<>();
        ArrayList<PhysicsObject> pack = new ArrayList<>();

        Bitmap rect = BitmapFactory.decodeResource(getResources(), R.drawable.frect);
        Bitmap circle = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        Bitmap floorRect = BitmapFactory.decodeResource(getResources(), R.drawable.floor);

//        flipper1 = new Flipper(new Vector2D(200,1500),0,900,80,floorRect);
//        flipper2 = new Flipper(new Vector2D(1200,1500),1,900,80,floorRect);
//        pack.add(flipper1);
//        pack.add(flipper2);

//        gameView.setGameObjectsList(pack);
//        gameView.setMovableList(movePack);
//        gameView.getDrawEngine().setGameObjectsList(pack);
//
//        gameView.getPhysicsEngine().setGameObjectsList(pack);
//        gameView.getPhysicsEngine().setGameMovableObjectsList(movePack);

//        TestCharacter hi = new TestCharacter(getResources());
//        hi.setCharOnView(0,gameView);
//    }
*/

