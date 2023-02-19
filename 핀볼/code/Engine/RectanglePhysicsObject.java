package com.example.pinball.Engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

//TODO 엄밀한 정의를 위해 사각형 모양을 사용하는 오브젝트는 이것을 상속하도록 합시다. 한 점, 직선 경계면 등은 GJK 알고리즘으로 작동하는 PolygonPO로 제작 가능합니다. (대신 원 오브젝트에서 따로 원-폴리곤 충돌 검사를 정의해 줘야 합니다.)
public class RectanglePhysicsObject extends PolygonPhysicsObject{


    /*//비트맵이 없으면 표시된 비트맵 없이 내부 벡터정보만 존재함.
    public RectanglePhysicsObject(Vector2D position, double width, double height) {
        MaterialPoint = position;
        ComputedDataFromWH(width, height);
    }
    //비트맵을 받아 출력하는 오브젝트가 됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, Bitmap bitmap) {
        this(position,width,height);
        setBitmap(bitmap);
        resizeBitmap(width, height);
    }
    //MovingObject 가 false 면 물리법칙에 의해 움직이지 않고 고정됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, Bitmap bitmap, boolean moveBool) {
        this(position, width, height, bitmap);
        MovingObject = moveBool;
        if(MovingObject == false){
            InverseOfMass = 0;
            InverseOfI = 0;
        }
    }
    //Rotation 을 설정하면 (360도 단위) 폴리곤이 돌아간 형태로 생성됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, Bitmap bitmap, double rotation, boolean moveBool){
        this(position,width,height,bitmap,moveBool);
        Rotation = rotation;
    }
    //점을 통한 생성은 x, y의 최대 최소 정보를 받음. 주의!!: Y는 커질수록 아래로 내려감!
    public RectanglePhysicsObject(double leftX, double rightX, double upY, double downY){
        Image = null;
        ComputedDataFromDot(leftX, rightX, upY, downY); //TODO 미완성 상태
    }*/

    //비트맵이 없으면 표시된 비트맵 없이 내부 벡터정보만 존재함.
    public RectanglePhysicsObject(Vector2D position, double width, double height) {
        MaterialPoint = position;
        OriginMPoint = MaterialPoint;
        ComputedDataFromWH(width, height);
    }
    //비트맵을 받아 출력하는 오브젝트가 됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, int id, Resources resources) {
        this(position,width,height);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        setBitmap(bitmap);
        resizeBitmap(width, height);
    }
    //MovingObject 가 false 면 물리법칙에 의해 움직이지 않고 고정됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, int id, Resources resources, boolean moveBool) {
        this(position, width, height, id, resources);
        MovingObject = moveBool;
        if(MovingObject == false){
            InverseOfMass = 0;
            InverseOfI = 0;
        }
    }
    //Rotation 을 설정하면 (360도 단위) 폴리곤이 돌아간 형태로 생성됨.
    public RectanglePhysicsObject(Vector2D position, double width, double height, int id, Resources resources, double rotation, boolean moveBool){
        this(position,width,height,id, resources,moveBool);
        Rotation = rotation;
    }
    //점을 통한 생성은 x, y의 최대 최소 정보를 받음. 주의!!: Y는 커질수록 아래로 내려감!
    public RectanglePhysicsObject(double leftX, double rightX, double upY, double downY){
        Image = null;
        ComputedDataFromDot(leftX, rightX, upY, downY); //TODO 미완성 상태
    }

    void ComputedDataFromWH(double width, double height){
        double wHalf = width / 2;
        double hHalf = height / 2;

        OriginVertexVectors = makeRectVertex(wHalf, hHalf);
        VertexVectors = makeRectVertex(wHalf, hHalf);

        ImagePaintVector = VertexVectors.get(0);

        SuperRange = Math.sqrt(width * width + height * height);
        InverseOfI = (12 * InverseOfMass) / (width * width + height * height);
    }

    void ComputedDataFromDot(double leftX, double rightX, double upY, double downY) {
        if(leftX > rightX){
            double a= rightX; rightX = leftX; leftX = a;
        }
        if(upY > downY){
            double a= downY; downY = upY; upY = a;
        }
        MaterialPoint = new Vector2D((leftX + rightX) /2, (upY+ downY) /2);

        double width  = rightX - leftX ; double wHalf = width/2;
        double height = downY - upY; double hHalf = height/2;
        OriginVertexVectors  = makeRectVertex(wHalf,hHalf);
        VertexVectors = makeRectVertex(wHalf, hHalf);
        ImagePaintVector = VertexVectors.get(0);

        SuperRange = Math.sqrt(width * width + height * height);
        InverseOfI = (12 * InverseOfMass) / (width * width + height * height);
    }

    private ArrayList<Vector2D> makeRectVertex(double wHalf, double hHalf) {
        ArrayList<Vector2D> returnA = new ArrayList<>();
        returnA.add(new Vector2D(-wHalf, -hHalf));
        returnA.add(new Vector2D(wHalf, -hHalf));
        returnA.add(new Vector2D(wHalf, hHalf));
        returnA.add(new Vector2D(-wHalf, hHalf));
        return returnA;
    }
}
