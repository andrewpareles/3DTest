package GameFiles;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSurface {

    private ArrayList<GameVector> surfaceBounds = new ArrayList<>();
    private Color color = new Color(0, 0, 0);

    public GameSurface(GameVector p1, GameVector p2, GameVector p3) {
        surfaceBounds.addAll(Arrays.asList(p1, p2, p3));
    }

    public GameSurface(GameVector p1, GameVector p2, GameVector p3, GameVector p4) {
        surfaceBounds.addAll(Arrays.asList(p1, p2, p3, p4));
    }


    public Color getColor() {
        return color;
    }

    public GameSurface setColor(Color c) {
        this.color = c;
        return this;
    }

    public GameVector getPoint(int n) {
        return surfaceBounds.get(n);
    }

    // Either 3 or 4
    public int getNumPoints() {
        return surfaceBounds.size();
    }

    public GameVector getAverageSurfaceVector() {
        GameVector sum = GameVector.ZERO;
        for (GameVector v : surfaceBounds)
            sum = sum.plus(v);
        sum = sum.times(1.0 / surfaceBounds.size());

        return sum;
    }

    public void shiftBy(GameVector shiftAmount) {
        for (int i = 0; i < surfaceBounds.size(); i++)
            surfaceBounds.set(i, surfaceBounds.get(i).plus(shiftAmount));
    }

    public void rotateBy(GameVector focus, GameVector axis, double theta) {
        for (int i = 0; i < surfaceBounds.size(); i++)
            surfaceBounds.set(i, surfaceBounds.get(i).rotateBy(focus, axis, theta));
    }

    public void scaleBy(GameVector focus, double percentChange) {
        for (int i = 0; i < surfaceBounds.size(); i++)
            surfaceBounds.set(i, surfaceBounds.get(i).scaleBy(focus, percentChange));
    }


}
