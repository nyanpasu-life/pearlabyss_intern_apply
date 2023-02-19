package com.example.pinball.Engine;

import android.graphics.Bitmap;

import java.util.ArrayList;

abstract public class PhysicsObject implements PhysicsObjectInterface{
    public boolean Collided = false; //
    boolean MovingObject =true;
    Bitmap Image;

    static public class UtilFunc {

        //인덱스 0,1은 절대좌표계에서 AABB 가 있을때, 한 정점과 가장 가까운 변에 속한 두 점을 리턴함. 변이 동일한 거리면 인덱스순으로 반환.
        //인덱스 2는 점과 변이 수직하는 점, 인덱스 3은 수직교점의 방향벡터.
        public static Vector2D[] getClosedAABBSideWithDot(Vector2D dot, ArrayList<Vector2D> AABB) {
            //TODO 받은 변의 벡터정보가 실제 AABB에 근사한지 확인하는 체크 필요. 현재는 무조건 맞다는 가정에 코드가 제작되어있음.
            double[] AABBxy = get_AABB_XY(AABB);
            double leastX = AABBxy[0], greatestX = AABBxy[1], leastY = AABBxy[2], greatestY = AABBxy[3]; //사각형 꼭지점 정보.

            double upsideDistance = Math.abs(dot.Y - greatestY);
            double downsideDistance = Math.abs(dot.Y - leastY);
            double leftsideDistance = Math.abs(dot.X - leastX);
            double rightsideDistance = Math.abs(dot.X - greatestX);


            //       해당코드는 AABB의 점 순서가 왼쪽 위, 오른쪽 위, 오른쪽 아래, 왼쪽 아래라는 순서를 전제로 만들어져있음!
            Vector2D leftupDot = AABB.get(0);
            Vector2D rightupDot = AABB.get(1);
            Vector2D rightdownDot = AABB.get(2);
            Vector2D leftdownDot = AABB.get(3);

            //upside가 가장 가까울때
            double minDistance = upsideDistance;
            Vector2D CrossDot = new Vector2D(dot.X, greatestY);
            Vector2D n = new Vector2D(0, 1);
            Vector2D dot1 = leftupDot;
            Vector2D dot2 = rightupDot;

            if (minDistance > downsideDistance) { //downside가 가장 가까울때
                minDistance = downsideDistance;
                CrossDot = new Vector2D(dot.X, leastY);
                n = new Vector2D(0, -1);
                dot1 = rightdownDot;
                dot2 = leftdownDot;
            }

            if (minDistance > leftsideDistance) { //leftside가 가장 가까울때
                minDistance = leftsideDistance;
                CrossDot = new Vector2D(leastX, dot.Y);
                n = new Vector2D(-1, 0);
                dot1 = leftdownDot;
                dot2 = leftupDot;
            }

            if (minDistance > rightsideDistance) { //rightside가 가장 가까울때
                minDistance = rightsideDistance;
                CrossDot = new Vector2D(greatestX, dot.Y);
                n = new Vector2D(1, 0);
                dot1 = rightupDot;
                dot2 = rightdownDot;
            }

            Vector2D[] returnArray = new Vector2D[4];
            returnArray[0] = dot1;
            returnArray[1] = dot2;
            returnArray[2] = CrossDot;
            returnArray[3] = n;
            return returnArray;
        }

        public static double[] get_AABB_XY(ArrayList<Vector2D> AABB) { //AABB의 최소X 최대X 최소Y 최대Y 순서대로 double 배열로 리턴.
            double leastX = 0, greatestX = 0, leastY = 0, greatestY = 0; //사각형 꼭지점 정보.
            for (Vector2D e : AABB) {
                if (AABB.indexOf(e) == 0) {
                    leastX = e.X;
                    greatestX = e.X;
                    leastY = e.Y;
                    greatestY = e.Y;
                }
                if (e.X < leastX) {
                    leastX = e.X;
                }
                if (e.X > greatestX) {
                    greatestX = e.X;
                }
                if (e.Y < leastY) {
                    leastY = e.Y;
                }
                if (e.Y > greatestY) {
                    greatestY = e.Y;
                }
            }
            double[] returnArray = new double[4];
            returnArray[0] = leastX;
            returnArray[1] = greatestX;
            returnArray[2] = leastY;
            returnArray[3] = greatestY;
            return returnArray;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        Image = bitmap;
    }
    public Bitmap getBitmap() {
        return Image;
    }

