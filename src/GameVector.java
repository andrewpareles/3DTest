import static java.lang.Math.*;

public class GameVector {
    private double x, y, z;

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
        return x * x + y * y + z * z;
    }

    public double distance(GameVector v) {
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

    //axis is the axis vector, point is the point about which rotation occurs
    public GameVector rotateBy(GameVector focus, GameVector axis, double theta) {

        GameVector a = axis.normalize();

        //if point is on axis then do nothing
        GameVector q = this.minus(focus);
        if (q.length() == 0 ||
                q.normalize().equals(a) ||
                q.normalize().negate().equals(a))
            return this;

        GameVector w = this.minus(focus);


        //TODO TRACE BACK W to the plane and use THAT as wproj
        double x = w.x(), y = w.y(), z = w.z();
        if (w.z() != 0) z = (a.x() * x + a.y() * y) / -a.z();
        else if (w.y() != 0) y = (a.x() * x + a.z() * z) / -a.y();
        else if (w.x() != 0) x = (a.y() * y + a.z() * z) / -a.x();

        //w projected onto the plane perpendicular to a
        GameVector wproj = new GameVector(x, y, z);
        GameVector wheight = w.minus(wproj);

        GameVector what = wproj.normalize();
        GameVector h = a.cross(what);

        return what.times(cos(theta))
                .plus(h.times(sin(theta)))
                .times(wproj.length())
                .plus(wheight)
                .plus(focus);

    }

    public boolean equals(Object o) {
        if (!(o instanceof GameVector)) return false;

        return ((GameVector) o).x() == x && ((GameVector) o).y() == y && (((GameVector) o).z() == z);
    }

}
