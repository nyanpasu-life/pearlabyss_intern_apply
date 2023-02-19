package com.example.pinball.Engine;

import java.util.ArrayList;

class GJKNoneRecursive extends GJK {
    GJKNoneRecursive(ArrayList<Vector2D> a, ArrayList<Vector2D> b){
        super(a,b);
    }
    @Override
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
                    return true;
                }
            }
        }

    }
    @Override
    Vector2D supportFunctionForGJK(Vector2D d) {
        Vector2D p1 = getLargestDotProduct(APoints, d);
        Vector2D p2 = getLargestDotProduct(BPoints, d.inverse());
        return p1.minus(p2);
    }
}
