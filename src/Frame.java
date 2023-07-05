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

//        point_timer = new Timer(100, this);
//        guess_timer = new Timer(100, this);
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2d = (Graphics2D) g;
        int radius = 10;
        if (points != null) {
            for (Point p : points) {
                g2d.fillOval(p.x, p.y, radius, radius);
            }
        }

        if (!should_animate) {
            if (hull != null && hull.size() > 1) {
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
