package GameFiles;

import GameObjects.CubeSquares;
import GameObjects.TriangularPyramid;
import Helpers.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static java.lang.Math.round;


public class Game extends JFrame implements ActionListener {

    final static int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width - 50;
    final static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - 50;
    final static double fps = 60;

    private Player p = new Player();

    private LinkedList<GameObject> objects = new LinkedList<>(Arrays.asList(
            new CubeSquares(5, 0, 0, 0),
            new TriangularPyramid(20, 20, 20, 2),
            new CubeSquares(1, 6, 6, 6),
            new GameObject(GameSurface.createSurface(new Color(0, 17, 255), new GameVector(12, 32, 8), new GameVector(18, 32, 6), new GameVector(10, 36, 7), 25)),
            new CubeSquares(1, 6, 6, -6),

            new CubeSquares(1, 6, -6, 6).addAction(t -> t.spin(p.getView(), .1)),
            new CubeSquares(1, 6, -6, -6).addAction(t -> t.shiftBy(new GameVector(Math.random() - .5, Math.random() - .5, Math.random() - .5))).addAction(t->t.spin(GameVector.Z(), .1)),
            new CubeSquares(1, -6, 6, 6).addAction(t -> t.grow(-5)),
            new CubeSquares(1, -6, 6, -6).addAction(t -> t.spin(GameVector.Z(), .1)),
            new CubeSquares(1, -6, -6, 6).addAction(t -> t.repelFrom(p.getPosition(), -.3, 2)).addAction(t-> t.spin(t.getCenterOfObject().minus(p.getPosition()), 3)),
            new CubeSquares(1, -6, -6, -6)
    ));


    private Game() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);

        setTitle("3DGame");
        setVisible(true);

        addKeyListener(p);
        addMouseListener(p);
        addMouseMotionListener(p);
        addMouseWheelListener(p);

        Timer timer = new Timer((int) (1000d / fps), this);
        timer.start();

        p.moveInDirection(new GameVector(-20, 5, 8));
        p.setView(Math.toRadians(90), Math.toRadians(-60));
    }

    public static void main(String[] args) {
        new Game();
    }

    //NOTE: speed/fps = distance per frame
    //NOTE: percent/fps = percent per frame

    //todo make these checkboxes in an escape button press
    //todo organize items by name, hashmap
    //todo label objects by name, add vector pointer (cylinder)
    //todo intersection math: 1) camera-object 2) two different objects
    //todo fix mouse jump at first, esc


    public void actionPerformed(ActionEvent e) {
//        if (p.ESC) return;

        p.move(fps);

        for (GameObject o : objects)
            o.run();


//        GameObject closest = objects.getLast();
//        rotateGameObject(closest, p.getView(), .1);
//        growGameObject(closest, -5);
//        repelGameObject(closest, .3, 2);


        repaint();
    }

    @Override
    public void repaint() {
        drawObjects();
    }

    private void drawObjects() {
        Image img = createImage(WIDTH, HEIGHT);

        // GET AND SORT ALL SURFACES BASED ON DISTANCE
        ArrayList<GameSurface> surfaces = new ArrayList<>();

        for (GameObject o : objects)
            surfaces.addAll(o.getSurfaces());

        surfaces.sort((s1, s2) -> {
            double compare = s1.getCenterOfSurface().distanceTo(p.getPosition()) -
                    s2.getCenterOfSurface().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        // DRAW ALL SURFACES IN ORDER
        for (GameSurface s : surfaces)
            drawSurface(s, img);

        //DRAW CROSSHAIR
        int crosshairLength = p.getCrosshairLength();
        int crosshairWidth = p.getCrosshairWidth();
        Graphics g = img.getGraphics();
        g.setColor(p.getCrosshairColor());
        g.fillRect((WIDTH - crosshairLength) / 2, (HEIGHT - crosshairWidth) / 2, crosshairLength, crosshairWidth);
        g.fillRect((WIDTH - crosshairWidth) / 2, (HEIGHT - crosshairLength) / 2, crosshairWidth, crosshairLength);

        this.getGraphics().drawImage(img, 0, 0, this);
    }

    private void drawSurface(GameSurface s, Image img) {
        Graphics imgGraphics = img.getGraphics();
        imgGraphics.setColor(s.getColor());

        int numPoints = s.getNumPoints();

        int[] xs = new int[numPoints];
        int[] ys = new int[numPoints];

        boolean existsVisiblePoint = false;

        for (int i = 0; i < numPoints; i++) {
            Pair<Boolean, Pair<Integer, Integer>> result = p.getCoordinatesOfPointOnScreen(s.getPoint(i));
            Pair<Integer, Integer> coordinates = result.getValue();

            boolean canSeePoint = result.getKey();

            if (canSeePoint) {
                existsVisiblePoint = true;
                xs[i] = coordinates.getKey();
                ys[i] = coordinates.getValue();
            } else {
                xs[i] = flipComputerCoordinateX(coordinates.getKey());
                ys[i] = flipComputerCoordinateY(coordinates.getValue());
            }

        }

        if (existsVisiblePoint)
            imgGraphics.fillPolygon(xs, ys, numPoints);

    }


    public static int toComputerCoordinateSystemX(double X) {
        return (int) round(X) + WIDTH / 2;
    }

    public static int toComputerCoordinateSystemY(double Y) {
        return HEIGHT / 2 - (int) round(Y);
    }

    public static int flipComputerCoordinateX(int X) {
        return WIDTH / 2 - X;
    }

    public static int flipComputerCoordinateY(int Y) {
        return HEIGHT / 2 - Y;
    }

}
