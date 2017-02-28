package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 1:11 AM
 */
public class Glossy extends Material {

    @Getter private SurfaceTexture albedo;
    @Getter private float roughness;

    public Glossy(SurfaceTexture albedo, float roughness) {
        this.albedo = albedo;
        this.roughness = roughness;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        Vec3 reflected = reflect(incoming.getDirection().normalize(), collision.getNormal());

        Ray scattered = new Ray(collision.getPoint(), reflected.add(Vec3.randomInUnitSphere().multiplyScalar(this.roughness)));
        Vec3 attenuation = this.albedo.getRGBAt(collision.getPoint());

        if (scattered.getDirection().dot(collision.getNormal()) > 0) {
            return new SurfaceInteraction(attenuation, scattered);
        } else {
            return null;
        }
    }
}
