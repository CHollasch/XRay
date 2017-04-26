package me.hollasch.xray.object.surface;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.AABB;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 27, 9:40 PM
 */
public class Triangle extends Surface {

    @Getter private Vec3 a, b, c;

    private Vec3 edgeA, edgeB;
    private Vec3 normal;

    private AABB boundingBox;

    public Triangle(Vec3 a, Vec3 b, Vec3 c, Material material) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.material = material;

        this.edgeA = b.subtract(a);
        this.edgeB = c.subtract(a);

        this.normal = this.edgeA.cross(this.edgeB).normalize();

        double xMin = Math.min(Math.min(a.getX(), b.getX()), c.getX());
        double yMin = Math.min(Math.min(a.getY(), b.getY()), c.getY());
        double zMin = Math.min(Math.min(a.getZ(), b.getZ()), c.getZ());

        double xMax = Math.max(Math.max(a.getX(), b.getX()), c.getX());
        double yMax = Math.max(Math.max(a.getY(), b.getY()), c.getY());
        double zMax = Math.max(Math.max(a.getZ(), b.getZ()), c.getZ());

        this.boundingBox = new AABB(Vec3.of(xMin, yMin, zMin), Vec3.of(xMax, yMax, zMax), material);
    }

    @Override
    public RayCollision rayIntersect(Ray ray, double tMin, double tMax) {
        Vec3 o = ray.getOrigin();
        Vec3 d = ray.getDirection();

        Vec3 p = d.cross(this.edgeB);
        double det = this.edgeA.dot(p);

        if (det > -tMin && det < tMin) {
            return null;
        }

        double inv_det = 1.0 / det;

        Vec3 t = o.subtract(this.a);
        double u = t.dot(p) * inv_det;

        if (u < 0.f || u > 1.f) {
            return null;
        }

        Vec3 q = t.cross(this.edgeA);
        double v = d.dot(q) * inv_det;

        if (v < 0.f || u + v > 1.f) {
            return null;
        }

        double tVal = this.edgeB.dot(q) * inv_det;

        if (tVal > tMin && tVal < tMax) {
            Vec3 collision = ray.getPointAt(tVal);
            double direction = this.normal.dot(d);

            Vec3 normal = this.normal.multiplyScalar(direction > 0 ? -1 : 1);

            return new RayCollision(tVal, collision, normal, this.material);
        }

        return null;
    }

    @Override
    public AABB getBoundingBox() {
        return this.boundingBox;
    }
}
