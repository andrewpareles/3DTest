import javafx.util.Pair;

import static java.lang.Math.*;

public class Player {

    //changing d doesn't alter anything unless VIEW_WIDTH and VIEW_HEIGHT are held constant (not dependent on angle)
    private final double d = 1;
    private double f;

    private final double viewAngleWidth = 80 * PI / 180;
    private final double viewAngleHeight = atan((double) Game.HEIGHT / Game.WIDTH * tan(viewAngleWidth));
    private final double VIEW_WIDTH = 2 * d * tan(viewAngleWidth);
    private final double VIEW_HEIGHT = 2 * d * tan(viewAngleHeight);

    private final double thetaDefault = 0;
    private double theta = thetaDefault;

    private final double phiDefault = 0;
    private double phi = phiDefault;

    private final GameVector positionDefault = new GameVector(0, 0, 0);
    private GameVector position = positionDefault;

    private GameVector view;

    Player() {
        updatef();
        updateView();
    }

    private void updatef() {
        f = d * sqrt(1 - sin(phi) * sin(phi));
    }

    private void updateView() {
        view = new GameVector(f * cos(theta), f * sin(theta), d * sin(phi));
    }

    public void setView(double theta, double phi) {
        this.theta = theta;
        this.phi = phi;
        updatef();
        updateView();
    }

    public void setView(GameVector view) {
        GameVector normal = view.normalize();
        double asin = asin(normal.y() / f);
        double acos = acos(normal.x() / f);

        double theta = (asin > 0 && acos > 0) ? acos : 2 * PI - acos;
        double phi = asin(normal.z() / d);

        setView(theta, phi);
    }

    public void changePhiBy(double changePhi) {
        setView(theta, phi + changePhi);
    }

    public void changeThetaBy(double changeTheta) {
        setView(theta + changeTheta, phi);
    }

    public void moveInDirection(GameVector direction) {
        position = position.plus(direction);
    }

    public GameVector getPosition() {
        return position;
    }

    public Pair<Integer, Integer> getCoordinatesOfPointOnScreen(GameVector P1) {

        GameVector v = view;
        GameVector P = position;
        GameVector PtoP1 = P1.minus(P);

        double t = ((v.x() * v.x()) + (v.y() * v.y()) + (v.z() * v.z())) /
                ((v.x() * PtoP1.x()) + (v.y() * PtoP1.y()) + (v.z() * PtoP1.z()));

        //if behind or in you
        if (t <= 0) return null;

        //point on plane:
        GameVector Pt = P.plus(PtoP1.times(t));

        GameVector H = new GameVector(-v.y(), v.x(), 0).negate().normalize();
        GameVector V = (v.z() == 0) ?
                new GameVector(0, 0, 1) :
                H.cross(v).normalize();

        GameVector wL = v.plus(P).minus(H.times(VIEW_WIDTH / 2));
        GameVector wR = v.plus(P).plus(H.times(VIEW_WIDTH / 2));
        GameVector wB = v.plus(P).minus(V.times(VIEW_HEIGHT / 2));
        GameVector wT = v.plus(P).plus(V.times(VIEW_HEIGHT / 2));

        double dL = V.cross(Pt.minus(wL)).length();
        double dR = V.cross(Pt.minus(wR)).length();
        double dB = H.cross(Pt.minus(wB)).length();
        double dT = H.cross(Pt.minus(wT)).length();

        double x1;
        if (dR <= dL) x1 = dL - VIEW_WIDTH / 2;
        else x1 = VIEW_WIDTH / 2 - dR;

        double y1;
        if (dT <= dB) y1 = dB - VIEW_HEIGHT / 2;
        else y1 = VIEW_HEIGHT / 2 - dT;

        double x = x1 * Game.WIDTH / VIEW_WIDTH;
        double y = y1 * Game.HEIGHT / VIEW_HEIGHT;

        int X = Game.toComputerCoordinateSystemX(x);
        int Y = Game.toComputerCoordinateSystemY(y);

        return new Pair<>(X, Y);

    }


}
