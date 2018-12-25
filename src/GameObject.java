import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

//a collection of surfaces
public abstract class GameObject {
    private ArrayList<GameSurface> surfaces = new ArrayList<>();

    public GameObject(GameSurface... s) {
        surfaces.addAll(Arrays.asList(s));

    }

    public ArrayList<GameSurface> getSurfaces() {
        return surfaces;
    }

    public GameVector getAverageSurfaceVector() {
        GameVector sum = GameVector.ZERO;
        for (GameSurface s : surfaces)
            sum = sum.plus(s.getAverageSurfaceVector());
        sum = sum.times(1.0 / surfaces.size());
        return sum;
    }
}
