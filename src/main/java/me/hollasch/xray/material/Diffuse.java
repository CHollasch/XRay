package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.MathUtil;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 12:56 AM
 */
public class Diffuse extends Material {

    @Getter private SurfaceTexture albedo;

    public Diffuse(SurfaceTexture albedo) {
        this.albedo = albedo;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        Vec3 rnd = MathUtil.bakedRandomInUnitSphere();
        float dot = rnd.dot(collision.getNormal());

        Vec3 target;
        if (dot < 0) {
            target = collision.getNormal().add(rnd.negate());
        } else {
            target = collision.getNormal().add(rnd);
        }

        Ray scattered = new Ray(collision.getPoint(), target);
        return new SurfaceInteraction(this.albedo.getRGBAt(collision.getPoint()), scattered);
    }
}
