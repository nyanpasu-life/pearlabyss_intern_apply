package com.example.pinball.Engine.Network;

import android.os.Handler;
import android.util.Log;

import com.example.pinball.Engine.GameData;
import com.example.pinball.Engine.PhysicsEngine;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.Vector2D;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.GameObjectCodes.Ball;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import static com.example.pinball.Engine.Network.NetPushEngine.PUSH_PACED_INPUT;

public class NetPhysicsEngine extends PhysicsEngine {
    final static int LATENCY = 20;
    boolean IsPaceMaker;

    int FrameRate = 120;
    Long RateRealTime;
    int FrameCount = 0;

    boolean IsPaused = false;

    NetInputSet Inputs;

    Handler PushHandler;

    ArrayList<GameCharacter> GameChars;

    public NetPhysicsEngine(boolean isPaceMaker, GameData gameData, NetInputSet netInputs, ArrayList<GameCharacter> gameChars, Handler pushHandler) {
        super(gameData);
        Inputs = netInputs;
        makeMilSecRate();
        GameChars = gameChars;
        PushHandler = pushHandler;
        IsPaceMaker = isPaceMaker;
        BallSyncTrick();
    }

    private void BallSyncTrick() {
        if (!isPaceMaker()) {
            PhysicsObject ball2 = (Ball) Data.getMovableList().get(0);
            PhysicsObject ball1 = (Ball) Data.getMovableList().get(1);
            ArrayList<PhysicsObject> tempList = new ArrayList<>();
            tempList.add(ball1);
            tempList.add(ball2);
            Data.setMovableList(tempList);
        }
    }

    private void makeMilSecRate() {
        double framePerS = (1.0d / FrameRate);
        RateRealTime = Math.round(1000 * (framePerS));
    }

    public void run() {
        makeDummyInputs();


        while (Run) {
            if (checkPaceComplete() && isPaceMaker() == true) {
                synchronized (Inputs) {
                    putPacedData();
                    Inputs.PacedInput = Inputs.IngInput;
                    PushHandler.sendEmptyMessage(PUSH_PACED_INPUT);//TODO 추후 스택으로 대체
                }
            }
            synchronized (Data) {
                if (IsPaused == false && Inputs.PacedInput != null) {
                    long startTime = new Date().getTime();
                    objCalculate();
                    pause(startTime);
                    synchronized (Inputs) {
                        pace();
                    }

                    FrameCount++;
                }
            }
        }
    }


    private boolean checkPaceComplete() {
        int checkmy = 1;
        int checkother = 1;
        try {
            checkmy = ((int) Inputs.IngInput.get("PE"));
            checkother = ((int) Inputs.IngInput.get("NPE"));
        } catch (NullPointerException e) {
        }
        if (checkmy + checkother == -3 * 2) {
            return true;
        }
        return false;
    }

    public void putPacedData() {
        Inputs.IngInput.put("Paced", 0);

        Inputs.IngInput.put("B1_Pos_X", Data.getMovableList().get(0).getMaterialPoint().getX());
        Inputs.IngInput.put("B1_Pos_Y", Data.getMovableList().get(0).getMaterialPoint().getY());
        Inputs.IngInput.put("B1_Vel_X", Data.getMovableList().get(0).getVelocity().getX());
        Inputs.IngInput.put("B1_Vel_Y", Data.getMovableList().get(0).getVelocity().getY());
        Inputs.IngInput.put("B2_Pos_X", Data.getMovableList().get(1).getMaterialPoint().getX());
        Inputs.IngInput.put("B2_Pos_Y", Data.getMovableList().get(1).getMaterialPoint().getY());
        Inputs.IngInput.put("B2_Vel_X", Data.getMovableList().get(1).getVelocity().getX());
        Inputs.IngInput.put("B2_Vel_Y", Data.getMovableList().get(1).getVelocity().getY());//TODO 동기화 임시제작
    }

    private void makeDummyInputs() {
        Inputs.PacedInput.put("Frame", FrameCount); //TODO  메세지가 전달이 안된다.
        putPacedData();
    }

    private void pace() {
        int pacedframe = -1;
        try {
            pacedframe = ((int) Inputs.getPacedInput().get("Frame"));
            Log.d("피직스엔진" + isPaceMaker(), "현재프레임" + FrameCount + "동기화 지시프레임" + pacedframe);
        } catch (NullPointerException e) {
        }
        if (pacedframe == FrameCount) {
            doPacedInput();
            if (isPaceMaker()) {
                selfPacingInput();
            } else { //페이스 메이커가 아니면
                shotPacingInput();
            }
        }
    }

    private boolean isPaceMaker() {
        return IsPaceMaker;
    }

    private void shotPacingInput() {
        Inputs.LocalInput.put("Frame", FrameCount + LATENCY);
        PushHandler.sendEmptyMessage(NetPushEngine.PUSH_MY_INPUT);
    }

    private void selfPacingInput() {
        Inputs.LocalInput.put("Frame", FrameCount + LATENCY);
        LinkedHashMap data = Inputs.takeOutLocal();
        data.put("PE", -3);
        Inputs.mergePacing(data);
    }

    private void doPacedInput() {
        LinkedHashMap order = Inputs.takeOutPaced_nullBefore();
        Iterator<String> iter = order.keySet().iterator();
        while (iter.hasNext()) {
            String key = ((String) iter.next());
            Object value = order.get(key);
            checkOrder(key, value);
        }
    }

    private void checkOrder(String key, Object value) {
        checkCharAct(key, value);
        checkPaused(key, value);
    }

    private void checkCharAct(String key, Object value) {
        if (value instanceof String) {
            if (value.equals(Data.getMyIp())) {
                GameChars.get(1).charAct(key);
            } else if (value.equals(Data.getOtherIP())) {
                GameChars.get(0).charAct(key);
            }
        }
    }

    private void checkPaused(String key, Object value) {
        if (key.equals("cPause")) {
            if (value.equals(-10)) {
                IsPaused = true;
            } else if (value.equals(-11)) {
                IsPaused = false;
            }
        }
    }

    private void pause(long startTime) {
        long endTime = new Date().getTime();
        long progressedTime = endTime - startTime;
        long waitTime = RateRealTime - progressedTime;
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }


}
