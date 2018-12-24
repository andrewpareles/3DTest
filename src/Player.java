import javafx.util.Pair;

import java.awt.*;
import java.awt.event.*;

import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.lang.Math.*;

public class Player implements KeyListener, MouseMotionListener, MouseListener {

    //changing d doesn't alter anything unless VIEW_WIDTH and VIEW_HEIGHT are held constant (not dependent on angle)
    private final double d = 1;
    private double f;

    private final double viewAngleWidth = 50 * PI / 180;
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
    // velocity DOES NOT incorporate walking speed
    private GameVector velocity = new GameVector(0, 0, 0);

    private boolean W = false, A = false, S = false, D = false, Q = false, E = false;

    private final double WALK_SPEED = 5;
    private final double RUN_SPEED = 100;

    private boolean isRunning = false;

    private final double xSens = 5;
    private final double ySens = 5;

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

    public GameVector getTotalVelocity() {
        GameVector w = new GameVector(view.x(), view.y(), 0).normalize();
        GameVector a = w.rotateBy(new GameVector(0, 0, 0), new GameVector(0, 0, 1), Math.PI / 2);
        GameVector s = w.negate();
        GameVector d = a.negate();
        GameVector q = new GameVector(0, 0, 1);
        GameVector e = q.negate();

        double speed = isRunning ? RUN_SPEED : WALK_SPEED;
        w = W ? w.times(speed) : GameVector.ZERO;
        a = A ? a.times(speed) : GameVector.ZERO;
        s = S ? s.times(speed) : GameVector.ZERO;
        d = D ? d.times(speed) : GameVector.ZERO;
        q = Q ? q.times(speed) : GameVector.ZERO;
        e = E ? e.times(speed) : GameVector.ZERO;

        return velocity.plus(w).plus(a).plus(s).plus(d).plus(q).plus(e);
    }

    private void setVelocity(GameVector velocity) {
        this.velocity = velocity;
    }


    private void setKeyPressed(char key, boolean pressed) {
        switch (Character.toLowerCase(key)) {
            case 'w':
                W = pressed;
                break;
            case 'a':
                A = pressed;
                break;
            case 's':
                S = pressed;
                break;
            case 'd':
                D = pressed;
                break;
            case 'q':
                Q = pressed;
                break;
            case 'e':
                E = pressed;
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        if (e.getKeyCode() == VK_SHIFT) isRunning = true;
        else setKeyPressed(key, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char key = e.getKeyChar();
        if (e.getKeyCode() == VK_SHIFT) isRunning = false;
        else setKeyPressed(key, false);
    }

    private Robot initializeRobot() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (Exception e) {
            return r;
        }
        return r;

    }

    private Robot robot = initializeRobot();


    @Override
    public void keyTyped(KeyEvent e) {

    }

    private int prevX = 0, prevY = 0, dX, dY;


    @Override
    public void mouseDragged(MouseEvent e) {
//        int x = e.getX();
//        int y = e.getY();
//        dX = x - prevX;
//        dY = y - prevY;
//        prevX = x;
//        prevY = y;
//        changeThetaBy(-dX * xSens / Game.WIDTH);
//        //this is width on purpose v
//        changePhiBy(-dY * ySens / Game.WIDTH);

    }

    @Override
    public void mousePressed(MouseEvent e) {
//        prevX = e.getX();
//        prevY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int xmid = Game.WIDTH / 2;
        int ymid = Game.HEIGHT / 2;
        dX = e.getXOnScreen() - xmid;
        dY = e.getYOnScreen() - ymid;

        changeThetaBy(-dX * xSens / 1000);
        changePhiBy(-dY * ySens / 1000);

        robot.mouseMove(xmid, ymid);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
