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

    //creates a polygon, center is the center of it, p1 is a point on the polygon, normal is normal to the surface
    public static GameSurface createSurface(Color color, GameVector center, GameVector normal, GameVector p1, int numSides) {
        GameVector r = p1.minus(center);
        double changeAngle = 2 * Math.PI / numSides;
        GameVector[] points = new GameVector[numSides];

        points[0] = r;
        for (int i = 1; i < numSides; i++) points[i] = points[i - 1].rotateBy(GameVector.ZERO, normal, changeAngle);

        GameSurface s = new GameSurface(color, points);
        s.shiftBy(center);

        return s;
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

    public int getNumPoints() {
        return surfaceBounds.size();
    }

    //I THINK this will always lie in the plane of the surface
    public GameVector getCenterOfSurface() {
        return Helpers.AverageVector.getAverageSurfaceVector(this.surfaceBounds);
    }

    public GameVector getNormalVector() {
        GameVector center = getCenterOfSurface();
        return surfaceBounds.get(0).minus(center)
                .cross(surfaceBounds.get(1).minus(center)).normalize();
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
