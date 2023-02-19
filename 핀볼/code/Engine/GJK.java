package com.example.pinball.Engine;

import java.util.ArrayList;

public class GJK {
    ArrayList<Vector2D> APoints = new ArrayList<>();
    ArrayList<Vector2D> BPoints = new ArrayList<>();
    ArrayList<Vector2D> Simplex = new ArrayList<>();

    Vector2D SearchD = new Vector2D(1, 0);

    boolean GJKResult = false;
    Vector2D EPANormalVector;
    double EPADepth;
    Vector2D CollisionPoint;
    Vector2D CollisionPoint1;
    Vector2D CollisionPoint2;

    GJK(PolygonPhysicsObject a, PolygonPhysicsObject b) {
        for (Vector2D e : a.VertexVectors) {
            APoints.add(a.MaterialPoint.plus(e));
        }
        for (Vector2D e : b.VertexVectors) {
            BPoints.add(b.MaterialPoint.plus(e));
        }
        GJKResult = doGJK(APoints, BPoints);
        if(GJKResult==true){
            doEPA();
        }

    }
    GJK(ArrayList<Vector2D> a, ArrayList<Vector2D> b){
        APoints = a;
        BPoints = b;
        GJKResult = doGJK(APoints, BPoints);
        if(GJKResult==true){
            doEPA();
        }
    }

    boolean doGJK(ArrayList<Vector2D> aPoints, ArrayList<Vector2D> bPoints) {
        Simplex.add(supportFunctionForGJK(SearchD));
        SearchD = SearchD.inverse();

        while (true) {
            Vector2D simplexLast = supportFunctionForGJK(SearchD);
            Simplex.add(simplexLast);
            if (simplexLast.dotProduct(SearchD) <= 0) {
                return false;
            } else {
                if (isSimplexContainsOrigin(Simplex)) {
                    ArrayList<Vector2D> onlyP1 = new ArrayList<>();
                    onlyP1.add(CollisionPoint1);
                    ArrayList<Vector2D> onlyP2 = new ArrayList<>();
                    onlyP2.add(CollisionPoint2);

                    GJK A_p2 = new GJKNoneRecursive(APoints, onlyP2);
                    GJK B_p1 = new GJKNoneRecursive(BPoints, onlyP1);

                    if(A_p2.GJKResult ==true & B_p1.GJKResult==false){
                        CollisionPoint = CollisionPoint2;
                    }
                    if(A_p2.GJKResult ==false & B_p1.GJKResult==true){
                        CollisionPoint = CollisionPoint1;
                    }
                    else {
                        CollisionPoint = CollisionPoint1.plus(CollisionPoint2).constantProduct(0.5);
                    }

                    return true;
                }
            }
        }

    }

    boolean isSimplexContainsOrigin(ArrayList<Vector2D> simplex) {
        Vector2D a = Simplex.get(Simplex.size()-1);
        Vector2D aInverse = a.inverse();
        if (Simplex.size() == 3) {
            Vector2D c = Simplex.get(0);
            Vector2D b = Simplex.get(1);

            Vector2D ab = b.minus(a);
            Vector2D ac = c.minus(a);
            Vector2D perp_ab = ac.tripleCrossProduct(ab,ab);
            Vector2D perp_ac = ab.tripleCrossProduct(ac,ac);

            if(perp_ab.dotProduct(aInverse) >0){
                Simplex.remove(c);
                SearchD = perp_ab;
            }
            else if(perp_ac.dotProduct(aInverse)>0){
                Simplex.remove(b);
                SearchD = perp_ac;
            }
            else{
                return true;
            }
        }
        else {
            Vector2D b = Simplex.get(0);
            Vector2D ab = b.minus(a);
            Vector2D perp_ab = ab.tripleCrossProduct(aInverse,ab);
            SearchD = perp_ab;
        }
        return false;
    }

    private Vector2D getSearchD(ArrayList<Vector2D> simplex) {
        Vector2D B = Simplex.get(0);
        Vector2D A = Simplex.get(1);
        Vector2D AB = A.minus(B);
        Vector2D AO = A;
        return AB.tripleCrossProduct(AO, AB);
    }


    Vector2D supportFunctionForGJK(Vector2D d) {
        Vector2D p1 = getLargestDotProduct(APoints, d);
        Vector2D p2 = getLargestDotProduct(BPoints, d.inverse());
        CollisionPoint1 = p1;
        CollisionPoint2 = p2;
        return p1.minus(p2);
    }

    Vector2D findCollisionPoint(Vector2D p1, Vector2D p2){
        ArrayList<Vector2D> ArrayP1 = new ArrayList<>();
        ArrayP1.add(p1);
        ArrayList<Vector2D> ArrayP2 = new ArrayList<>();
        ArrayP1.add(p2);

        GJK A_p2 = new GJKNoneRecursive(APoints, ArrayP2);
        GJK B_p1 = new GJKNoneRecursive(BPoints, ArrayP1);

        if(A_p2.GJKResult == true & B_p1.GJKResult ==false){
            return p2;
        }
        if(A_p2.GJKResult == false & B_p1.GJKResult ==true){
            return p1;
        }
        else{
            return p1.plus(p2).constantProduct(0.5);
        }
    }

    Vector2D getLargestDotProduct(ArrayList<Vector2D> aPoints, Vector2D d) {
        Vector2D returnVector = null;
        boolean firstBool = true;
        double max = 0;
        double distance;
        for (Vector2D e : aPoints) {
            if (firstBool == true) {
                max = e.dotProduct(d);
                returnVector = e;
                firstBool = false;
            } else {
                distance = e.dotProduct(d);
                if (max < distance) {
                    max = distance;
                    returnVector = e;
                }
            }
        }
        return returnVector;
    }


    //GJK
    //--------------------------------------------------------------------------------------
    //EPA

    class Edge{
        Vector2D NormalVector;
        double Distance;
        int Index;
    }

    void doEPA(){
        while (true){
            Edge e = findEdge();
            Vector2D p = supportFunctionForEPA(e.NormalVector);

            double d = p.dotProduct(e.NormalVector);
            if(d - e.Distance < 0.03){
                EPANormalVector = e.NormalVector;
                EPADepth = d;
                break;
            }
            else {
                Simplex.add(e.Index, p);
            }
        }
    }

    Edge findEdge(){
        Edge closest = new Edge();
        closest.Distance = Double.MAX_VALUE;
        for(int i =0;i<Simplex.size();i++){
            int j = i +1 == Simplex.size()? 0 : i+1;
            Vector2D a = Simplex.get(i);
            Vector2D b = Simplex.get(j);
            Vector2D e = b.minus(a);
            Vector2D n = e.tripleCrossProduct(a,e);
            n.reSize(1);
            double d = n.dotProduct(a);

            if(d <closest.Distance){
                closest.Distance = d;
                closest.NormalVector =n;
                closest.Index=j;
            }
        }
        return closest;
    }

    Vector2D supportFunctionForEPA(Vector2D d) {
        Vector2D p1 = getLargestDotProduct(APoints, d);
        Vector2D p2 = getLargestDotProduct(BPoints, d.inverse());

        return p1.minus(p2);
    }
}

