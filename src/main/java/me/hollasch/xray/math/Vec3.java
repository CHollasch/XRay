package me.hollasch.xray.math;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Connor Hollasch
 * @since Feb 23, 7:08 PM
 */
public class Vec3 {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter
    @Setter private double x, y, z;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public Vec3() {
        this(0, 0, 0);
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public final Vec3 add(Vec3 other) {
        return this.add(other.x, other.y, other.z);
    }

    public final Vec3 add(double x, double y, double z) {
        return Vec3.of(this.x + x, this.y + y, this.z + z);
    }

    public final Vec3 subtract(Vec3 other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public final Vec3 subtract(double x, double y, double z) {
        return Vec3.of(this.x - x, this.y - y, this.z - z);
    }

    public final Vec3 multiply(Vec3 other) {
        return this.multiply(other.x, other.y, other.z);
    }

    public final Vec3 multiply(double x, double y, double z) {
        return Vec3.of(this.x * x, this.y * y, this.z * z);
    }

    public final Vec3 divide(Vec3 other) {
        return this.divide(other.x, other.y, other.z);
    }

    public final Vec3 divide(double x, double y, double z) {
        return Vec3.of(this.x / x, this.y / y, this.z / z);
    }

    public final Vec3 addScalar(double scalar) {
        return this.add(scalar, scalar, scalar);
    }

    public final Vec3 subtractScalar(double scalar) {
        return this.subtract(scalar, scalar, scalar);
    }

    public final Vec3 multiplyScalar(double scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public final Vec3 divideScalar(double scalar) {
        return this.divide(scalar, scalar, scalar);
    }

    public final double dot(Vec3 other) {
        return this.dot(other.x, other.y, other.z);
    }

    public final double dot(double x, double y, double z) {
        return (this.x * x) + (this.y * y) + (this.z * z);
    }

    public final Vec3 cross(Vec3 other) {
        return this.cross(other.x, other.y, other.z);
    }

    public final Vec3 cross(double x, double y, double z) {
        return Vec3.of((this.y * z) - (this.z * y), (this.z * x) - (this.x * z), (this.x * y) - (this.y * x));
    }

    public final double lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public final double length() {
        return Math.sqrt(lengthSquared());
    }

    public final Vec3 negate() {
        return Vec3.of(-this.x, -this.y, -this.z);
    }

    public final Vec3 normalize() {
        double length = length();

        if (length > 0) {
            length = 1.0 / length;
        }

        return Vec3.of(this.x * length, this.y * length, this.z * length);
    }

    public final Vec3 clamp(double min, double max) {
        return Vec3.of(
                min > getX() ? min : max < getX() ? max : getX(),
                min > getY() ? min : max < getY() ? max : getY(),
                min > getZ() ? min : max < getZ() ? max : getZ()
        );
    }

    public final Vec3 abs() {
        return Vec3.of(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public final int maxDimensionIndex() {
        return (this.x > this.y) ? ((this.x > this.z) ? 0 : 2) : ((this.y > this.z) ? 1 : 2);
    }

    public final double maxComponent() {
        return (this.x > this.y) ? ((this.x > this.z) ? this.x : this.z) : ((this.y > this.z) ? this.y : this.z);
    }

    public final Vec3 permute(int xL, int yL, int zL) {
        double[] dim = {this.x, this.y, this.z};
        return Vec3.of(dim[xL], dim[yL], dim[zL]);
    }

    public final int toRGB() {
        return this.toRGBA(1.0);
    }

    public final int toRGBA(double alpha) {
        return toRGBA(255.99, alpha);
    }

    public final int toRGBA(double max, double alpha) {
        Vec3 clamped = clamp(0, 1);

        this.x = clamped.x;
        this.y = clamped.y;
        this.z = clamped.z;

        return ((int) (max * this.z)
                | ((int) (max * this.y) << 8)
                | ((int) (max * this.x) << 16)
                | ((int) (max * alpha) << 24));
    }

    public Vec3 clone() {
        return Vec3.of(this.x, this.y, this.z);
    }

    //==============================================================================================
    // STATIC ACCESS
    //==============================================================================================

    public static final Vec3 of(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static final Vec3 of(float c) {
        return Vec3.of(c, c, c);
    }

    public static final Vec3 of(double c) {
        return new Vec3(c, c,c);
    }

    public static final Vec3 rand() {
        return Vec3.of(Math.random(), Math.random(), Math.random());
    }

    public static final double distanceBetween(final Vec3 a, final Vec3 b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();

        return Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
    }

    public static final Vec3 min(Vec3 a, Vec3 b) {
        double xMin = Math.min(a.getX(), b.getX());
        double yMin = Math.min(a.getY(), b.getY());
        double zMin = Math.min(a.getZ(), b.getZ());

        return Vec3.of(xMin, yMin, zMin);
    }

    public static final Vec3 max(Vec3 a, Vec3 b) {
        double xMax = Math.max(a.getX(), b.getX());
        double yMax = Math.max(a.getY(), b.getY());
        double zMax = Math.max(a.getZ(), b.getZ());

        return Vec3.of(xMax, yMax, zMax);
    }

    public static final Vec3 randomInUnitSphere() {
        Vec3 point;

        do {
            point = Vec3.rand().multiplyScalar(2.0).subtract(Vec3.of(1, 1, 1));
        } while (point.lengthSquared() >= 1);

        return point;
    }

    public static final Vec3 randomInUnitDisk() {
        Vec3 point;

        do {
            point = Vec3.rand().multiply(Vec3.of(2, 2, 0)).subtract(Vec3.of(1, 1, 0));
        } while (point.dot(point) >= 1.0);

        return point;
    }

    //==============================================================================================
    // DERIVED FUNCTIONS
    //==============================================================================================

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec3)) {
            return false;
        }

        Vec3 v = (Vec3) o;
        return v.x == x && v.y == y && v.z == z;
    }
}