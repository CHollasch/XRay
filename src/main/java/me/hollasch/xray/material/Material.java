package me.hollasch.xray.material;

import lombok.Getter;
import lombok.Setter;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 23, 9:26 PM
 */
public abstract class Material {

    @Getter
    @Setter
    private SurfaceTexture surfaceTexture;

    public abstract SurfaceInteraction scatter(Ray incoming, RayCollision collision);

    protected Vec3 reflect(Vec3 incoming, Vec3 normal) {
        return incoming.subtract(normal.multiplyScalar(incoming.dot(normal) * 2.0f));
    }

    protected Vec3 refract(Vec3 incoming, Vec3 normal, double n) {
        Vec3 unitIncoming = incoming.normalize();
        double dot = unitIncoming.dot(normal);
        double discriminant = 1.0 - n * n * (1 - (dot * dot));

        if (discriminant > 0) {
            return unitIncoming
                    .subtract(normal.multiplyScalar(dot))
                    .multiplyScalar(n)
                    .subtract(normal.multiplyScalar(Math.sqrt(discriminant)));
        } else {
            return null;
        }
    }
}
