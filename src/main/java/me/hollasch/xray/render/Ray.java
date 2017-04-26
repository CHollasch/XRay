package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;

/**
 * @author Connor Hollasch
 * @since Feb 23, 7:19 PM
 */
public class Ray {

    @Getter private final Vec3 origin;
    @Getter private final Vec3 direction;

    public Ray(final Vec3 origin, final Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 getPointAt(double t) {
        return origin.add(direction.multiplyScalar(t));
    }
}
