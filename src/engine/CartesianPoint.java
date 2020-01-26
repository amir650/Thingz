package engine;

import java.util.*;

public class CartesianPoint<T extends Number> {

    private final T x;
    private final T y;

    CartesianPoint(final T x,
                   final T y) {
        this.x = x;
        this.y = y;
    }

    CartesianPoint(final CartesianPoint<T> point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public T getX() {
        return this.x;
    }

    public T getY() {
        return this.y;
    }

    public static double distance(final CartesianPoint<Double> p1,
                                  final CartesianPoint<Double> p2) {
        final double xTerm = p2.getX() - p1.getX();
        final double yTerm = p2.getY() - p1.getY();
        return Math.sqrt((xTerm * xTerm) + (yTerm * yTerm));
    }

    public double distance(final CartesianPoint<Double> p2) {
        return -1;
    }

    @Override
    public String toString() {
        return "(" +this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CartesianPoint point = (CartesianPoint) o;
        return this.x.equals(point.x) &&
               this.y.equals(point.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
