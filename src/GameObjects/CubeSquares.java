package GameObjects;

import GameFiles.GameObject;
import GameFiles.GameSurface;
import GameFiles.GameVector;

import java.awt.*;

public class CubeSquares extends GameObject {

    //s is size, x y z is coords
    public CubeSquares(double s, double x, double y, double z) {
// wall, xs position
        super(
//                GameSurface.createSurface(new Color(120, 32, 6),
//                        new GameVector(x, y, z),
//                        GameVector.X,
//                        new GameVector(x + s, y + s, z + s),
//                        4
//                ),
//                GameSurface.createSurface(new Color(120, 32, 6),
//                        new GameVector(x, y, z),
//                        GameVector.X,
//                        new GameVector(x - s, y + s, z + s),
//                        4
//                ),
//floor, zs position
                GameSurface.createSurface(new Color(0, 17, 255),
                        new GameVector(x, y, z),
                        GameVector.Z,
                        new GameVector(x + s, y + s, z + s),
                        4)
//                ),
//                GameSurface.createSurface(new Color(0, 17, 255),
//                        new GameVector(x, y, z),
//                        GameVector.Z,
//                        new GameVector(x + s, y + s, z - s),
//                        4
//                ),
//
////wall, ys position
//                GameSurface.createSurface(new Color(21, 255, 0),
//                        new GameVector(x, y, z),
//                        GameVector.Y,
//                        new GameVector(x + s, y + s, z + s),
//                        4
//                ),
//                GameSurface.createSurface(new Color(21, 255, 0),
//                        new GameVector(x, y, z),
//                        GameVector.Y,
//                        new GameVector(x + s, y - s, z + s),
//                        4
//                )
        );
    }
}