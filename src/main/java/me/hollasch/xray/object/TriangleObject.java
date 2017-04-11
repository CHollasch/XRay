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
 * @since Feb 27, 9:40 PM
 */
public class TriangleObject extends WorldObject {

    @Getter private Material material;
    @Getter private Vec3 vertexOne, vertexTwo, vertexThree;

    private Vec3 u, v, normal;
    private Plane plane;

    public TriangleObject(Vec3 vertexOne, Vec3 vertexTwo, Vec3 vertexThree, Material material) {
        this.vertexOne = vertexOne;
        this.vertexTwo = vertexTwo;
        this.vertexThree = vertexThree;
        this.material = material;

        this.u = vertexOne.subtract(vertexTwo);
        this.v = vertexOne.subtract(vertexThree);
        this.normal = this.u.cross(this.v).normalize();

        float a = this.normal.getX();
        float b = this.normal.getY();
        float c = this.normal.getZ();

        float d = vertexOne.getX() * this.normal.getX()
                + vertexOne.getY() * this.normal.getY()
                + vertexOne.getZ() * this.normal.getZ();

        this.plane = new Plane(a, b, c, -d, this.material);
    }

    @Override
    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        RayCollision planeHit = this.plane.rayIntersect(ray, tMin, tMax);

        if (planeHit == null) {
            return null;
        }

        double uu, uv, vv, wu, wv, D;
        uu = this.u.dot(this.u);
        uv = this.u.dot(this.v);
        vv = this.v.dot(this.v);

        Vec3 w = planeHit.getPoint().add(this.vertexOne.negate());

        wu = w.dot(this.u);
        wv = w.dot(this.v);
        D = uv * uv - uu * vv;

        double s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0 || s > 1) {
            return null;
        }

        t = (uv * wu - uu * wv) / D;
        if (t < 0 || (s + t) > 1) {
            return null;
        }

        return new RayCollision(planeHit.getTValue(), planeHit.getPoint(), planeHit.getNormal(), this.material);
    }
}
