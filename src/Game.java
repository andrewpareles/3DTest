import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.round;

public class Game extends JPanel {

    final static int WIDTH = 1600;
    final static int HEIGHT = 1000;
    private final static double fps = 60;

    private static JFrame frame = new JFrame();

    static {
        frame.getContentPane().add(new Game());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
    }

    private static Player p = new Player();


    private static ArrayList<GameObject> objects = new ArrayList<>(Arrays.asList(
            new Cube(5, 0, 0, 0)
    ));

    public static void main(String[] args) throws InterruptedException {
        frame.addKeyListener(p);
        frame.addMouseListener(p);
        frame.addMouseMotionListener(p);


        while (true) {
            Graphics g = frame.getGraphics();
            g.clearRect(0, 0, WIDTH, HEIGHT);
            drawObjects(p, objects, g);

            p.moveInDirection(p.getTotalVelocity().times(1 / fps));
            Thread.sleep(1000 / (long) fps);

            //NOTE: speed/fps = distance per frame
            //NOTE: percent/fps = percent per frame


//            objects.get(0).shiftBy(new GameVector(0, 0, .1));
//            objects.get(0).rotateBy(new GameVector(0, 0, 0), new GameVector(0, 2, 0), .005);
//            objects.get(0).scaleBy(GameVector.ZERO, (10 / 100d) * (1 / fps));
        }

    }

    public static void drawObjects(Player p, ArrayList<GameObject> objects, Graphics g) {
        objects.sort((o1, o2) -> {
            double compare = o1.getAverageSurfaceVector().distance(p.getPosition()) -
                    o2.getAverageSurfaceVector().distance(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameObject o : objects)
            drawObject(p, o, g);

    }

    public static void drawObject(Player p, GameObject o, Graphics g) {
        ArrayList<GameSurface> surfaces = o.getSurfaces();
        surfaces.sort((o1, o2) -> {
            double compare = o1.getAverageSurfaceVector().distance(p.getPosition()) -
                    o2.getAverageSurfaceVector().distance(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });
        for (GameSurface s : surfaces)
            drawSurface(p, s, g);
    }


    public static void drawSurface(Player p, GameSurface s, Graphics g) {
        int numPoints = s.getNumPoints();

        int[] xs = new int[numPoints];
        int[] ys = new int[numPoints];

        for (int i = 0; i < numPoints; i++) {
            Pair<Integer, Integer> coordinates = p.getCoordinatesOfPointOnScreen(s.getPoint(i));
            if (coordinates == null) return;

            xs[i] = coordinates.getKey();
            ys[i] = coordinates.getValue();
        }

        g.setColor(s.getColor());
        g.fillPolygon(xs, ys, numPoints);

    }


    public static int toComputerCoordinateSystemX(double X) {
        return (int) round(X) + WIDTH / 2;
    }

    public static int toComputerCoordinateSystemY(double Y) {
        return HEIGHT / 2 - (int) round(Y);
    }

}
