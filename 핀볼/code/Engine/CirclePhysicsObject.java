package com.example.pinball.Engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.example.pinball.GameObjectCodes.AccelerationBlock;
import com.example.pinball.GameObjectCodes.ArcherCircle;
import com.example.pinball.GameObjectCodes.ReductionBlock;
import com.example.pinball.GameObjectCodes.ReductionCircle;

import java.util.ArrayList;

public class CirclePhysicsObject extends PhysicsObject {

    private Vector2D materialPoint;
    protected double radius;
    protected double InverseOfMass = 0.001;
    private double InverseOfI = 0;

    private Vector2D velocity = new Vector2D(0, 0);
    private ArrayList<Vector2D> colliderTest = new ArrayList<>();
    Vector2D collisionPoint = new Vector2D(0, 0);
    Vector2D collisionDirection = new Vector2D(0, 0);
    double collisionDepth = 0;

    //double m = 1;

    boolean flag = true;

    Paint imagePaint = new Paint();
    Matrix matrix = new Matrix();

    /*public CirclePhysicsObject(Vector2D position, Bitmap bitmap) {
        materialPoint = position;
        setBitmap(bitmap);
        radius = bitmap.getWidth() / 2.0;
    }

    public CirclePhysicsObject(Vector2D position, int r, Bitmap bitmap) {
        materialPoint = position;
        setBitmap(bitmap);
        resizeBitmap(r*2);
        radius = r;
    }

    public CirclePhysicsObject(Vector2D position, int r, Bitmap bitmap, boolean move) {
        materialPoint = position;
        setBitmap(bitmap);
        resizeBitmap(r*2);
        radius = r;
        if(move == false) {
            MovingObject = false;
        }
    }*/

    public CirclePhysicsObject(Vector2D position, int id, Resources resources) {
        materialPoint = position;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        setBitmap(bitmap);
        radius = bitmap.getWidth() / 2.0;
    }

    public CirclePhysicsObject(Vector2D position, int r, int id, Resources resources) {
        materialPoint = position;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        setBitmap(bitmap);
        resizeBitmap(r*2);
        radius = r;
    }

    public CirclePhysicsObject(Vector2D position, int r, int id, Resources resources, boolean move) {
        materialPoint = position;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        setBitmap(bitmap);
        resizeBitmap(r*2);
        radius = r;
        if(move == false) {
            MovingObject = false;
        }
    }

    @Override
    public boolean collisionCheck(PhysicsObject x) {
        if (x instanceof CirclePhysicsObject) {
            CirclePhysicsObject other = (CirclePhysicsObject) x;

            double distance = distance(this.materialPoint, other.getMaterialPoint());

            if (distance <= this.radius + other.radius) {
                /*other.flag = false;
                moveByCollision(other);
                other.moveByCollision(this);  구버전 코드
                other.flag = true;

                moveByCollision(other);*/

                //other에서 this의 중점으로 향하는 벡터를 만들고, 그 벡터의 크기를 절반으로 줄였을 때, 도달하는 위치의 점이 충돌 지점.
                Vector2D OtherToThisVector= this.materialPoint.minus(other.getMaterialPoint());
                OtherToThisVector = OtherToThisVector.reSize(0.5);
                collisionPoint = other.getMaterialPoint().plus(OtherToThisVector);

                // 충돌 깊이 = distance - {(distance - this.r) + (distance - other.r)} = this.r + other.r -distance
                collisionDepth = this.radius + other.radius - distance;

                // 충돌 방향 = OtherToThisVector 의 정규(normal) 벡터
                collisionDirection = OtherToThisVector.reSize(1);

                outDepth_makeImpulse(this,other,collisionPoint,collisionDirection,collisionDepth); //충격 공식
                return true;
            }
        }

        if (x instanceof RectanglePhysicsObject) {
            RectanglePhysicsObject other = (RectanglePhysicsObject) x;
            if (distance(this.materialPoint, other.getMaterialPoint()) <= (this.radius + other.getRadius())) {
                if (isCollidedWithRect(other)) {
                    outDepth_makeImpulse(this,other,collisionPoint,collisionDirection,collisionDepth);
                    return true;
                }
            }
        }
        return false;
    }


