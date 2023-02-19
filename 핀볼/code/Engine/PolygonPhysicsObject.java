package com.example.pinball.Engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;

public class PolygonPhysicsObject extends PhysicsObject {
    boolean Collided = false; // 물체가 충돌될경우 act 함수가 호출될때까지 true 상태가 유지됨.

    Vector2D MaterialPoint; //물체의 무게중심(질점)
    ArrayList<Vector2D> OriginVertexVectors = new ArrayList<>(); //회전되지 않은 고유의 도형 모양. 회전당 발생하는 오차를 줄이기 위해 회전은 항상 본래 모양에서 계산됨.
    ArrayList<Vector2D> VertexVectors = new ArrayList<>(); // 중심점을 기준으로 꼭지점까지의 벡터들 ,p0 ~ pn-1 , 반드시 예각이 없는 다면체여야함.
    double SuperRange; //사각형을 감싸는 원의 반지름

    Vector2D Velocity = new Vector2D(0, 0);
    double Rotation = 0;
    double RotationSpeed = 0; //양수:반시계방향 음수:시계방향
    double InverseOfMass = 0.0001; // 질량의 역수
    double InverseOfI; //관성모멘트의 역수. 복잡한 도형은 근사값만 사용.

    Matrix MMatrix = new Matrix();
    Paint MPaint = new Paint();
    Vector2D ImagePaintVector;
    Vector2D OriginMPoint;


    @Override
    public void addImpulseAndFriction(double impulse, Vector2D n, Vector2D normalAngularA, double frictionForceScalar, PhysicsObject other) {
        Vector2D VelocityVariation = n.constantProduct(impulse * InverseOfMass);
        Velocity = Velocity.plus(VelocityVariation);
        double RotationSpeedVariation = (180 / Math.PI) * (normalAngularA.dotProduct(n.constantProduct(impulse)) * InverseOfI);
        RotationSpeed = RotationSpeed + RotationSpeedVariation;

        /*Vector2D VelocityVariation_P = VelocityVariation.plus(normalAngular.constantProduct(RotationSpeedVariation));
        Vector2D frictionDirection_P = VelocityVariation_P.reSize(1).inverse();
        Vector2D verbN = n.getNormalVector();
        Vector2D realFrictionDirection = verbN.constantProduct(verbN.dotProduct(frictionDirection_P)).reSize(1);
        Vector2D realFriction = realFrictionDirection.constantProduct(frictionForceScalar);
        Velocity = Velocity.plus(realFriction);*/ // TODO 마찰 미구현상태
    }

    @Override
    protected void rotateByPivot(double angle, Vector2D pivot) {
        setRotation(angle);
        Vector2D newM = getOriginMPoint().rotateDotByPivot(angle, pivot);
        setMaterialPoint(newM);
    }

    public Vector2D getOriginMPoint() {
        return OriginMPoint;
    }

    @Override
    public boolean collisionCheck(PhysicsObject other) {
        if (MaterialPoint.minus(other.getMaterialPoint()).getSize() + 0.02 <= SuperRange + other.getRadius()) {
            if(collisionCheck2(other)){
                return true;
            }
        }
        return false;
    }

    public boolean collisionCheck2(PhysicsObject other) {
        if (other instanceof PolygonPhysicsObject) {
            PolygonPhysicsObject otherPol = (PolygonPhysicsObject) other;
            GJK gjk = new GJK(this, otherPol);
            if(gjk.GJKResult ==true){
                outDepth_makeImpulse(this,other,gjk.CollisionPoint,gjk.EPANormalVector.inverse(),gjk.EPADepth);
                return true;
            }
        }
        return false;
    }

