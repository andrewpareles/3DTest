package GameFiles;

import static java.lang.Math.*;

public class GameVector {
    private double x, y, z;

    public static final GameVector ZERO = new GameVector(0, 0, 0);
    public static final GameVector X = new GameVector(1, 0, 0);
    public static final GameVector Y = new GameVector(0, 1, 0);
    public static final GameVector Z = new GameVector(0, 0, 1);


    public GameVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dot(GameVector v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public GameVector cross(GameVector v) {
        return new GameVector(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public GameVector times(double t) {
        return new GameVector(t * x, t * y, t * z);
    }


    public GameVector plus(GameVector v) {
        return new GameVector(
                x + v.x,
                y + v.y,
                z + v.z
        );
    }

    public GameVector minus(GameVector v) {
        return new GameVector(
                x - v.x,
                y - v.y,
                z - v.z
        );
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return this.dot(this);
    }

    public double distanceTo(GameVector v) {
        return this.minus(v).length();
    }

    public GameVector normalize() {
        return this.times(1 / length());
    }

    public GameVector negate() {
        return new GameVector(-x, -y, -z);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public GameVector scaleBy(GameVector focus, double percentChange) {
        return this.minus(focus).times(1 + percentChange)
                .plus(focus);
    }

    // Project t onto v, return (t . v / v . v) v
    // Requires v.length() == 1
    public GameVector projectOnUnit(GameVector v) {
        return v.times(this.dot(v));
    }

    // Project t onto v, return (t . v / v . v) v
    // Requires v.length() != 0
    public GameVector projectOn(GameVector v) {
        return v.times(this.dot(v) / (v.lengthSquared()));
    }

    //axis is the axis vector, focus is the point about which rotation occurs
    public GameVector rotateBy(GameVector focus, GameVector axis, double theta) {

        GameVector a = axis.normalize();

        //if point is on axis then do nothing
        GameVector w = this.minus(focus);
        if (w.lengthSquared() == 0 ||
                w.normalize().equals(a) ||
                w.normalize().negate().equals(a))
            return this;

        GameVector b = w.projectOnUnit(a);

        GameVector what = w.minus(b);
        GameVector vhat = w.cross(a.negate()).normalize().times(what.length());

        return b.plus(what.times(cos(theta)))
                .plus(vhat.times(sin(theta)));

    }

    public boolean equals(Object o) {
        if (!(o instanceof GameVector)) return false;

        return ((GameVector) o).x() == x && ((GameVector) o).y() == y && (((GameVector) o).z() == z);
    }

}
