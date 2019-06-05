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
    // theta is in (0, 2pi)
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
    private double scrollSensitivity = 5;


    private final double WALK_SPEED = 15;
    private final double CROUCH_SPEED = 5;

    private boolean isCrouching = false;

    private final double xSens = 5;
    private final double ySens = 5;

    private final double verticalCutoffAngle = PI / 2 - 1 * PI / 180;

    //RI: Player can never look totally up or totally down
    Player() {
        updatef();
        updateView();
    }

    public int getCrosshairLength() {
        return crosshairLength;
    }

    public int getCrosshairWidth() {
        return crosshairWidth;
    }

    public Color getCrosshairColor() {
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
        // theta is in [0, 2pi)
        while (theta >= 2 * Math.PI) theta -= 2 * Math.PI;
        while (theta < 0) theta += 2 * Math.PI;

        while (phi > Math.PI / 2) phi -= Math.PI;
        while (phi < -Math.PI / 2) phi += Math.PI;


        view = new GameVector(f * cos(theta), f * sin(theta), d * sin(phi));
    }

    public void setView(double theta, double phi) {
        this.theta = theta;
        this.phi = phi;
        updatef();
        updateView();
    }

    public void setView(GameVector view) {
        GameVector normal = view.normal();
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

    public GameVector getView() {
        return view;
    }

    //Pt is the coordinates of the vector (yes, starting from the origin)
    public Pair<Integer, Integer> getCoordinatesOfPointInPlaneOnScreen(GameVector Pt) {
        GameVector v = view;
        GameVector P = position;

        // Now determine how to draw Pt on the screen
        // orthogonal vectors, H is horizontal ("x-axis"), V is vertical ("y-axis")
        GameVector H = new GameVector(-v.y(), v.x(), 0).negative().normal();
        GameVector V = H.cross(v).normal();

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
        return new Pair<>(X, Y);
    }


    //returns true if in front of player, false otherwise, and then coords.
    public Pair<Boolean, Pair<Integer, Integer>> getCoordinatesOfPointOnScreen(GameVector P1) {
        GameVector v = view;
        GameVector P = position;
        GameVector PtoP1 = P1.minus(P);

        double t = v.dot(v) / v.dot(PtoP1);

        //if behind or in you
        boolean pointIsBehindPlayer = t <= 0;

        //point on plane:
        GameVector Pt = P.plus(PtoP1.times(t));

        Pair<Integer, Integer> coordinatesOnScreen = getCoordinatesOfPointInPlaneOnScreen(Pt);

        return new Pair<>(!pointIsBehindPlayer, coordinatesOnScreen);
    }

    public GameVector getTotalVelocity() {
        GameVector w = (new GameVector(view.x(), view.y(), 0)).normal();
        System.out.println("w1:" + w);

        GameVector a = w.rotatedBy(GameVector.ZERO, new GameVector(0, 0, 1), Math.PI / 2);
        GameVector s = w.negative();
        GameVector d = a.negative();
        GameVector q = GameVector.Z;
        GameVector e = q.negative();
        GameVector v = view;
        System.out.println("w2:" + w);

        double speed = isCrouching ? CROUCH_SPEED : WALK_SPEED;
        System.out.println("speed" + speed);

        System.out.println("w*speed" + w.times(speed));
        System.out.println("zero" + GameVector.ZERO);


        w = W ? w.times(speed) : GameVector.ZERO;
        a = A ? a.times(speed) : GameVector.ZERO;
        s = S ? s.times(speed) : GameVector.ZERO;
        d = D ? d.times(speed) : GameVector.ZERO;
        q = Q ? q.times(speed) : GameVector.ZERO;
        e = E ? e.times(speed) : GameVector.ZERO;
        v = v.times(scrollCount * speed * scrollSensitivity);
        System.out.println("w3:" + w);


        System.out.println("vel:" + velocity.length());
        System.out.println("w:" + w.length());
        System.out.println("a:" + a.length());
        System.out.println("s:" + s.length());
        System.out.println("d:" + d.length());
        System.out.println("q:" + q.length());
        System.out.println("e:" + e.length());
        System.out.println("v:" + v.length());

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

    private double dX, dY, prevX, prevY;

    @Override
    public void mousePressed(MouseEvent e) {
        prevX = e.getXOnScreen();
        prevY = e.getYOnScreen();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        int xmid = Game.WIDTH / 2;
//        int ymid = Game.HEIGHT / 2;
//        robot.mouseMove(xmid, ymid);
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
    private int xmid = Game.WIDTH / 2;
    private int ymid = Game.HEIGHT / 2;

    private void updateMouse(MouseEvent e, boolean moveTDragF) {
        int x = e.getX();
        int y = e.getY();
        if (moveTDragF) {
            dX = x - xmid;
            dY = y - ymid;
            robot.mouseMove(xmid, ymid);
        } else {
            dX = x - prevX;
            dY = y - prevY;
            prevX = x;
            prevY = y;
        }
        double dTheta = -dX * xSens / Game.WIDTH;
        double dPhi = -dY * ySens / Game.WIDTH; //this is width on purpose

        changeThetaBy(dTheta);
        changePhiBy(dPhi);

        // teleport rotation condition
//        if (dTheta > 0 && 0 < theta + dTheta && theta + dTheta < Math.PI / 2) changeThetaBy(Math.PI / 2);
//        if (dTheta < 0 && 0 < theta + dTheta && theta + dTheta < Math.PI / 2) changeThetaBy(-Math.PI / 2);

    }
    @Override
    public void mouseDragged(MouseEvent e) {
//        updateMouse(e, false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMouse(e, true);
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