    public void addImpulseAndFriction(double impulse, Vector2D n, Vector2D normalAngular, double frictionForceScalar) {
        Vector2D VelocityVariation = n.constantProduct(impulse * InverseOfMass);
        Velocity = Velocity.plus(VelocityVariation);
        double RotationSpeedVariation = (180 / Math.PI) * (normalAngular.dotProduct(n.constantProduct(impulse)) * InverseOfI);
        RotationSpeed = RotationSpeed + RotationSpeedVariation;

        /*Vector2D VelocityVariation_P = VelocityVariation.plus(normalAngular.constantProduct(RotationSpeedVariation));
        Vector2D frictionDirection_P = VelocityVariation_P.reSize(1).inverse();
        Vector2D verbN = n.getNormalVector();
        Vector2D realFrictionDirection = verbN.constantProduct(verbN.dotProduct(frictionDirection_P)).reSize(1);
        Vector2D realFriction = realFrictionDirection.constantProduct(frictionForceScalar);
        Velocity = Velocity.plus(realFriction);*/ // TODO 마찰 미구현상태
    }

    @Override
    public void addGravitation(Vector2D gravity) {
        Velocity = Velocity.plus(gravity);
    }

    @Override
    public void act() {
        MaterialPoint = MaterialPoint.plus(Velocity);
        Rotation +=RotationSpeed;
        if(Rotation <0 || Rotation >360);{
            Rotation = Rotation %360;
        }
        rotateVertexes();

    }

    @Override
    protected Vector2D getVelocityAtP(Vector2D collisionPoint) {
        if(MovingObject ==true) {
            Vector2D AMtoP = collisionPoint.minus(MaterialPoint);
            Vector2D normalAngularThis = AMtoP.getNormalVector();//A의 각속도 방향
            return Velocity.plus(normalAngularThis.constantProduct(RotationSpeed * (Math.PI / 180)));
        }
        return new Vector2D(0,0);
    }

    void rotateVertexes(){
        ArrayList<Vector2D> newVertexVectors = new ArrayList<>();
        for (int i = 0; i < OriginVertexVectors.size(); i++) {
            newVertexVectors.add(OriginVertexVectors.get(i).rotate(Rotation));
        }
        synchronized (VertexVectors){
            VertexVectors = newVertexVectors;
        }
    }


    @Override
    public void paint(Canvas c) {
        Vector2D imagePaintPoint = getImagePaintPoint();
        MMatrix.setTranslate((float) imagePaintPoint.X, (float) imagePaintPoint.Y);
        MMatrix.postRotate((float) Rotation,(float)MaterialPoint.X,(float)MaterialPoint.Y);
        if(Image != null){
            tryPaint(c);
        }

//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        synchronized (VertexVectors) {
//            for (Vector2D v : VertexVectors) {
//                Vector2D otherPoint = MaterialPoint.plus(v);
//                c.drawLine((float) MaterialPoint.X, (float) MaterialPoint.Y, (float) otherPoint.X, (float) otherPoint.Y, paint);
//            }
//        }
    }

    private void tryPaint(Canvas c) {
        try{
            c.drawBitmap(Image, MMatrix, MPaint);
        }catch (Exception e){}
    }

    public Vector2D getImagePaintPoint() {
        return MaterialPoint.plus(ImagePaintVector);
    }


    @Override
    public double getRadius() {
        return SuperRange;
    }


    public Vector2D getMaterialPoint() {
        return MaterialPoint;
    }

    @Override
    public void setMaterialPoint(Vector2D mP) {
        MaterialPoint = mP;
    }

    @Override
    protected boolean isMovingObject() {
        return MovingObject;
    }

    @Override
    public double getInverseOfMass() {
        return InverseOfMass;
    }

    @Override
    protected double getInverseOfI() {
        return InverseOfI;
    }

    @Override
    public Vector2D getVelocity() {
        return Velocity;
    }

    @Override
    public void setVelocity(Vector2D v) {
        Velocity =v;
    }

    protected double getRotation(){return Rotation;}

    protected void setRotation(double r){
        rotateVertexes();
        Rotation = r;
    }

    public void setOriginMPoint(Vector2D oriPo){
        OriginMPoint = oriPo;
    }

}