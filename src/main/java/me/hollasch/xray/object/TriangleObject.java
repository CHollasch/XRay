package me.hollasch.xray.object;

import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 27, 9:40 PM
 */
public class TriangleObject extends WorldObject {

    private Vec3 vertexOne, vertexTwo, vertexThree;

    @Override
    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        Vec3 edgeOne, edgeTwo;
        Vec3 p, q, t;
        float det, inv_det, u, v;
        float tValue;

        edgeOne = this.vertexTwo.subtract(this.vertexOne);
        edgeTwo = this.vertexThree.subtract(this.vertexOne);

        p = ray.getDirection().cross(edgeTwo);
        det = edgeOne.dot(p);

        if (det > -tMin && det < tMin) {
            return null;
        }

        inv_det = 1.0f / det;
        t = ray.getOrigin().subtract(this.vertexOne);
        u = t.dot(p) * inv_det;

        if (u < 0f || u > 1f) {
            return null;
        }

        q = t.cross(edgeOne);
        v = ray.getDirection().dot(q) * inv_det;

        if (v < 0f || u + v > 1f) {
            return null;
        }

        tValue = edgeTwo.dot(q) * inv_det;

        if (tValue < tMax && tValue > tMin) {
            //return new RayCollision(tValue, )
        }

        return null;
    }
}
