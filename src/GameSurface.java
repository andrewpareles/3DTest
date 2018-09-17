import java.util.ArrayList;
import java.util.Arrays;

public class GameSurface {
    private ArrayList<GameVector> surfaceBounds = new ArrayList<>();

    public GameSurface(GameVector... vectors) {
        surfaceBounds.addAll(Arrays.asList(vectors));
    }

    public GameVector getPoint(int n) {
        return surfaceBounds.get(n);
    }

    public int getNumPoints() {
        return surfaceBounds.size();
    }

    public GameVector getAverageSurfaceVector() {
        GameVector sum = new GameVector(0, 0, 0);
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
