package GameFiles;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSurface {

    //RI: size of surfaceBounds = 3
    private ArrayList<GameVector> surfaceBounds = new ArrayList<>();
    private Color color;

    private GameSurface(Color color, GameVector p1, GameVector p2, GameVector p3) {
        surfaceBounds.addAll(Arrays.asList(p1, p2, p3));
        this.color = color;
    }

    //p1 is the pivot point, all the surfaces returned are based on it, ie new GameSurface(p1, p_n, p_n+1)
    public static ArrayList<GameSurface> createSurface(Color color, GameVector p1, GameVector p2, GameVector p3, GameVector... ps) {
        ArrayList<GameSurface> surfaces = new ArrayList<>();
        surfaces.add(new GameSurface(color, p1, p2, p3));
        if (ps.length >= 1) surfaces.add(new GameSurface(color, p1, p3, ps[0])); // p1 p3 p4
        for (int i = 1; i < ps.length - 1; i++) surfaces.add(new GameSurface(color, p1, ps[i], ps[i + 1]));
        return surfaces;
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

    public GameVector getCenterOfSurface() {
        return Helpers.AverageVector.getAverageSurfaceVector(this.surfaceBounds);
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
