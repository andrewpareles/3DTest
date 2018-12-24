import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
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


    private static ArrayList<GameSurface> surfaces = new ArrayList<>(Arrays.asList(
            new GameSurface(
                    new GameVector(10, -5, -5),
                    new GameVector(10, -5, 5),
                    new GameVector(10, 5, 5),
                    new GameVector(10, 5, -5)
            )

    ));


    public static void main(String[] args) throws InterruptedException {
        frame.addKeyListener(p);
        frame.addMouseListener(p);
        frame.addMouseMotionListener(p);


        while (true) {
            surfaces.sort((o1, o2) -> {
                double compare = o1.getAverageSurfaceVector().distance(p.getPosition()) -
                        o2.getAverageSurfaceVector().distance(p.getPosition());
                return compare == 0 ? 0 : compare < 0 ? 1 : -1;
            });

            Graphics g = frame.getGraphics();
            g.clearRect(0, 0, WIDTH, HEIGHT);
            for (GameSurface s : surfaces)
                drawSurface(p, s, g);

            p.moveInDirection(p.getTotalVelocity().times(1 / fps));

            Thread.sleep(1000 / (long) fps);

            //NOTE: speed/fps = distance per frame
            //NOTE: percent/fps = percent per frame


            //p.changeThetaBy(.01);
            //p.moveInDirection(new GameVector(0, 0, 10).times(1/fps));
            //p.setView(surfaces.get(0).getAverageSurfaceVector().minus(p.getPosition()));
            //surfaces.get(0).shiftBy(new GameVector(0, 0, .1));
            //surfaces.get(0).rotateBy(new GameVector(10, 5, 0), new GameVector(0, 2, 1), .05);
            //surfaces.get(0).scaleBy(new GameVector(10, 5, -5), (10 / 100d) * (1 / fps));
        }

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

        g.fillPolygon(xs, ys, numPoints);
        g.setColor(new Color(120, 40, 120));

    }


    public static int toComputerCoordinateSystemX(double X) {

        return (int) round(X) + WIDTH / 2;
    }

    public static int toComputerCoordinateSystemY(double Y) {
        return HEIGHT / 2 - (int) round(Y);
    }

}
