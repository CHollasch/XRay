package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.material.texture.SingleColorTexture;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 1:43 AM
 */
public class Glass extends Material {

    @Getter private float ior;

    public Glass(SurfaceTexture shading, float ior) {
        setSurfaceTexture(shading);
        this.ior = ior;
    }

    public Glass(float ior) {
        this(new SingleColorTexture(Vec3.of(1, 1, 1)), ior);
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        Vec3 outwardNormal;
        Vec3 reflected = reflect(incoming.getDirection(), collision.getNormal());

        float n;
        Vec3 attenuation = Vec3.of(1.0f, 1.0f, 1.0f);
        float reflective_probability;
        float cosine;

        if (incoming.getDirection().dot(collision.getNormal()) > 0) {
            outwardNormal = collision.getNormal().negate();
            n = this.ior;
            cosine = this.ior * incoming.getDirection().dot(collision.getNormal()) / incoming.getDirection().length();
        } else {
            outwardNormal = collision.getNormal();
            n = 1.0f / this.ior;
            cosine = -incoming.getDirection().dot(collision.getNormal()) / incoming.getDirection().length();
        }

        Vec3 refraction = refract(incoming.getDirection(), outwardNormal, n);
        Ray scattered;

        if (refraction != null) {
            reflective_probability = schlick(cosine, this.ior);
        } else {
            reflective_probability = 1.0f;
        }

        if (Math.random() < reflective_probability) {
            scattered = new Ray(collision.getPoint(), reflected);
        } else {
            scattered = new Ray(collision.getPoint(), refraction);
        }

        return new SurfaceInteraction(attenuation.add(getSurfaceTexture().getRGBAt(collision.getPoint())), scattered);
    }

    private float schlick(float cosine, float refractive_index) {
        float r0 = (1 - refractive_index) / (1 + refractive_index);
        r0 = r0 * r0;
        return (float) (r0 + (1 - r0) * Math.pow((1 - cosine), 5));
    }
}
