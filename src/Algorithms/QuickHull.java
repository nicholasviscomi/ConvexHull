package Algorithms;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class QuickHull {

    public static ArrayList<Point> hull = new ArrayList<>();

    static Point prev_extreme = null;

    /*
    on left if > 0
    on right if < 0
    on line if = 0
     */
    public double orientation(Point a, Point b, Point test) {
        return (b.x - a.x)*(test.y - a.y) - (b.y - a.y)*(test.x - a.x);
    }
    public static void convex_hull(ArrayList<Point> points, Point leftmost, Point rightmost, int insertion_index) {
//        ArrayList<ArrayList<Point>> split_points = split_points(points, leftmost, rightmost);
        Point extreme = find_extreme(points, leftmost, rightmost);
        if (extreme != null && !extreme.equals(prev_extreme)) {
            System.out.println("extreme = " + extreme);
            System.out.println("leftmost = " + leftmost);
            System.out.println("rightmost = " + rightmost);

            hull.add(insertion_index, extreme);

//            new Scanner(System.in).nextLine();
            ArrayList<Point> left_section = split_points(points, leftmost, extreme).get(0);
            convex_hull(left_section, leftmost, extreme, insertion_index);

            ArrayList<Point> right_section = split_points(points, extreme, rightmost).get(0);
            convex_hull(right_section, extreme, rightmost, insertion_index + 1);

            System.out.println("hull = " + hull);
            prev_extreme = extreme;
        }
        //        ArrayList<ArrayList<Point>> split_points = split_points(points, leftmost, rightmost);
//        ArrayList<Point> above = split_points.get(0);
//        Point ext = find_extreme(above, leftmost, rightmost);
//        if (above.size() > 0) {
//            hull.add(ext);
//        }
//        split_points = split_points(above, leftmost, ext);
//        above = split_points.get(0);
//        ext = find_extreme(above, leftmost, ext);
//        if (above.size() > 0) {
//            hull.add(ext);
//        }
//        split_points = split_points(above, ext, rightmost);
//        above = split_points.get(0);
//        ext = find_extreme(above, ext, rightmost);
//        if (above.size() > 0) {
//            hull.add(ext);
//        }
    }

    public static Point find_extreme(ArrayList<Point> points, Point leftmost, Point rightmost) {
        if (points.size() == 0) {
            System.out.println("No more points above");
            return null;
        }

        double m = ((double) (rightmost.y - leftmost.y)) / ((double) (rightmost.x - leftmost.x));
        Point midpoint = new Point((leftmost.x + rightmost.x)/2, (leftmost.y + rightmost.y)/2);
        double b = -m * midpoint.x + midpoint.y;

        Point extreme = null; double dist = 0;
        for (Point p: points) {

            /*
            y = mx - mx1 + y1
            A = -m
            B = 1
            C = -b
             */
            double d = Math.abs(-m * p.x + p.y - b)/Math.sqrt((-m)*(-m) + b*b);
            if (d > dist) {
                extreme = p;
                dist = d;
            }
        }

        if (extreme == null) return null;

        if (extreme.equals(leftmost) || extreme.equals(rightmost)) {
            System.out.println("No more extreme points");
            return null;
        }

        return extreme;
    }

    // Returns above points first, below points second
    public static ArrayList<ArrayList<Point>> split_points(ArrayList<Point> points, Point leftmost, Point rightmost) {
        double m = ((double) (rightmost.y - leftmost.y)) / ((double) (rightmost.x - leftmost.x));
        Point midpoint = new Point((leftmost.x + rightmost.x)/2, (leftmost.y + rightmost.y)/2);
        // y = mx (-mx1 + y1)
        double b = -m * midpoint.x + midpoint.y;

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
