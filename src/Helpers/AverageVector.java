package Helpers;

import GameFiles.GameSurface;
import GameFiles.GameVector;

import java.util.ArrayList;

public class AverageVector {

    public static GameVector getAverageObjectVector(ArrayList<GameSurface> surfaces) {
        GameVector sum = GameVector.ZERO;
        for (GameSurface s : surfaces)
            sum = sum.plus(s.getCenterOfSurface());
        sum = sum.times(1.0 / surfaces.size());
        return sum;
    }

    public static GameVector getAverageSurfaceVector(ArrayList<GameVector> surfaceBounds) {
        GameVector sum = GameVector.ZERO;
        for (GameVector v : surfaceBounds)
            sum = sum.plus(v);
        sum = sum.times(1.0 / surfaceBounds.size());

        return sum;
    }
}
