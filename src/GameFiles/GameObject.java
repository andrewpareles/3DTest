package GameFiles;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

//a collection of surfaces
public class GameObject {
    // any number of surfaces

    private Runnable action;
    private ArrayList<GameSurface> surfaces = new ArrayList<>();

    // takes in a list of ArrayList<GameSurface>, useful for GameSurface.createSurface(...)
    public GameObject(ArrayList<GameSurface>... lsts) {
        for (ArrayList<GameSurface> s : lsts)
            surfaces.addAll(s);
    }

    // takes in a list of GameSurfaces
    public GameObject(GameSurface... s) {
        surfaces.addAll(Arrays.asList(s));
    }

    private GameObject setAction(Runnable r) {
        this.action = r;
        return this;
    }

    public void run() {
        if (action != null) action.run();
    }

    public GameObject spin(GameVector axis, double rotationsPerSecond) {
        setAction(() -> {
            GameVector ctrOfClosest = this.getCenterOfObject();
            this.rotateBy(ctrOfClosest, axis, Math.toRadians(rotationsPerSecond * 360d) / Game.fps);
        });

        return this;
    }

    public GameObject grow(double percent100PerSecond) {
        setAction(() -> {
            GameVector ctrOfClosest = this.getCenterOfObject();
            this.scaleBy(ctrOfClosest, (percent100PerSecond / 100d) / Game.fps);
        });

        return this;
    }

    // repels proportionally to scaleConst * (1 / distance^inversePow)
    public GameObject repelFrom(GameVector repel, double scaleConst, double inversePow) {
        setAction(() -> {
            GameVector distToClosest = this.getCenterOfObject().minus(repel);
            GameVector amt = distToClosest.times(scaleConst / Math.pow(distToClosest.lengthSquared(), inversePow / 2));
            this.shiftBy(amt);
        });

        return this;
    }


    public ArrayList<GameSurface> getSurfaces() {
        return surfaces;
    }

    public GameVector getCenterOfObject() {
        return Helpers.AverageVector.getAverageObjectVector(this.surfaces);
    }

    public void rotateBy(GameVector focus, GameVector axis, double theta) {
        for (GameSurface surface : surfaces) surface.rotateBy(focus, axis, theta);
    }

    public void shiftBy(GameVector shiftAmount) {
        for (GameSurface surface : surfaces) surface.shiftBy(shiftAmount);
    }

    public void scaleBy(GameVector focus, double percentChange) {
        for (GameSurface surface : surfaces) surface.scaleBy(focus, percentChange);
    }
}
