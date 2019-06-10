package GameObjects;

import GameFiles.GameObject;
import GameFiles.GameSurface;
import GameFiles.GameVector;

import java.awt.*;

public class TriangularPyramid extends GameObject {
    public TriangularPyramid(double x, double y, double z, double s) {
        super(

                GameSurface.createSurface(new Color(0, 224, 228),
                        new GameVector(2 * s, 1.5 * s, 1.5 * s),
                        new GameVector(4 * s, 0, 0),
                        new GameVector(2 * s, 3 * s, 0)
                ),
                GameSurface.createSurface(new Color(120, 32, 6),
                        new GameVector(2 * s, 1.5 * s, 1.5 * s),
                        GameVector.ZERO(),
                        new GameVector(2 * s, 3 * s, 0)
                ),
                GameSurface.createSurface(new Color(0, 255, 36),
                        new GameVector(2 * s, 1.5 * s, 1.5 * s),
                        new GameVector(4 * s, 0, 0),
                        GameVector.ZERO()
                )

        );

        this.shiftBy(new GameVector(x, y, z));
    }

}
