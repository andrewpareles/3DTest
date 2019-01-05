package GameFiles;

import javafx.util.Pair;

import java.awt.*;
import java.awt.event.*;

import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.lang.Math.*;

public class Player implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

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
    // phi is from -pi/2 to pi/2
    private double phi = phiDefault;

    private final GameVector positionDefault = GameVector.ZERO;
    private GameVector position = positionDefault;

    //view is always normal
    private GameVector view;
    // velocity DOES NOT incorporate walking speed
    private GameVector velocity = GameVector.ZERO;

    private boolean W = false, A = false, S = false, D = false, Q = false, E = false;

    private int crosshairLength = 30;
    private int crosshairWidth = 2;
    private Color crosshairColor = new Color(0, 224, 228);

    // + for up, - for down
    private int scrollCount = 0;
    private double scrollSensitivity = 2;


    private final double WALK_SPEED = 10;
    private final double CROUCH_SPEED = 1;

    private boolean isCrouching = false;

    private final double xSens = 5;
    private final double ySens = 5;

    private final double verticalCutoffAngle = PI / 2 - 1 * PI / 180;

    //RI: Player can never look totally up or totally down
    Player() {
        updatef();
        updateView();
    }

    public int getCrosshairLength(){
        return  crosshairLength;
    }

    public int getCrosshairWidth(){
        return crosshairWidth;
    }
    public Color getCrosshairColor(){
        return crosshairColor;
    }

    public void move(double fps) {
        moveInDirection(getTotalVelocity().times(1 / fps));
        scrollCount = 0;
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
        double newPhi = phi + changePhi;
        if (newPhi > verticalCutoffAngle) newPhi = verticalCutoffAngle;
        else if (newPhi < -verticalCutoffAngle) newPhi = -verticalCutoffAngle;
        setView(theta, newPhi);
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

    public Pair<Boolean, Pair<Integer, Integer>> getCoordinatesOfPointOnScreen(GameVector P1) {

        GameVector v = view;
        GameVector P = position;
        GameVector PtoP1 = P1.minus(P);

        double t = v.dot(v) / v.dot(PtoP1);
        //if behind or in you
        boolean pointIsBehindPlayer = t <= 0;

        //point on plane:
        GameVector Pt = P.plus(PtoP1.times(t));

        // Now determine how to draw Pt on the screen
        // orthogonal vectors, H is horizontal ("x-axis"), V is vertical ("y-axis")
        GameVector H = new GameVector(-v.y(), v.x(), 0).negate().normalize();
        GameVector V = H.cross(v).normalize();

        // point that V and H are based on, "center" of screen
        GameVector vPlusP = v.plus(P);

        GameVector wL = vPlusP.minus(H.times(VIEW_WIDTH / 2));
        GameVector wR = vPlusP.plus(H.times(VIEW_WIDTH / 2));
        GameVector wB = vPlusP.minus(V.times(VIEW_HEIGHT / 2));
        GameVector wT = vPlusP.plus(V.times(VIEW_HEIGHT / 2));

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

        return new Pair<>(pointIsBehindPlayer, new Pair<>(X, Y));
    }

    public GameVector getTotalVelocity() {
        GameVector w = new GameVector(view.x(), view.y(), 0).normalize();
        GameVector a = w.rotateBy(GameVector.ZERO, new GameVector(0, 0, 1), Math.PI / 2);
        GameVector s = w.negate();
        GameVector d = a.negate();
        GameVector q = GameVector.Z;
        GameVector e = q.negate();
        GameVector v = view;

        double speed = isCrouching ? CROUCH_SPEED : WALK_SPEED;
        w = W ? w.times(speed) : GameVector.ZERO;
        a = A ? a.times(speed) : GameVector.ZERO;
        s = S ? s.times(speed) : GameVector.ZERO;
        d = D ? d.times(speed) : GameVector.ZERO;
        q = Q ? q.times(speed) : GameVector.ZERO;
        e = E ? e.times(speed) : GameVector.ZERO;
        v = v.times(scrollCount * speed * scrollSensitivity);
        return velocity.plus(w).plus(a).plus(s).plus(d).plus(q).plus(e).plus(v);
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
        if (e.getKeyCode() == VK_SHIFT) isCrouching = true;
        else setKeyPressed(key, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char key = e.getKeyChar();
        if (e.getKeyCode() == VK_SHIFT) isCrouching = false;
        else setKeyPressed(key, false);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //- for up, + for down
        if (e.getWheelRotation() < 0) scrollCount += 1; //UP
        else scrollCount -= 1;//DOWN
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    double dX, dY, prevX, prevY;

    @Override
    public void mousePressed(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        int xmid = Game.WIDTH / 2;
        int ymid = Game.HEIGHT / 2;

        robot.mouseMove(xmid, ymid);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        dX = x - prevX;
        dY = y - prevY;
        prevX = x;
        prevY = y;
        changeThetaBy(-dX * xSens / Game.WIDTH);
        //this is width on purpose v
        changePhiBy(-dY * ySens / Game.WIDTH);
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
    public void mouseMoved(MouseEvent e) {
//        int xmid = Game.WIDTH / 2;
//        int ymid = Game.HEIGHT / 2;
//        int dX = e.getXOnScreen() - xmid;
//        int dY = e.getYOnScreen() - ymid;
//
//        changeThetaBy(-dX * xSens / 1000);
//        changePhiBy(-dY * ySens / 1000);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
