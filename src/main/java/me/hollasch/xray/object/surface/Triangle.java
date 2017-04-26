package me.hollasch.xray.object.surface;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 27, 9:40 PM
 */
public class Triangle extends Surface {

    private static final float EPSILON = 0.0001f;

    @Getter private Vec3 a, b, c;

    private Vec3 edgeA, edgeB;
    private Vec3 normal;

    public Triangle(Vec3 a, Vec3 b, Vec3 c, Material material) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.material = material;

        this.edgeA = b.subtract(a);
        this.edgeB = c.subtract(a);

        this.normal = this.edgeA.cross(this.edgeB).normalize();
    }

    @Override
    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        Vec3 o = ray.getOrigin();
        Vec3 d = ray.getDirection();

        Vec3 p = d.cross(this.edgeB);
        float det = this.edgeA.dot(p);

        if (det > -EPSILON && det < EPSILON) {
            return null;
        }

        float inv_det = 1.0f / det;

        Vec3 t = o.subtract(this.a);
        float u = t.dot(p) * inv_det;

        if (u < 0.f || u > 1.f) {
            return null;
        }

        Vec3 q = t.cross(this.edgeA);
        float v = d.dot(q) * inv_det;

        if (v < 0.f || u + v > 1.f) {
            return null;
        }

        float tVal = this.edgeB.dot(q) * inv_det;

        if (tVal > tMin && tVal < tMax) {
            Vec3 collision = ray.getPointAt(tVal);
            float direction = this.normal.dot(d);

            return new RayCollision(tVal, collision, normal.multiplyScalar(direction > 0 ? -1 : 1), this.material);
        }

        return null;
    }
}
