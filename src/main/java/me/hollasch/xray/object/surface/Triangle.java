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
public strictfp class Triangle extends Surface {

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
        Vec3 p0 = this.a;
        Vec3 p1 = this.b;
        Vec3 p2 = this.c;

        Vec3 origin = ray.getOrigin();

        Vec3 p0t = p0.subtract(origin);
        Vec3 p1t = p1.subtract(origin);
        Vec3 p2t = p2.subtract(origin);

        int kz = ray.getDirection().abs().maxDimensionIndex();
        int kx = kz + 1;
        if (kx == 3) kx = 0;
        int ky = kx + 1;
        if (ky == 3) ky = 0;

        Vec3 d = ray.getDirection().permute(kx, ky, kz);
        p0t = p0t.permute(kx, ky, kz);
        p1t = p1t.permute(kx, ky, kz);
        p2t = p2t.permute(kx, ky, kz);

        double sx = -d.getX() / d.getZ();
        double sy = -d.getY() / d.getZ();
        double sz = 1.0 / d.getZ();

        p0t = p0t.add(sx * p0t.getZ(), sy * p0t.getZ(), 0);
        p1t = p1t.add(sx * p1t.getZ(), sy * p1t.getZ(), 0);
        p2t = p2t.add(sx * p2t.getZ(), sy * p2t.getZ(), 0);

        double e0 = p1t.getX() * p2t.getY() - p1t.getY() * p2t.getX();
        double e1 = p2t.getX() * p0t.getY() - p2t.getY() * p0t.getX();
        double e2 = p0t.getX() * p1t.getY() - p0t.getY() * p1t.getX();

        if ((e0 < 0 || e1 < 0 || e2 < 0) && (e0 > 0 || e1 > 0 || e2 > 0)) {
            return null;
        }

        double det = e0 + e1 + e2;
        if (det == 0) {
            return null;
        }

        p0t = p0t.multiply(1, 1, sz);
        p1t = p1t.multiply(1, 1, sz);
        p2t = p2t.multiply(1, 1, sz);

        double tScale = e0 * p0t.getZ() + e1 * p1t.getZ() + e2 * p2t.getZ();

        if ((det < 0 && (tScale >= 0 || tScale < tMax * det)) || (det > 0 && (tScale <= 0 || tScale > tMax * det))) {
            return null;
        }

        double iDet = 1.0 / det;
        double t = tScale * iDet;

        double maxZt = Vec3.of(p0t.getZ(), p1t.getZ(), p2t.getZ()).abs().maxComponent();
        double deltaZ = gamma(3) * maxZt;

        double maxXt = Vec3.of(p0t.getX(), p1t.getX(), p2t.getX()).abs().maxComponent();
        double maxYt = Vec3.of(p0t.getY(), p1t.getY(), p2t.getY()).abs().maxComponent();

        double deltaX = gamma(5) * (maxXt + maxZt);
        double deltaY = gamma(5) * (maxYt + maxZt);

        double deltaE = 2 * (gamma(2) * maxXt * maxYt + deltaY * maxXt + deltaX * maxYt);
        double maxE = Vec3.of(e0, e1, e2).abs().maxComponent();
        double deltaT = 3 * (gamma(3) * maxE * maxZt + deltaE * maxZt + deltaZ * maxE) * Math.abs(iDet);

        if (t < deltaT) {
            return null;
        }

        double direction = this.normal.dot(ray.getDirection());
        Vec3 normal = this.normal.multiplyScalar(direction > 0 ? -1 : 1);

        return new RayCollision(t, ray.getPointAt(t).add(normal.multiplyScalar(Math.ulp(1.0) * 3)), normal, this.material);
    }

    private double gamma(int n) {
        return (n *  Math.ulp(1.0)) / (1 - n * Math.ulp(1.0));
    }

    @Override
    public AABB getBoundingBox() {
        return this.boundingBox;
    }
}
