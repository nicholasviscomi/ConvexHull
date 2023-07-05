package Algorithms;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JarvisMarch {
    private static int orientation(Point curr, Point test, Point best_guess) {
        // https://algs4.cs.princeton.edu/91primitives/
        int val = (test.y - curr.y) * (best_guess.x - test.x) -
                (test.x - curr.x) * (best_guess.y - test.y);

        if (val == 0) return 0;  // co-linear
        return (val > 0) ? 1: 2; // clock or counter-clock wise
    }

    public static ArrayList<ArrayList<Point>> convex_hull(ArrayList<Point> points) {
        if (points == null || points.size() == 0) return null;

        ArrayList<ArrayList<Point>> animations = new ArrayList<>();
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
            ArrayList<Point> row = new ArrayList<>();
            row.add(curr);

            next = points.get(
                    (points.indexOf(curr) + 1) % points.size()
            );

            for (Point p: points) {
                if (p.equals(curr)) { continue; }

                if (orientation(curr, p, next) == 2) {
                    next = p;
                    row.add(p);
                }
            }

            animations.add(row);

            assert next != null;
            if (hull.contains(next)) {
                hull.add(next);
                break;
            }

            hull.add(next);
            curr = next; // connect the hull back to the origin
        }

        return animations;
    }
}