    void resizeBitmap(double width, double height) {
        Image = Bitmap.createScaledBitmap(Image, (int) width, (int) height, false);
    }
    protected void resizeBitmap(double size) {
        resizeBitmap(size,size);
    }

    static public void outDepth_makeImpulse(PhysicsObject a, PhysicsObject b, Vector2D collisionPoint, Vector2D CollisionDirection, double collisionDepth) { //물체 A, B ,충돌점, 충돌점에서 충돌방향, 충돌깊이
        double impulse = 0;
        Vector2D n = CollisionDirection.reSize(1);

        //서로를 충돌지점에서 빼낸다.
        moveOutFromDepth(a, b, n, collisionDepth);

        //충격력을 구한다.
        //자신에게 작용하는 충격력을 구하고 addImpulse 로 이동 방향을 정한다.
        Vector2D AMtoP = collisionPoint.minus(a.getMaterialPoint());
        Vector2D BMtoP = collisionPoint.minus(b.getMaterialPoint());
        Vector2D normalAngularA = AMtoP.getNormalVector();//A의 각속도 방향
        Vector2D normalAngularB = BMtoP.getNormalVector();

        Vector2D velocityAatP = a.getVelocityAtP(collisionPoint);
        Vector2D velocityBatP = b.getVelocityAtP(collisionPoint);
        Vector2D vAB = velocityAatP.minus(velocityBatP);

        double impulseFormulaNumerator = vAB.constantProduct(-1.8).dotProduct(n);
        double denominator1 = n.dotProduct(n.constantProduct(a.getInverseOfMass() + b.getInverseOfMass()));
        double denominator2 = normalAngularA.dotProduct(n) * normalAngularA.dotProduct(n) * a.getInverseOfI();
        double denominator3 = normalAngularA.dotProduct(n) * normalAngularA.dotProduct(n) * b.getInverseOfI();
        double impulseFormulaDenominator = denominator1 + denominator2 +denominator3;

        impulse = impulseFormulaNumerator / impulseFormulaDenominator;
        double frictionForceScalar = 0;  // TODO 미구현

        a.addImpulseAndFriction(impulse, n, normalAngularA, frictionForceScalar, b);
        if(b.isMovingObject())
        {
            b.addImpulseAndFriction(-impulse, n, normalAngularB, frictionForceScalar, a);
        }
    }


    private static void moveOutFromDepth(PhysicsObject a, PhysicsObject b, Vector2D n, double collisionDepth) {
        if (b.isMovingObject() == true) {
            a.setMaterialPoint(a.getMaterialPoint().plus(n.constantProduct(collisionDepth / 1.98)));
            b.setMaterialPoint(b.getMaterialPoint().plus(n.constantProduct(-collisionDepth / 1.98)));
        } else {
            Vector2D collisionDepthVector = n.constantProduct(collisionDepth * 1.04);
            a.setMaterialPoint(a.getMaterialPoint().plus(collisionDepthVector));
        }
    }

    public abstract void addImpulseAndFriction(double impulse, Vector2D n, Vector2D normalAngularA, double frictionForceScalar, PhysicsObject other);

    //public abstract void addImpulseAndFriction(double impulse, Vector2D n, double frictionForceScalar); //물리엔진 외부에서 물리 정보를 모르지만 충격량을 주고싶을때 사용.

    public void everyTick(){
        // 게임상에서 오브젝트가 매 틱마다 행동하는 것은 이 메소드를 오버라이드.
    }

    public void gameCollided(PhysicsObject other){
        // 게임상에서 충돌할시 벌어지는 일을 구현하려면 이 메소드를 오버라이드.
    }

    abstract void rotateByPivot(double angle, Vector2D pivot);


    abstract public boolean collisionCheck(PhysicsObject other);

    public abstract void setMaterialPoint(Vector2D mP);

    protected abstract boolean isMovingObject();

    public abstract double getInverseOfMass();

    protected abstract double getInverseOfI();

    public abstract Vector2D getVelocity();

    public abstract void setVelocity(Vector2D v);

    protected abstract Vector2D getVelocityAtP(Vector2D collisionPoint);



}
