package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 1:43 AM
 */
public class Glass extends Material {

    @Getter
    private double ior;

    public Glass(double ior) {
        this.ior = ior;
    }

    public Glass(double ior, SurfaceTexture color) {
        this.ior = ior;
        setSurfaceTexture(color);
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        Vec3 outwardNormal;
        Vec3 reflected = reflect(incoming.getDirection(), collision.getNormal());

        double n;
        Vec3 attenuation = Vec3.of(1.0);
        double reflective_probability;
        double cosine;

        if (incoming.getDirection().dot(collision.getNormal()) > 0) {
            outwardNormal = collision.getNormal().negate();
            n = this.ior;
            cosine = incoming.getDirection().dot(collision.getNormal()) / incoming.getDirection().length();
            cosine = Math.sqrt(1 - this.ior * this.ior * (1 - cosine * cosine));
        } else {
            outwardNormal = collision.getNormal();
            n = 1.0 / this.ior;
            cosine = -incoming.getDirection().dot(collision.getNormal()) / incoming.getDirection().length();
        }

        Vec3 refraction = refract(incoming.getDirection(), outwardNormal, n);
        Ray scattered;

        if (refraction != null) {
            reflective_probability = schlick(cosine, this.ior);
        } else {
            reflective_probability = 1.0;
        }

        if (Math.random() < reflective_probability) {
            scattered = new Ray(collision.getPoint(), reflected);
        } else {
            scattered = new Ray(collision.getPoint(), refraction);
        }

        if (getSurfaceTexture() != null) {
            attenuation = attenuation.multiply(getSurfaceTexture().getRGBAt(scattered.getOrigin()));
        }

        return new SurfaceInteraction(attenuation, scattered);
    }

    private double schlick(double cosine, double refractive_index) {
        double r0 = (1 - refractive_index) / (1 + refractive_index);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }
}