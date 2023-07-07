package Algorithms;

import java.awt.*;
import java.util.ArrayList;

public class QuickHull {
    private static Point cvt_pt(Point p) {
        return new Point(p.x, 700 - p.y);
    }

    public static ArrayList<Point> convex_hull(ArrayList<Point> points) {
        ArrayList<Point> hull = new ArrayList<>();

        // find left most and right most point
        Point leftmost = new Point(10000, 0);
        Point rightmost = new Point(0, 0);
        for (Point p: points) {
            p = cvt_pt(p);
            if (p.x > rightmost.x)  rightmost = p;
            if (p.x < leftmost.x)   leftmost = p;
        }

        hull.add(leftmost); hull.add(rightmost);

        ArrayList<ArrayList<Point>> split_points = split_points(points, leftmost, rightmost);
        ArrayList<Point> above = split_points.get(0);
        ArrayList<Point> below = split_points.get(1);




        return hull;
    }

    // Returns above points first, below points second
    public static ArrayList<ArrayList<Point>> split_points(ArrayList<Point> points, Point leftmost, Point rightmost) {
        double m = ((double) (rightmost.y - leftmost.y)) / ((double) (rightmost.x - leftmost.x));
        Point midpoint = new Point((leftmost.x + rightmost.x)/2, (leftmost.y + rightmost.y)/2);
        // y = mx (-mx1 + y1)
        double b = -m * midpoint.x + midpoint.y;

        System.out.println("y = " + m + "x + " + b);

        ArrayList<Point> above_line = new ArrayList<>();
        ArrayList<Point> below_line = new ArrayList<>();
        for (Point p: points) {
            if (p.y < m * p.x + b) {
                /*
                because y increases going down, this is above the line
                 */
                above_line.add(p);
            } else {
                below_line.add(p);
            }
        }

        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        result.add(above_line); result.add(below_line);
        return result;
    }
}
