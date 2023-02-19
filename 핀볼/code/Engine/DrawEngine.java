package com.example.pinball.Engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

//DrawEngine 은 SurfaceView 와 연동된 SurfaceHolder 객체를 받아와 화면에 게임 상태를 출력한다.
public class DrawEngine extends Thread implements CustomThread {
    private boolean Run = false; //Run == true 여야 그리는 행동이 작동함.
    private SurfaceHolder MSurfaceHolder; // 이 객체를 이용해 SurefaceView 에 그림을 그릴 수 있음.
    GameData Data;

    //생성자 : SurfaceHolder 를 받아와 자신의 인스턴스 변수로 저장한다.
    public DrawEngine(SurfaceHolder holder, GameData data) {
        MSurfaceHolder = holder;
        Data = data;
    }

    //run 함수. 쓰레드가 종료되기 전까지 SurfaceView 에 지속적으로 그림을 그린다.
    @Override
    public void run() {
        int j = 0;
        while (Run) {
            synchronized (MSurfaceHolder) {
                Canvas c = null;
                c = MSurfaceHolder.lockCanvas(null);
                try {
                    c.drawColor(Color.WHITE);
                    for (PhysicsObject e : Data.getGameObjectsList()) {
                        e.paint(c);
                    }
                }
                catch (NullPointerException e){e.printStackTrace();}
                finally {
                    if (c != null) {
                        MSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    //쓰레드가 작동하는지에 관한 논리값을 입력받는다.
    public void setRunning(boolean b) {
        Run = b;
    }

    /*public void convertDraw(double width, double height){
        WidthRate = width / 720;
        HeightRate = height / 1280;
        for(PhysicsObjectInterface e : GameObjectsList){
            Bitmap tempB = e.getBitmap();
            int scaledWidth = (int) Math.round(tempB.getWidth()*WidthRate);
            int scaledHeight = (int) Math.round(tempB.getHeight()*HeightRate);
            //tempB = Bitmap.createScaledBitmap(tempB, scaledWidth, scaledHeight,true);     // TODO: 이미지 전체 비율에 문제가 있습니다. 일단 주석처리 해두겠습니다.
            e.setBitmap(tempB);
        }
    }*/
}