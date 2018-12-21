import javafx.util.Pair;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.lang.Math.*;

public class Player implements MouseListener, KeyListener {

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

    private double WALK_SPEED = 1;
    private double RUN_SPEED = 2;

    private GameVector velocity = new GameVector(0,0,0);

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

    public GameVector getVelocity() {
        return velocity;
    }

    private void setVelocity(GameVector velocity) {
        this.velocity = velocity;
    }

    @Override
    public void keyPressed(KeyEvent e) {
// USE THIS
        //TODO REVAMP SO THAT MOVING LEFT MAKES SENSE IF CHANGE CAMERA ANGLE (ENUM MAYBE, L R F B)
        char key = e.getKeyChar();
        double speed = Character.isLowerCase(key) ? RUN_SPEED : WALK_SPEED;
        GameVector view_proj_to_xy = new GameVector(view.x(), view.y(), 0).normalize();
        switch (Character.toLowerCase(key)) {
            case 'w':
                setVelocity(getVelocity().plus(view_proj_to_xy));
                break;
            case 's':
                setVelocity(getVelocity().plus(view_proj_to_xy.negate()));
                break;

                //TODO FIX ROTATE BY AND THIS WILL WORK. NEXT DO MOUSE MOVEMENTS, RIGHT CLICK AND DRAG TO PIVOT CAMERA
            case 'a':
                GameVector view_rotated_90_left = view_proj_to_xy.rotateBy(new GameVector(0,0,0),  new GameVector(0,0,1), 90);
                setVelocity(getVelocity().plus(view_rotated_90_left));
                break;
            case 'd':
                GameVector view_rotated_90_right = view_proj_to_xy.rotateBy(new GameVector(0,0,0),  new GameVector(0,0,1), 90);
                setVelocity(getVelocity().plus(view_rotated_90_right));
                break;



        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
// USE THIS
        // LITERALLY JUST SUBTRACT
//        if (e.getKeyChar() == 'q') q = false;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        e.getButton() == 1 || e.getButton() == 2
// USE THIS
    }

    @Override
    public void keyTyped(KeyEvent e) {
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
