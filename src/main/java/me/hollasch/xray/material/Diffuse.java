package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 12:56 AM
 */
public class Diffuse extends Material {

    @Getter private Vec3 albedo;

    public Diffuse(Vec3 albedo) {
        this.albedo = albedo;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        Vec3 target = collision.getPoint().add(collision.getNormal()).add(Vec3.randomInUnitSphere());

        Ray scattered = new Ray(collision.getPoint(), target.subtract(collision.getPoint()));
        return new SurfaceInteraction(this.albedo, scattered);
    }
}
