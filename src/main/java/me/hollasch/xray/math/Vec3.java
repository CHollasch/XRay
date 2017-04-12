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
    @Setter private float x, y, z;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public Vec3() {
         this(0, 0, 0);
    }

    public Vec3(float x, float y, float z) {
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

    public final Vec3 add(float x, float y, float z) {
        return Vec3.of(this.x + x, this.y + y, this.z + z);
    }

    public final Vec3 subtract(Vec3 other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public final Vec3 subtract(float x, float y, float z) {
        return Vec3.of(this.x - x, this.y - y, this.z - z);
    }

    public final Vec3 multiply(Vec3 other) {
        return this.multiply(other.x, other.y, other.z);
    }

    public final Vec3 multiply(float x, float y, float z) {
        return Vec3.of(this.x * x, this.y * y, this.z * z);
    }

    public final Vec3 divide(Vec3 other) {
        return this.divide(other.x, other.y, other.z);
    }

    public final Vec3 divide(float x, float y, float z) {
        return Vec3.of(this.x / x, this.y / y, this.z / z);
    }

    public final Vec3 addScalar(float scalar) {
        return this.add(scalar, scalar, scalar);
    }

    public final Vec3 subtractScalar(float scalar) {
        return this.subtract(scalar, scalar, scalar);
    }

    public final Vec3 multiplyScalar(float scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public final Vec3 divideScalar(float scalar) {
        return this.divide(scalar, scalar, scalar);
    }

    public final float dot(Vec3 other) {
        return this.dot(other.x, other.y, other.z);
    }

    public final float dot(float x, float y, float z) {
        return (this.x * x) + (this.y * y) + (this.z * z);
    }

    public final Vec3 cross(Vec3 other) {
        return this.cross(other.x, other.y, other.z);
    }

    public final Vec3 cross(float x, float y, float z) {
        return Vec3.of((this.y * z) - (this.z * y), (this.z * x) - (this.x * z), (this.x * y) - (this.y * x));
    }

    public final float lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public final float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public final Vec3 negate() {
        return Vec3.of(-this.x, -this.y, -this.z);
    }

    public final Vec3 normalize() {
        float length = length();
        if (length > 0) {
            length = 1.0f / length;
        }

        return Vec3.of(this.x * length, this.y * length, this.z * length);
    }
    
    public final Vec3 clamp(float min, float max) {
        return Vec3.of(
                min > getX() ? min : max < getX() ? max : getX(),
                min > getY() ? min : max < getY() ? max : getY(),
                min > getZ() ? min : max < getZ() ? max : getZ()
        );
    }

    public final int toRGB() {
        return this.toRGBA(1.0f);
    }

    public final int toRGBA(float alpha) {
        return toRGBA(255.99f, alpha);
    }

    public final int toRGBA(float max, float alpha) {
        Vec3 clamped = clamp(0f, 1f);

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

    public static final Vec3 of(float x, float y, float z) {
        return new Vec3(x, y, z);
    }

    public static final Vec3 rand() {
        return Vec3.of((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    public static final Vec3 randomInUnitSphere() {
        Vec3 point;

        do {
            point = Vec3.rand().multiplyScalar(2.0f).subtract(Vec3.of(1, 1, 1));
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
    // TOSTRING
    //==============================================================================================

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
