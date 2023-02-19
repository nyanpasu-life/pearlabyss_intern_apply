package com.example.pinball;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.GameObjectCodes.Flipper;

/**     게임플레이 activity. GameView를 Inflate    **/

public class GamePlayActivity_Legacy extends AppCompatActivity {

    int playerCharacter, otherCharacter;
    GameView gv;

    private View 	decorView;
    private int	uiOption;
    Flipper left, right;
    private int playerLife, otherLife;

    private TextView playerLifeText, otherLifeText;
    public Handler lifeTextHandler = new Handler(){
        public void handleMessage(Message message){
            updateLife(message.arg1, (GameCharacter)message.obj);
            Log.d("tag", Integer.toString(message.arg1));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );

        setContentView(R.layout.game_play);
        Log.d("debug", "create success");
        setContentView(R.layout.game_play);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        playerCharacter = bundle.getInt("character");
        //otherCharacter = getOtherCharacter();     //TODO: Network
        otherCharacter = 1;
        gv = findViewById(R.id.game_view);
        //gv.setActivity(this);//TODO: Legacy 이름 충돌

        gv.startThreads();
        gv.setCharacterId(playerCharacter, otherCharacter);

        left = gv.getLeftFlipper();
        right = gv.getRightFlipper();

        playerLifeText = findViewById(R.id.playerLife);
        otherLifeText = findViewById(R.id.otherLife);

        playerLife = gv.player.getLife();
        otherLife = gv.otherOne.getLife();
        playerLifeText.setText("LIFE: " + Integer.toString(playerLife));
        otherLifeText.setText("LIFE: " + Integer.toString(otherLife));

        Button leftButton = findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.powered();
            }
        });

        Button rightButton  = findViewById(R.id.right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right.powered();
            }
        });

    }

/*    public void onLeftClicked(View v){
        left.powered();
    }
    public void onRightClicked(View v){
        right.powered();
    }*/

    public void updateLife(int newLife, GameCharacter character){
        if(character == gv.player)
            playerLifeText.setText("LIFE: " + Integer.toString(newLife));
        else if(character == gv.otherOne)
            otherLifeText.setText("LIFE: " + Integer.toString(newLife));
    }
}
