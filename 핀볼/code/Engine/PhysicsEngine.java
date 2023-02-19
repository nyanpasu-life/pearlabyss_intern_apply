package com.example.pinball.Engine;

public class PhysicsEngine extends Thread implements CustomThread {
    protected boolean Run = false;
    protected GameData Data;
    protected static Vector2D gravity = new Vector2D(0, 0.1);

    protected PhysicsEngine(GameData gameData) {
        Data = gameData;
    }

    @Override
    public void run() {
        while (Run) {
            objCalculate();
            try {
                Thread.sleep(10);

            } catch (Exception e) {
            }
        }
    }

    // 두 오브젝트가 충돌하면 두 오브젝트의 행동함수를 호출하는 함수


    //쓰레드가 작동하는지에 관한 논리값을 입력받는다.
    public void setRunning(boolean b) {
        Run = b;
    }

    protected void objCalculate(){
        synchronized (Data){
            for (PhysicsObject e : Data.getGameObjectsList()) {
                e.everyTick();
            }

            for (PhysicsObject e : Data.getMovableList()) {
                for (PhysicsObject other : Data.getGameObjectsList()) { //그리드가 추가되면 수정되어야하는 부분. 현재는 모든 오브젝트에 대해 충돌 검사를 하도록 작성되어있음.
                    if (e != other & e.Collided == false) {
                        if (e.collisionCheck(other)) {
                            e.gameCollided(other);
                            other.gameCollided(e);
                        }
                    }
                }
                if (e.Collided == false) {
                    if(e.getMaterialPoint().getY() > (2560/2))
                        e.addGravitation(gravity); //본래는 바닥면과 충돌할때만 수직항력에 의해 중력이 없어지지만, 프로그래밍의 편의성을 위하여 충돌된 물체는 그 계산 차례에 중력은 고려하지 않음.
                    else
                        e.addGravitation(gravity.inverse());
                }
                e.act();
            }
        }
    }

    public class Grid {

    }


}