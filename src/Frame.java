import Algorithms.JarvisMarch;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Frame extends JPanel implements ChangeListener, ActionListener {
    JFrame frame;
    private Graphics2D g2d;
    JSlider n_points_slider;
    JButton solve, clear;

    int width = 700, height = 700;


    ArrayList<Point> points;
    int n_points = 25;
    ArrayList<Point> hull;
    ArrayList<ArrayList<Point>> animations;
    ArrayList<Point> flat_animations;
    Timer anim_timer;
    int anim_index = 0;
    boolean should_animate = false;

    public static void main(String[] args) {
        new Frame();
    }
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

        anim_timer = new Timer(300, this);
    }

    private ArrayList<Point> set_points() {
        ArrayList<Point> all_points = new ArrayList<>();

        for (double theta = 0; theta < Math.PI * 2; theta += (Math.PI*2)/100) {
            for (int y = -250; y < 250; y += 10) {
                for (int x = -250; x < 250; x += 10) {
                    all_points.add(
                        new Point(
                            (int) (width/2 + x * Math.cos(theta)), (int) (height/2 + y * Math.sin(theta)) - 50
                        )
                    );
                }
            }
        }

        Collections.shuffle(all_points);

        ArrayList<Point> res = new ArrayList<>();
        for (int i = 0; i < n_points; i++) {
            res.add(all_points.get(i));
        }

        return res;
    }

    private int are_intersecting(
            float v1x1, float v1y1, float v1x2, float v1y2,
            float v2x1, float v2y1, float v2x2, float v2y2
    ) {
        float d1, d2;
        float a1, a2, b1, b2, c1, c2;

        // Convert vector 1 to a line (line 1) of infinite length.
        // We want the line in linear equation standard form: A*x + B*y + C = 0
        a1 = v1y2 - v1y1;
        b1 = v1x1 - v1x2;
        c1 = (v1x2 * v1y1) - (v1x1 * v1y2);

        // Every point (x,y), that solves the equation above, is on the line,
        // every point that does not solve it, is not. The equation will have a
        // positive result if it is on one side of the line and a negative one
        // if is on the other side of it. We insert (x1,y1) and (x2,y2) of vector
        // 2 into the equation above.
        d1 = (a1 * v2x1) + (b1 * v2y1) + c1;
        d2 = (a1 * v2x2) + (b1 * v2y2) + c1;

        // If d1 and d2 both have the same sign, they are both on the same side
        // of our line 1 and in that case no intersection is possible. Careful,
        // 0 is a special case, that's why we don't test ">=" and "<=",
        // but "<" and ">".
        if (d1 >= 0 && d2 >= 0) return 0;
        if (d1 <= 0 && d2 <= 0) return 0;

        // The fact that vector 2 intersected the infinite line 1 above doesn't
        // mean it also intersects the vector 1. Vector 1 is only a subset of that
        // infinite line 1, so it may have intersected that line before the vector
        // started or after it ended. To know for sure, we have to repeat the
        // the same test the other way round. We start by calculating the
        // infinite line 2 in linear equation standard form.
        a2 = v2y2 - v2y1;
        b2 = v2x1 - v2x2;
        c2 = (v2x2 * v2y1) - (v2x1 * v2y2);

        // Calculate d1 and d2 again, this time using points of vector 1.
        d1 = (a2 * v1x1) + (b2 * v1y1) + c2;
        d2 = (a2 * v1x2) + (b2 * v1y2) + c2;

        // Again, if both have the same sign (and neither one is 0),
        // no intersection is possible.
        if (d1 > 0 && d2 > 0) return 0;
        if (d1 < 0 && d2 < 0) return 0;

        // If we get here, only two possibilities are left. Either the two
        // vectors intersect in exactly one point or they are collinear, which
        // means they intersect in any number of points from zero to infinite.
        if ((a1 * b2) - (a2 * b1) == 0.0f) return 2;

        // If they are not collinear, they must intersect in exactly one point.
        return 1;
    }

    private boolean point_in_polygon(Point test) {
        if (hull == null) return false;

        int n_hits = 0;
        for (int i = 1; i < hull.size() - 1; i++) {
            Point p1 = hull.get(i - 1), p2 = hull.get(i);
            p1 = new Point(p1.x + radius/2, p1.y + radius/2);
            p2 = new Point(p2.x + radius/2, p2.y + radius/2);

            if (are_intersecting(
                    p1.x, p1.y, p2.x, p2.y,
                    test.x, test.y, width, test.y
            ) == 1) {
                n_hits++;
            }

        }

        return n_hits == 1;
    }

    int radius = 10;
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;

        int radius = 10;
        if (points != null) {
            for (Point p : points) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.fillOval(p.x, p.y, radius, radius);
            }
        }

        if (!should_animate) {
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    if (point_in_polygon(new Point(x, y))) {
//                        g2d.setColor(new Color(0, 255, 0, (int) (255 * 0.5)));
//                        g2d.fillOval(x, y, 1, 1);
//                    }
//                }
//            }

            if (hull != null ) {
                int[] x_points = new int[hull.size()];
                int[] y_points = new int[hull.size()];
                for (int i = 0; i < hull.size(); i++) {
                    x_points[i] = hull.get(i).x + radius/2;
                    y_points[i] = hull.get(i).y + radius/2;
                }
                g2d.setColor(new Color(0, 255, 0, (int) (255 * 0.3)));
                g2d.fillPolygon(x_points, y_points, hull.size());

                for (int i = 1; i < hull.size(); i++) {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(
                            hull.get(i - 1).x + radius / 2, hull.get(i - 1).y + radius / 2,
                            hull.get(i).x + radius / 2, hull.get(i).y + radius / 2
                    );
                }

                for (Point p : hull) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillOval(p.x, p.y, radius, radius);
                }
            }
        }

        if (should_animate) {
            Point curr = null;
            Point prev = null;
            for (int i = 0; i < anim_index; i++) {
                Point p = flat_animations.get(i);
                if (i == 0) curr = p;


                // can't only check if p is a point on the hull
                // rather, must check if p is the NEXT point on the hull, not just any point
                assert hull != null;
                if (p.equals(
                        hull.get(hull.indexOf(prev) + 1)
                )) {
                    curr = p;
                    g2d.setColor(Color.RED);
                    g2d.fillOval(p.x, p.y, radius, radius);

                    if (prev != null) {
                        g2d.setStroke(new BasicStroke(3));
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(
                                prev.x + radius / 2, prev.y + radius / 2,
                                curr.x + radius / 2, curr.y + radius / 2
                        );
                    }
                    prev = curr;
                } else {
                    // drawing the guessing lines
                    g2d.setStroke(new BasicStroke(1));
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(
                            curr.x + radius / 2, curr.y + radius / 2,
                            p.x + radius / 2, p.y + radius / 2
                    );
                }
            }
        }

        g2d.setStroke(new BasicStroke(1));
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
            animations = JarvisMarch.convex_hull(points);
            flat_animations = new ArrayList<>();

            hull = new ArrayList<>();
            for (ArrayList<Point> row : animations) {
                hull.add(row.get(0));
                flat_animations.addAll(row);
            }
            hull.add(hull.get(0));

            anim_index = 0;
            should_animate = true;
//            point_timer.start();
//            guess_timer.start();
            anim_timer.start();
            repaint();
        }

        if (e.getSource() == clear) {
            hull = null;
            should_animate = false;

//            point_timer.stop();
//            guess_timer.stop();
            anim_timer.stop();
            repaint();
        }

        if (e.getSource() == anim_timer) {
            anim_index += 1;

            if (anim_index >= flat_animations.size() - 1) {
                anim_timer.stop();
                should_animate = false;
            }

            repaint();
        }
    }
}
