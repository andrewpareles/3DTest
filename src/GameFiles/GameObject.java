package GameFiles;


import java.util.ArrayList;
import java.util.Arrays;

//a collection of surfaces
public class GameObject {
    // any number of surfaces
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

    public ArrayList<GameSurface> getSurfaces() {
        return surfaces;
    }

    public GameVector getCenterOfObject() {
        return Helpers.AverageVector.getAverageObjectVector(this.surfaces);
    }

    public void shiftBy(GameVector shiftAmount) {
        for (int i = 0; i < surfaces.size(); i++)
            surfaces.get(i).shiftBy(shiftAmount);
    }

    public void rotateBy(GameVector focus, GameVector axis, double theta) {
        for (int i = 0; i < surfaces.size(); i++)
            surfaces.get(i).rotateBy(focus, axis, theta);
    }

    public void scaleBy(GameVector focus, double percentChange) {
        for (int i = 0; i < surfaces.size(); i++)
            surfaces.get(i).scaleBy(focus, percentChange);
    }
}
