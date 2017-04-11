package me.hollasch.xray.object;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * Math derived from public GitHub repository: https://github.com/idris/raytracer
 *
 * @author Connor Hollasch
 * @since April 11, 4:30 PM
 */
public class Plane extends WorldObject {

    @Getter private float a, b, c, d;
    @Getter private Vec3 normal;
    @Getter private Material material;

    public Plane(float a, float b, float c, float d, Material material) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.material = material;

        this.normal = new Vec3(a, b, c).normalize();
    }

    @Override
    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        float d = (this.a * ray.getDirection().getX()
                + this.b * ray.getDirection().getY()
                + this.c * ray.getDirection().getZ());

        if (d == 0) {
            return null;
        }

        float t = -(this.a * ray.getOrigin().getX()
                + this.b * ray.getOrigin().getY()
                + this.c * ray.getOrigin().getZ() + d) / d;

        if (t < tMin || t > tMax) {
            return null;
        }

        return new RayCollision(t, ray.getPointAt(t), this.normal, this.material);
    }
}
