package GameFiles;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

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

    private GameObject addAction(Runnable r) {
        if (action == null) action = r;
        else {
            Runnable old_action = action;
            action = () -> {old_action.run(); r.run();}; // chaining
        }

        return this;
    }

    public void run() {
        if (action != null) action.run();
    }

    //********* ACTIONS: **********
    public GameObject spin(GameVector axis, double rotationsPerSecond) {
        addAction(() -> {
            GameVector ctrOfClosest = this.getCenterOfObject();
            this.rotateBy(ctrOfClosest, axis, Math.toRadians(rotationsPerSecond * 360d) / Game.fps);
        });

        return this;
    }

    public GameObject grow(double percent100PerSecond) {
        addAction(() -> {
            GameVector ctrOfClosest = this.getCenterOfObject();
            this.scaleBy(ctrOfClosest, (percent100PerSecond / 100d) / Game.fps);
        });

        return this;
    }

    // repels proportionally to scaleConst * (1 / distance^inversePow)
    public GameObject repelFrom(GameVector repel, double scaleConst, double inversePow) {
        addAction(() -> {
            GameVector distToClosest = this.getCenterOfObject().minus(repel);
            GameVector amt = distToClosest.times(scaleConst / Math.pow(distToClosest.lengthSquared(), inversePow / 2));
            this.shiftBy(amt);
        });

        return this;
    }

    //(this) -> {...}, where this is this GameObject
    // NOTE: The two following lines are semantically equivalent
    // o.customAction(a-> a.grow(-5))
    // o.grow(-5)
    public GameObject customAction(Consumer<GameObject> c) {
        addAction(() -> c.accept(this));
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
