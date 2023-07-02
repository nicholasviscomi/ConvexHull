package Algorithms;

import java.awt.*;
import java.util.ArrayList;

public class JarvisMarch {
    private static int orientation(Point curr, Point test, Point best_guess) {
        int val = (test.y - curr.y) * (best_guess.x - test.x) -
                (test.x - curr.x) * (best_guess.y - test.y);

        if (val == 0) return 0;  // collinear
        return (val > 0) ? 1: 2; // clock or counterclock wise
    }

    public static ArrayList<Point> convex_hull(ArrayList<Point> points) {
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
}
