package me.hollasch.xray.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 23, 10:15 PM
 */
@AllArgsConstructor
public class Sphere extends WorldObject {

    @Getter private Vec3 origin;
    @Getter private double radius;
    private AABB boundingBox;

    public Sphere(Vec3 origin, double radius, Material material) {
        this.origin = origin;
        this.radius = radius;
        this.material = material;

        Vec3 min = Vec3.of(origin.getX() - radius, origin.getY() - radius, origin.getZ() - radius);
        Vec3 max = Vec3.of(origin.getX() + radius, origin.getY() + radius, origin.getZ() + radius);

        this.boundingBox = new AABB(min, max, material);
    }

    public RayCollision rayIntersect(Ray ray, double tMin, double tMax) {
        Vec3 delta = ray.getOrigin().subtract(this.origin);
        double a = ray.getDirection().dot(ray.getDirection());
        double b = delta.dot(ray.getDirection());
        double c = delta.dot(delta) - (this.radius * this.radius);
        double discriminant = (b * b) - (a * c);

        if (discriminant > 0) {
            double t = (-b - Math.sqrt(b * b - a * c)) / a;

            if (t < tMax && t > tMin) {
                return new RayCollision(
                        t,
                        ray.getPointAt(t),
                        ray.getPointAt(t).subtract(origin).divideScalar(radius),
                        this.material
                );
            }

            t = (-b + Math.sqrt(b * b - a * c)) / a;

            if (t < tMax && t > tMin) {
                return new RayCollision(
                        t,
                        ray.getPointAt(t),
                        ray.getPointAt(t).subtract(origin).divideScalar(radius),
                        this.material
                );
            }
        }

        return null;
    }

    @Override
    public AABB getBoundingBox() {
        return this.boundingBox;
    }
}
