package com.example.pinball.Engine;

import java.io.Serializable;

public class Vector2D implements Serializable {
    double X, Y;
    public Vector2D(double x, double y){
        X = x;
        Y = y;
    }

    public Vector2D plus(Vector2D other){
        return new Vector2D(X+other.X, Y+other.Y);
    }
    public Vector2D minus(Vector2D other){
        return new Vector2D(X-other.X, Y-other.Y);
    }
    public Vector2D conversion(double a,double b){
//        return new Vector2D(X*a, Y*b);        //TODO: 이미지 전체 비율에 문제가 있습니다. 일단 주석처리 해두겠습니다.
        return new Vector2D(X, Y);
    }
    public Vector2D inverse(){
        return new Vector2D(-X,-Y);
    }
    public double dotProduct(Vector2D other){
        return X*other.X + Y*other.Y;
    }
    public Vector2D constantProduct(double a){return new Vector2D(a*X,a*Y);}
    public double imaginaryZCrossProduct(Vector2D other){return X*other.Y - Y*other.X;}
    public double getSize(){
        return Math.sqrt(X*X + Y*Y);
    }
    public Vector2D reSize(double afterSize){
        double beforeSize = getSize();
        double ratio = afterSize / beforeSize;
        return new Vector2D(X *ratio,Y*ratio);
    }
    public Vector2D getNormalVector(){
        Vector2D returnVector = new Vector2D(-Y,X);
        returnVector = returnVector.reSize(1);
        return returnVector;
    } //90도 회전한 정규벡터
    public Vector2D rotate(double angle){
        double radianAngle = (Math.PI / 180) * angle;
        Vector2D returnVector = new Vector2D(0,0);
        returnVector.X = Math.cos(radianAngle)*X - Math.sin(radianAngle)*Y;
        returnVector.Y = Math.sin(radianAngle)*X + Math.cos(radianAngle)*Y;
        return returnVector;
    }
    public Vector2D rotateDotByPivot(double angle, Vector2D pivot){
        Vector2D IRVector = this.minus(pivot);
        IRVector = IRVector.rotate(angle);
        return pivot.plus(IRVector);
    }
    public Vector2D tripleCrossProduct(Vector2D other1, Vector2D other2){
        Vector2D A = this;
        Vector2D B = other1;
        Vector2D C = other2;
        return B.constantProduct(C.dotProduct(A)).minus(A.constantProduct(C.dotProduct(B))).reSize(1);
    }

    public LinearLine getLinearLine(Vector2D other){ //두 벡터가 정점일때, 그 두점을 지나는 일차방정식을 구함.
        double a =this.X;  double b = this.Y;  double c = other.X;  double d = other.Y;
        if(a==c){
            return new LinearNoYLine(a);
        }
        else {
            double A = (b - d) / (a - c);
            double B = ((a * d) - (b * c)) / (a - c);
            return new LinearEquation(A, B);
        }
    }

    @Override
    public boolean equals(Object obj){
        Vector2D other =(Vector2D)obj;
        if(this.X == other.X & this.Y ==other.Y){
            return true;
        }
        else{
            return false;
        }
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }
}
