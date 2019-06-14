package GameFiles;

import static java.lang.Math.*;

public class GameVector {
    private double x, y, z;

    private static final GameVector ZERO = new GameVector(0, 0, 0);
    private static final GameVector X = new GameVector(1, 0, 0);
    private static final GameVector Y = new GameVector(0, 1, 0);
    private static final GameVector Z = new GameVector(0, 0, 1);

    public static GameVector ZERO() {
        return new GameVector(ZERO);
    }

    public static GameVector X() {
        return new GameVector(X);
    }

    public static GameVector Y() {
        return new GameVector(Y);
    }

    public static GameVector Z() {
        return new GameVector(Z);
    }

    public GameVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // instantiate this as a copy of [copy]
    public GameVector(GameVector copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }

    // copies attributes from newVector to this
    public void updateTo(GameVector newVector) {
        x = newVector.x;
        y = newVector.y;
        z = newVector.z;
    }

    // copies attributes from newVector to this
    public void updateTo(double x, double y, double z) {
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

    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }


    public boolean isParallelTo(GameVector v) {
        return this.normal().equals(v.normal()) || this.normal().equals(v.normal().negative());
    }

    public GameVector times(double t) {
        GameVector v = new GameVector(t * x, t * y, t * z);
        return v;
    }

    public GameVector plus(GameVector v) {
        GameVector v2 = new GameVector(
                x + v.x,
                y + v.y,
                z + v.z
        );
        return v2;
    }

    public GameVector minus(GameVector v) {
        return plus(v.negative());
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

    public GameVector normal() {
        return this.times(1 / this.length());
    }

    public GameVector negative() {
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

    public GameVector scaledBy(GameVector focus, double percentChange) {
        return this.minus(focus).times(1 + percentChange).plus(focus);
    }

    public void scaleBy(GameVector focus, double percentChange) {
        updateTo(scaledBy(focus, percentChange));
    }

    // Project t onto v, return (t . v ) v
    // Requires v.length() == 1
    public GameVector projectedOnUnit(GameVector v) {
        return v.times(this.dot(v));
    }

    // Project t onto v, return (t . v / v . v) v
    // Requires v.length() != 0
    public GameVector projectedOn(GameVector v) {
        return v.times(this.dot(v) / (v.lengthSquared()));
    }

    //axis is the axis vector, focus is the point about which rotation occurs
    public GameVector rotatedBy(GameVector focus, GameVector axis, double theta) {

        GameVector a = axis.normal();

        GameVector w = this.minus(focus);
        //if point is on axis then do nothing
//        if (w.lengthSquared() == 0 || w.isParallelTo(a))
//            return this;

        //component not being rotated, will add it back later
        GameVector b = w.projectedOnUnit(a);

        //component in plane of rotation
        GameVector what = w.minus(b);

        //perpendicular component also in plane of rotation
        GameVector vhat = w.cross(a.negative()).normal().times(what.length());

        return what.times(cos(theta))
                .plus(vhat.times(sin(theta)))
                .plus(focus)
                .plus(b);
    }

    public void rotateBy(GameVector focus, GameVector axis, double theta) {
        updateTo(rotatedBy(focus, axis, theta));
    }

    public void shiftBy(GameVector shift) {
        updateTo(plus(shift));
    }

    public String toString() {
        return "x:\t" + x + "\ny:\t" + y + "\nz:\t" + z + "\n";
    }

    public boolean equals(Object o) {
        if (!(o instanceof GameVector)) return false;
        return ((GameVector) o).x == x && ((GameVector) o).y == y && (((GameVector) o).z == z);
    }

}
