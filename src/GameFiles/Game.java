package GameFiles;

import GameObjects.CubeSquares;
import GameObjects.CubeTriangles;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.round;


public class Game extends JFrame implements ActionListener {

    final static int WIDTH = 1600;
    final static int HEIGHT = 1000;
    private final double fps = 60;

    private Player p = new Player();

    private ArrayList<GameObject> objects = new ArrayList<>(Arrays.asList(
//            new CubeSquares(5, 0, 0, 0)
            new CubeSquares(1, 6, 6, 6),
            new CubeTriangles(1, 6, 6, 6)
//new GameObject(
//        GameSurface.createSurface(new Color(0,0,0),GameVector.ZERO, new GameVector(1,1,1),GameVector.Z, 25 )
//    )
//            new CubeTriangles(1, 6, 6, -6),
//            new CubeTriangles(1, 6, -6, 6),
//            new CubeTriangles(1, 6, -6, -6),
//            new CubeTriangles(1, -6, 6, 6),
//            new CubeTriangles(1, -6, 6, -6),
//            new CubeTriangles(1, -6, -6, 6),
//            new CubeTriangles(1, -6, -6, -6)
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

        Timer timer = new Timer((int) (1000 / fps), this);
        timer.start();

    }

    public static void main(String[] args) {
        new Game();
    }


    public void actionPerformed(ActionEvent e) {

        p.move(fps);


        //            g.clearRect(0, 0, WIDTH, HEIGHT);
//            frame.repaint();

        //NOTE: speed/fps = distance per frame
        //NOTE: percent/fps = percent per frame

//            objects.get(8).shiftBy(objects.get(8).getCenterOfObject().minus(p.getPosition()).normalize().times(1 / objects.get(8).getCenterOfObject().length()));
//            objects.get(0).rotateBy(new GameFiles.GameVector(0, 0, 0), new GameFiles.GameVector(0, 2, 0), .005);
//            objects.get(0).scaleBy(GameFiles.GameVector.ZERO, (10 / 100d) * (1 / fps));

//        getGraphics().clearRect(0, 0, WIDTH, HEIGHT);
        repaint();
    }

    @Override
    public void repaint() {
        drawObjects();
    }

    private void drawObjects() {

        Image img = createImage(WIDTH, HEIGHT);

        objects.sort((o1, o2) -> {
            double compare = o1.getCenterOfObject().distanceTo(p.getPosition()) -
                    o2.getCenterOfObject().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameObject o : objects)
            drawObject(o, img);

        //DRAW CROSSHAIR
        int crosshairLength = p.getCrosshairLength();
        int crosshairWidth = p.getCrosshairWidth();
        Graphics g = img.getGraphics();
        g.setColor(p.getCrosshairColor());
        g.fillRect((WIDTH - crosshairLength) / 2, (HEIGHT - crosshairWidth) / 2, crosshairLength, crosshairWidth);
        g.fillRect((WIDTH - crosshairWidth) / 2, (HEIGHT - crosshairLength) / 2, crosshairWidth, crosshairLength);

        getGraphics().drawImage(img, 0, 0, this);
    }

    private void drawObject(GameObject o, Image img) {
        ArrayList<GameSurface> surfaces = o.getSurfaces();
        surfaces.sort((o1, o2) -> {
            double compare = o1.getCenterOfSurface().distanceTo(p.getPosition()) -
                    o2.getCenterOfSurface().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameSurface s : surfaces)
            drawSurface(s, img);
    }


    private void drawSurface(GameSurface s, Image img) {
        int numPoints = s.getNumPoints();

        int[] xs = new int[numPoints];
        int[] ys = new int[numPoints];
        boolean[] bs = new boolean[numPoints];
        boolean existsPointInFront = false;
        for (int i = 0; i < numPoints; i++) {
            Pair<Boolean, Pair<Integer, Integer>> result = p.getCoordinatesOfPointOnScreen(s.getPoint(i));
            existsPointInFront = !result.getKey() || existsPointInFront;
            Pair<Integer, Integer> coordinates = result.getValue();
//TODO
            xs[i] = coordinates.getKey();
            ys[i] = coordinates.getValue();
        }

        if (existsPointInFront) {
            Graphics imgGraphics = img.getGraphics();
            imgGraphics.setColor(s.getColor());
            imgGraphics.fillPolygon(xs, ys, numPoints);
        }
    }


    public static int toComputerCoordinateSystemX(double X) {
        return (int) round(X) + WIDTH / 2;
    }

    public static int toComputerCoordinateSystemY(double Y) {
        return HEIGHT / 2 - (int) round(Y);
    }

}
