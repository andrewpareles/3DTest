package GameFiles;

import GameObjects.Cube;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.round;

public class Game extends JPanel implements ActionListener {
    final static int WIDTH = 1600;
    final static int HEIGHT = 1000;
    private final double fps = 20;

    private Timer timer;
    private JFrame frame = new JFrame();

    private Player p = new Player();

    private Game() {
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);

        frame.addKeyListener(p);
        frame.addMouseListener(p);
        frame.addMouseMotionListener(p);

        timer = new Timer((int) (1000 / fps), this);
        timer.start();
    }

    private ArrayList<GameObject> objects = new ArrayList<>(Arrays.asList(
            new Cube(5, 0, 0, 0),
            new Cube(1, 6, 6, 6),
            new Cube(1, 6, 6, -6),
            new Cube(1, 6, -6, 6),
            new Cube(1, 6, -6, -6),
            new Cube(1, -6, 6, 6),
            new Cube(1, -6, 6, -6),
            new Cube(1, -6, -6, 6),
            new Cube(1, -6, -6, -6)
    ));

    public static void main(String[] args) throws InterruptedException {
        new Game();
//        game.frame.getContentPane().add(game);
//        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        game.frame.setSize(WIDTH, HEIGHT);
//        game.frame.setVisible(true);
//
//        game.frame.addKeyListener(game.p);
//        game.frame.addMouseListener(game.p);
//        game.frame.addMouseMotionListener(game.p);

    }


    public void actionPerformed(ActionEvent e) {

        p.moveInDirection(p.getTotalVelocity().times(1 / fps));

        //            g.clearRect(0, 0, WIDTH, HEIGHT);
//            frame.repaint();

        //NOTE: speed/fps = distance per frame
        //NOTE: percent/fps = percent per frame

//            objects.get(8).shiftBy(objects.get(8).getCenterOfObject().minus(p.getPosition()).normalize().times(1 / objects.get(8).getCenterOfObject().length()));
//            objects.get(0).rotateBy(new GameFiles.GameVector(0, 0, 0), new GameFiles.GameVector(0, 2, 0), .005);
//            objects.get(0).scaleBy(GameFiles.GameVector.ZERO, (10 / 100d) * (1 / fps));
    }

    @Override
    public void repaint() {
        drawObjects(p, objects);
    }

    public void drawObjects(Player p, ArrayList<GameObject> objects) {
        objects.sort((o1, o2) -> {
            double compare = o1.getCenterOfObject().distanceTo(p.getPosition()) -
                    o2.getCenterOfObject().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameObject o : objects)
            drawObject(p, o);

    }

    public void drawObject(Player p, GameObject o) {
        ArrayList<GameSurface> surfaces = o.getSurfaces();
        surfaces.sort((o1, o2) -> {
            double compare = o1.getCenterOfSurface().distanceTo(p.getPosition()) -
                    o2.getCenterOfSurface().distanceTo(p.getPosition());
            return compare == 0 ? 0 : compare < 0 ? 1 : -1;
        });

        for (GameSurface s : surfaces)
            drawSurface(p, s);
    }


    public void drawSurface(Player p, GameSurface s) {
        int numPoints = s.getNumPoints();

        int[] xs = new int[numPoints];
        int[] ys = new int[numPoints];

        for (int i = 0; i < numPoints; i++) {
            Pair<Integer, Integer> coordinates = p.getCoordinatesOfPointOnScreen(s.getPoint(i));
            if (coordinates == null) return;

            xs[i] = coordinates.getKey();
            ys[i] = coordinates.getValue();
        }

        Graphics g = frame.getGraphics();
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
