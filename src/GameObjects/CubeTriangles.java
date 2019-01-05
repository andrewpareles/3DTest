package GameObjects;

import GameFiles.*;

import java.awt.*;

public class CubeTriangles extends GameObject {

    //s is size, x y z is coords
    public CubeTriangles(double s, double x, double y, double z) {
// wall, xs position
        super(
                GameSurface.createSurface(new Color(120, 32, 6),
                        new GameVector(s + x, -s + y, -s + z),
                        new GameVector(s + x, -s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, s + y, -s + z))
                ,
                GameSurface.createSurface(new Color(120, 32, 6),
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(-s + x, s + y, -s + z)
                ),
                //floor, zs position
                GameSurface.createSurface(new Color(0, 17, 255),
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, -s + y, s + z)
                ),
                GameSurface.createSurface(new Color(0, 17, 255),
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, s + y, -s + z),
                        new GameVector(s + x, s + y, -s + z),
                        new GameVector(s + x, -s + y, -s + z)
                ),
                // wall, ys position
                GameSurface.createSurface(new Color(21, 255, 0),
                        new GameVector(-s + x, s + y, -s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, s + y, -s + z)
                ),
                GameSurface.createSurface(new Color(21, 255, 0),
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(s + x, -s + y, s + z),
                        new GameVector(s + x, -s + y, -s + z)
                )
        );
    }

}
