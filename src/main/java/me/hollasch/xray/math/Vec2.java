package me.hollasch.xray.math;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Connor Hollasch
 * @since Feb 23, 7:08 PM
 */
public class Vec2 {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter
    @Setter private double x, y;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public Vec2() {
        this(0, 0);
    }

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public final Vec2 add(Vec2 other) {
        return this.add(other.x, other.y);
    }

    public final Vec2 add(double x, double y) {
        return Vec2.of(this.x + x, this.y + y);
    }

    public final Vec2 subtract(Vec2 other) {
        return this.subtract(other.x, other.y);
    }

    public final Vec2 subtract(double x, double y) {
        return Vec2.of(this.x - x, this.y - y);
    }

    public final Vec2 multiply(Vec2 other) {
        return this.multiply(other.x, other.y);
    }

    public final Vec2 multiply(double x, double y) {
        return Vec2.of(this.x * x, this.y * y);
    }

    public final Vec2 divide(Vec2 other) {
        return this.divide(other.x, other.y);
    }

    public final Vec2 divide(double x, double y) {
        return Vec2.of(this.x / x, this.y / y);
    }

    public final Vec2 addScalar(double scalar) {
        return this.add(scalar, scalar);
    }

    public final Vec2 subtractScalar(double scalar) {
        return this.subtract(scalar, scalar);
    }

    public final Vec2 multiplyScalar(double scalar) {
        return this.multiply(scalar, scalar);
    }

    public final Vec2 divideScalar(double scalar) {
        return this.divide(scalar, scalar);
    }

    public final double dot(Vec2 other) {
        return this.dot(other.x, other.y);
    }

    public final double dot(double x, double y) {
        return (this.x * x) + (this.y * y);
    }

    public final Vec2 cross(Vec2 other) {
        return this.cross(other.x, other.y);
    }

    public final Vec2 cross(double x, double y) {
        return Vec2.of((this.x * y) - (this.y * x));
    }

    public final double lengthSquared() {
        return (x * x) + (y * y);
    }

    public final double length() {
        return Math.sqrt(lengthSquared());
    }

    public final Vec2 negate() {
        return Vec2.of(-this.x, -this.y);
    }

    public final Vec2 normalize() {
        double length = length();

        if (length > 0) {
            length = 1.0 / length;
        }

        return Vec2.of(this.x * length, this.y * length);
    }

    public final Vec2 clamp(double min, double max) {
        return Vec2.of(
                min > getX() ? min : max < getX() ? max : getX(),
                min > getY() ? min : max < getY() ? max : getY()
        );
    }

    public final Vec2 abs() {
        return Vec2.of(Math.abs(this.x), Math.abs(this.y));
    }

    public Vec2 clone() {
        return Vec2.of(this.x, this.y);
    }

    //==============================================================================================
    // STATIC ACCESS
    //==============================================================================================

    public static final Vec2 of(double x, double y) {
        return new Vec2(x, y);
    }

    public static final Vec2 of(float c) {
        return Vec2.of(c, c);
    }

    public static final Vec2 of(double c) {
        return new Vec2(c, c);
    }

    public static final Vec2 rand() {
        return Vec2.of(Math.random(), Math.random());
    }

    public static final double distanceBetween(final Vec2 a, final Vec2 b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();

        return Math.sqrt((dX * dX) + (dY * dY));
    }

    public static final Vec2 min(Vec2 a, Vec2 b) {
        double xMin = Math.min(a.getX(), b.getX());
        double yMin = Math.min(a.getY(), b.getY());

        return Vec2.of(xMin, yMin);
    }

    public static final Vec2 max(Vec2 a, Vec2 b) {
        double xMax = Math.max(a.getX(), b.getX());
        double yMax = Math.max(a.getY(), b.getY());

        return Vec2.of(xMax, yMax);
    }

    //==============================================================================================
    // DERIVED FUNCTIONS
    //==============================================================================================

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec2)) {
            return false;
        }

        Vec2 v = (Vec2) o;
        return v.x == x && v.y == y;
    }
}