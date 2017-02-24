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
    @Getter private float radius;

    public Sphere(Vec3 origin, float radius, Material material) {
        this.origin = origin;
        this.radius = radius;
        this.material = material;
    }

    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        Vec3 delta = ray.getOrigin().subtract(this.origin);
        float a = ray.getDirection().dot(ray.getDirection());
        float b = delta.dot(ray.getDirection());
        float c = delta.dot(delta) - (this.radius * this.radius);
        float discriminant = (b * b) - (a * c);

        if (discriminant > 0) {
            float t = (float) ((-b - Math.sqrt(b * b - a * c)) / a);

            if (t < tMax && t > tMin) {
                return new RayCollision(
                        t,
                        ray.getPointAt(t),
                        ray.getPointAt(t).subtract(origin).divideScalar(radius),
                        this.material
                );
            }

            t = (float) ((-b + Math.sqrt(b * b - a * c)) / a);

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
}
