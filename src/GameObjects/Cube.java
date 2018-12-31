package GameObjects;

import GameFiles.*;

import java.awt.*;

public class Cube extends GameObject {

    //s is size, x y z is coords
    public Cube(double s, double x, double y, double z) {
// wall, xs position
        super(
                new GameSurface(
                        new GameVector(s + x, -s + y, -s + z),
                        new GameVector(s + x, -s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, s + y, -s + z)
                ).setColor(new Color(120, 32, 6)),
                new GameSurface(
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(-s + x, s + y, -s + z)
                ).setColor(new Color(120, 32, 6)),
                //floor, zs position
                new GameSurface(
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, -s + y, s + z)
                ).setColor(new Color(0, 17, 255)),
                new GameSurface(
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, s + y, -s + z),
                        new GameVector(s + x, s + y, -s + z),
                        new GameVector(s + x, -s + y, -s + z)
                ).setColor(new Color(0, 17, 255)),
                // wall, ys position
                new GameSurface(
                        new GameVector(-s + x, s + y, -s + z),
                        new GameVector(-s + x, s + y, s + z),
                        new GameVector(s + x, s + y, s + z),
                        new GameVector(s + x, s + y, -s + z)
                ).setColor(new Color(21, 255, 0)),
                new GameSurface(
                        new GameVector(-s + x, -s + y, -s + z),
                        new GameVector(-s + x, -s + y, s + z),
                        new GameVector(s + x, -s + y, s + z),
                        new GameVector(s + x, -s + y, -s + z)
                ).setColor(new Color(21, 255, 0))
        );
    }
}
