import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Frame extends JPanel implements ChangeListener, ActionListener {
    JFrame frame;
    JSlider n_points_slider;
    JButton solve, clear;

    int width = 700, height = 700;


    ArrayList<Point> points;
    int n_points = 25;
    ArrayList<Point> hull;
    public Frame() {
        setLayout(null);
        setFocusable(true);
        requestFocus();

        frame = new JFrame();
        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Convex Hull");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        n_points_slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 25);
        n_points_slider.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
        n_points_slider.setMinorTickSpacing(5);
        n_points_slider.setMajorTickSpacing(10);
        n_points_slider.setPaintLabels(true);
        n_points_slider.setPaintTicks(true);
        Dimension d = n_points_slider.getPreferredSize();
        n_points_slider.setBounds(
                width / 2 - d.width / 2, height - 100,
                d.width, d.height
        );
        n_points_slider.addChangeListener(this);
        n_points_slider.setVisible(true);

        solve = new JButton();
        solve = new JButton();
        solve.setText("Find Hull");
        solve.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        d = solve.getPreferredSize();
        solve.setBounds(
                n_points_slider.getX() + n_points_slider.getWidth() + 10,
                n_points_slider.getY() + n_points_slider.getHeight() / 2 - d.height/2,
                d.width, d.height
        );
        solve.addActionListener(this);
        solve.setVisible(true);

        clear = new JButton();
        clear = new JButton();
        clear.setText("Clear Hull");
        clear.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        d = clear.getPreferredSize();
        clear.setBounds(
                solve.getX() + solve.getWidth() + 10,
                n_points_slider.getY() + n_points_slider.getHeight() / 2 - d.height/2,
                d.width, d.height
        );
        clear.addActionListener(this);
        clear.setVisible(true);

        add(n_points_slider);
        add(solve);
        add(clear);

        points = set_points();
        frame.setVisible(true);
    }

    private ArrayList<Point> set_points() {
        ArrayList<Point> all_points = new ArrayList<>();

        Random rand = new Random();
        for (int x = 100; x < 600; x += rand.nextInt(30) + 10) {
            for (int y = 50; y < 550; y += rand.nextInt(30) + 10) {
                all_points.add(new Point(x, y));
            }
        }

        Collections.shuffle(all_points);

        ArrayList<Point> res = new ArrayList<>();
        for (int i = 0; i < n_points; i++) {
            res.add(all_points.get(i));
        }

        return res;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int radius = 10;
        if (points != null) {
            for (Point p : points) {
                g.fillOval(p.x, p.y, radius, radius);
            }
        }

        if (hull != null && hull.size() > 1) {
            for (int i = 1; i < hull.size(); i++) {
                g.drawLine(
                    hull.get(i - 1).x + radius/2, hull.get(i - 1).y + radius/2,
                    hull.get(i).x + radius/2, hull.get(i).y + radius/2
                );
            }

            for (Point p: hull) {
                g.setColor(new Color(0, 255, 0));
                g.fillOval(p.x, p.y, radius, radius);
            }
        }
    }

    private int orientation(Point curr, Point test, Point best_guess) {
        int val = (test.y - curr.y) * (best_guess.x - test.x) -
                (test.x - curr.x) * (best_guess.y - test.y);

        if (val == 0) return 0;  // collinear
        return (val > 0) ? 1: 2; // clock or counterclock wise
    }
    private ArrayList<Point> jarvis_march() {
        if (points == null || points.size() == 0) return null;

        ArrayList<Point> hull = new ArrayList<>();

        // finding leftmost point
        Point start = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).x < start.x) {
                start = points.get(i);
            }
        }

        Point curr = start;
        hull.add(curr);

        Point next;
        while (true) {
            next = points.get((points.indexOf(curr) + 1) % points.size());
            for (Point p: points) {
                if (p.equals(curr)) { continue; }

                if (orientation(curr, p, next) == 2) {
                    next = p;
                }
            }
            assert next != null;
            if (hull.contains(next)) {
                hull.add(next);
                break;
            }

            hull.add(next);
            curr = next; // connect the hull back to the origin
        }

        return hull;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == n_points_slider) {
            hull = null;
            n_points = n_points_slider.getValue();
            points = set_points();
            repaint();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == solve) {
            hull = jarvis_march();
            repaint();
        }

        if (e.getSource() == clear) {
            hull = null;
            repaint();
        }
    }
}
