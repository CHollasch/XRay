package me.hollasch.xray.object;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * Created by Connor on 4/21/2017.
 */
public class AABB extends WorldObject {

    @Getter
    private Vec3 min, max;

    public AABB(Vec3 min, Vec3 max) {
        this(min, max, null);
    }

    public AABB(Vec3 min, Vec3 max, Material material) {
        this.min = Vec3.min(min, max);
        this.max = Vec3.max(min, max);

        this.material = material;
    }
    @Override
    public RayCollision rayIntersect(Ray ray, double tMin, double tMax) {
        Vec3 intersection = intersectBox(ray.getOrigin(), ray.getDirection());
        if (intersection.getX() > tMin && intersection.getX() < intersection.getY() && intersection.getX() < tMax) {
            return new RayCollision(intersection.getX(), ray.getPointAt(intersection.getX()), Vec3.of(0,0, 0), this.material);
        } else {
            return null;
        }
    }

    @Override
    public AABB getBoundingBox() {
        return this;
    }

    public boolean doesIntersect(Ray ray) {
        Vec3 tMin = (this.min.subtract(ray.getOrigin())).divide(ray.getDirection());
        Vec3 tMax = (this.max.subtract(ray.getOrigin())).divide(ray.getDirection());

        Vec3 t1 = Vec3.min(tMin, tMax);
        Vec3 t2 = Vec3.max(tMin, tMax);

        if ((t1.getX() > t2.getZ()) || (t1.getZ() > t2.getX()) || (t1.getX() > t2.getY()) || (t1.getY() > t2.getX())) {
            return false;
        }

        return true;
    }

    private Vec3 intersectBox(Vec3 origin, Vec3 direction) {
        Vec3 tMin = (this.min.subtract(origin)).divide(direction);
        Vec3 tMax = (this.max.subtract(origin)).divide(direction);

        Vec3 t1 = Vec3.min(tMin, tMax);
        Vec3 t2 = Vec3.max(tMin, tMax);

        double tNear = Math.max(Math.max(t1.getX(), t1.getY()), t1.getZ());
        double tFar = Math.min(Math.min(t2.getX(), t2.getY()), t2.getZ());

        return Vec3.of(tNear, tFar, 0);
    }

    public static AABB merge(AABB b1, AABB b2) {
        double b1xMin = b1.getMin().getX(), b2xMin = b2.getMin().getX();
        double b1yMin = b1.getMin().getY(), b2yMin = b2.getMin().getY();
        double b1zMin = b1.getMin().getZ(), b2zMin = b2.getMin().getZ();

        double b1xMax = b1.getMax().getX(), b2xMax = b2.getMax().getX();
        double b1yMax = b1.getMax().getY(), b2yMax = b2.getMax().getY();
        double b1zMax = b1.getMax().getZ(), b2zMax = b2.getMax().getZ();

        Vec3 newMin = Vec3.of(Math.min(b1xMin, b2xMin), Math.min(b1yMin, b2yMin), Math.min(b1zMin, b2zMin));
        Vec3 newMax = Vec3.of(Math.max(b1xMax, b2xMax), Math.max(b1yMax, b2yMax), Math.max(b1zMax, b2zMax));

        return new AABB(newMin, newMax, b1.getMaterial());
    }
}
