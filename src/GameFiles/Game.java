package GameFiles;

import GameObjects.CubeSquares;
import GameObjects.CubeSquares;
import GameObjects.TriangularPyramid;
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
            new CubeSquares(5, 0, 0, 0),
            new TriangularPyramid(20, 20, 20, 2),
            new CubeSquares(1, 6, 6, 6),
            new GameObject(
                    GameSurface.createSurface(new Color(0, 17, 255), new GameVector(12, 32, 8), new GameVector(18, 32, 6),  new GameVector(10, 36, 7), 25)
            ),
            new CubeSquares(1, 6, 6, -6),
            new CubeSquares(1, 6, -6, 6),
            new CubeSquares(1, 6, -6, -6),
            new CubeSquares(1, -6, 6, 6),
            new CubeSquares(1, -6, 6, -6),
            new CubeSquares(1, -6, -6, 6),
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

        Timer timer = new Timer((int) (1000 / fps), this);
        timer.start();


        p.moveInDirection(new GameVector(-20, 5, 0));

    }

    public static void main(String[] args) {
        new Game();
    }

    public void actionPerformed(ActionEvent e) {
        p.move(fps);

        //NOTE: speed/fps = distance per frame
        //NOTE: percent/fps = percent per frame

//        GameVector dist = objects.get(objects.size()-1).getCenterOfObject();
//        GameVector amt = dist.minus(p.getPosition()).times(1 / dist.lengthSquared());
//        if (amt.isFinite()) objects.get(objects.size()-1).shiftBy(amt);



        // TODO WHY DOES ROTATING MOVE THINGS TO THE ORIGIN
        
//        GameVector amt2 = objects.get(0).getCenterOfObject();
//        System.out.println(amt2);
//        objects.get(0).rotateBy(amt2, new GameFiles.GameVector(0, 1, 0), .005);
//            objects.get(0).scaleBy(GameFiles.GameVector.ZERO(), (10 / 100d) * (1 / fps));

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
        //closest surface comes 1st
        surfaces.sort((o1, o2) -> {
            double compare = o1.getCenterOfSurface().distanceTo(p.getPosition()) -
                    o2.getCenterOfSurface().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameSurface s : surfaces)
            drawSurface(s, img);
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
        return WIDTH - X;
    }

    public static int flipComputerCoordinateY(int Y) {
        return -Y;
    }

}