    private void moveByCollision(CirclePhysicsObject other) {

        double otherPositionVX = other.materialPoint.X - this.materialPoint.X;
        double otherPositionVY = other.materialPoint.Y - this.materialPoint.Y;
        Log.d("mpx", Double.toString(otherPositionVX));
        Log.d("mpy", Double.toString(otherPositionVY));

        this.collisionPoint = new Vector2D(otherPositionVX, otherPositionVY);
        double scalar = distance(this.materialPoint, other.materialPoint) * this.radius / (this.radius + other.radius);
        this.collisionPoint.reSize(scalar);

        Log.d("collision x", Double.toString(collisionPoint.X));
        Log.d("collision y", Double.toString(collisionPoint.Y));


        //repositioning
        other.materialPoint.X += this.collisionPoint.X / this.radius;
        other.materialPoint.Y += this.collisionPoint.Y / this.radius;

        //update velocity
        other.velocity.X += this.collisionPoint.X / this.radius;
        other.velocity.Y += this.collisionPoint.Y / this.radius;
    }

   /* private double getForce(CirclePhysicsObject other) {
        double j = 1 * velocity.minus(other.velocity).getSize() / (1 / m + 1 / other.m);
        return j / m / 100;
    }*/

    @Override
    public void addGravitation(Vector2D gravity) {
        velocity = velocity.plus(gravity);
    }

    @Override
    public void act() {
//        addGravitation(new Vector2D(0, 1));
        materialPoint = materialPoint.plus(velocity);
    }

    @Override
    public void paint(Canvas c) {

        /*ArrayList<Vector2D> conVertexVectors = new ArrayList<>();
        for(Vector2D v : colliderTest){
            conVertexVectors.add(v.conversion(widthRate, heightRate));
        }*/
        matrix.setTranslate((float) (materialPoint.X - radius), (float) (materialPoint.Y - radius));
        c.drawBitmap(Image, matrix, imagePaint);
    }


    public void convertBitmap(double widthRate, double heightRate) {        // ???
        Image = Bitmap.createBitmap(Image, 0, 0, (int) (Math.round(Image.getWidth()) * widthRate), (int) (Math.round(Image.getHeight() * heightRate)));
        Image = Bitmap.createBitmap(Image, 0, 0, (int) (Math.round(Image.getWidth()) * widthRate), (int) (Math.round(Image.getHeight() * heightRate)));
    }

    public Bitmap getBitmap() {
        return Image;
    }

