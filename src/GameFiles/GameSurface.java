package GameFiles;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSurface {

    private ArrayList<GameVector> surfaceBounds = new ArrayList<>();
    private Color color;

    private GameSurface(Color color, GameVector... ps) {
        surfaceBounds.addAll(Arrays.asList(ps));
        this.color = color;
    }

    //f is the pivot point, all the surfaces returned are based on it, ie new GameSurface(f, p_n, p_n+1)
    public static ArrayList<GameSurface> createSurface(Color color, GameVector f, GameVector p2, GameVector p3, GameVector... pn) {
        ArrayList<GameSurface> surfaces = new ArrayList<>();
        surfaces.add(new GameSurface(color, f, p2, p3));
        if (pn.length >= 1) surfaces.add(new GameSurface(color, f, p3, pn[0])); // f p3 p4
        for (int i = 1; i < pn.length - 1; i++) surfaces.add(new GameSurface(color, f, pn[i], pn[i + 1]));
        return surfaces;
    }

    //creates a polygon
    public static ArrayList<GameSurface> createSurface(Color color, GameVector position, GameVector normal, int numSides) {
        ArrayList<GameSurface> surfaces = new ArrayList<>();
//        surfaces.add();
        return surfaces;
//TODO

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