    public void setBitmap(Bitmap bitmap) {
        Image = bitmap;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public Vector2D getMaterialPoint() {
        return materialPoint;
    }

    private double distance(Vector2D a, Vector2D b) {
        return Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2));
    }

    private boolean isCollidedWithRect(RectanglePhysicsObject other) {
        boolean result = false;

        //collider setting
        ArrayList<Vector2D> vertexVector = other.OriginVertexVectors;
        ArrayList<Vector2D> boxCollider = new ArrayList<>();
        /*double colliderHeight =  other.getBitmap().getHeight() / 2 + radius;
        double colliderWidth = other.getBitmap().getWidth() / 2 + radius; */

        //double newVectorSize = Math.sqrt(Math.pow(colliderHeight, 2)+Math.pow(colliderWidth, 2));

        //collision check
        for (int i = 0; i < 4; i++) {
            boxCollider.add(vertexVector.get(i).plus(other.MaterialPoint)); //충돌하는 사각형의, 회전되지 않은 상태의 점 정보 4개 (View 의 절대좌표상태)
        }
        Vector2D IR_materialPoint = materialPoint.rotateDotByPivot(-other.Rotation, other.MaterialPoint); //사각형이 중심점에서 회전하는 것을 기준으로 그 반대로 회전된 원의 중심점.

        int zoneStatus = getRectZone(IR_materialPoint, boxCollider); //회전된 원 중심점의 위치 확인.
        if (zoneStatus == 0) { //원의 중심점이 사각형 내부에 있을경우 반드시 충돌.
            result = true;
        } else if (zoneStatus == 1) { //원의 중심점이 사각형 모서리 방향에 있을경우 그 모서리가 원과 충돌하면 충돌.
            for (Vector2D e : boxCollider) {
                if (getCircleZone(e, IR_materialPoint, radius)) {
                    result = true;
                    collisionPoint = e;
                }
            }
        } else if (zoneStatus == 2) {  //원의 반지름과 사각형 중심의 Y값의 차이가, 높이값의 절반과 반지름의 합보다 작으면 충돌.
            double h = other.getBitmap().getHeight() / 2;
            double r = radius;
            double a = Math.abs(IR_materialPoint.Y - other.MaterialPoint.Y);
            if (h + r >= a) {
                result = true;
            }
        } else if (zoneStatus == 3) {  //원의 반지름과 사각형 중심의 X값의 차이가 넓이값의 절반보다 작으면 충돌.
            double h = other.getBitmap().getWidth() / 2;
            double r = radius;
            double a = Math.abs(IR_materialPoint.X - other.MaterialPoint.X);
            if (h + r >= a) {
                result = true;
            }
        }

        if (result == true && (zoneStatus == 0 || zoneStatus == 2 || zoneStatus == 3)) {
            Vector2D[] colliedSide = UtilFunc.getClosedAABBSideWithDot(IR_materialPoint, boxCollider);
            collisionPoint = colliedSide[2];
            collisionDirection = colliedSide[3];
            if (zoneStatus == 0) {
                collisionDepth = radius + Math.abs(IR_materialPoint.minus(collisionPoint).getSize());
            } else {
                collisionDepth = Math.abs(radius - Math.abs(IR_materialPoint.minus(collisionPoint).getSize()));
            }
        } else if (result == true && zoneStatus == 1) {
            // collisionPoint 충돌 체크에서 추가함
            collisionDirection = IR_materialPoint.minus(collisionPoint);
            collisionDepth = Math.abs(radius - collisionDirection.getSize());
        }

        /*if(boxCollider.get(0).X <= materialPoint.X || boxCollider.get(0).Y <= materialPoint.Y
                || boxCollider.get(2).X >= materialPoint.X || boxCollider.get(2).Y >= materialPoint.Y){
            result = true;
        }*/

        //구해진 충돌 정보를 사각형의 중점 기준으로 다시 회전시켜 본래의 충돌 정보를 구함.
        collisionPoint = collisionPoint.rotateDotByPivot(other.Rotation, other.MaterialPoint);
        collisionDirection = collisionDirection.rotate(other.Rotation);

        return result;
    }

    int getRectZone(Vector2D dot, ArrayList<Vector2D> RectDots) { //점이 사각형 어디에 위치하느냐에 따라 사각형 내부: 0, 사각형 모서리: 1, 사각형 위아래 변:2, 사각형 양옆 변: 3을 반환함.
        //TODO UtilFunc 으로 이동.
        double[] AABBxy = UtilFunc.get_AABB_XY(RectDots);
        double leastX = AABBxy[0], greatestX = AABBxy[1], leastY = AABBxy[2], greatestY = AABBxy[3];
        boolean dotX_inRect = (dot.X >= leastX && dot.X <= greatestX);
        boolean dotY_inRect = (dot.Y >= leastY && dot.Y <= greatestY);
        if (dotX_inRect && dotY_inRect) {
            return 0;
        } else if (dotX_inRect) {
            return 2;
        } else if (dotY_inRect) {
            return 3;
        } else {
            return 1;
        }

    }

    boolean getCircleZone(Vector2D dot, Vector2D center, double r) { //점이 원 내부에 존재하는지 판별.
        if (dot.minus(center).getSize() < r) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addImpulseAndFriction(double impulse, Vector2D n, Vector2D normalAngularA, double frictionForceScalar, PhysicsObject other) {
        if(other instanceof ArcherCircle || other instanceof AccelerationBlock)
            impulse *= 1.2;
        else if(other instanceof ReductionCircle || other instanceof ReductionBlock)
            impulse *= 0.8;
        Vector2D impulseVector = n.constantProduct(impulse * InverseOfMass);
        velocity = velocity.plus(impulseVector);
    }

    @Override
    void rotateByPivot(double angle, Vector2D pivot) {

    }

    @Override
    public void setMaterialPoint(Vector2D mP) {
        materialPoint = mP;
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
        return 0;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2D v) {
        velocity = v;
    }

    @Override
    protected Vector2D getVelocityAtP(Vector2D collisionPoint) {
        return velocity;
    }
}