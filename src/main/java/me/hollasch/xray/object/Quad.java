package me.hollasch.xray.object;

import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * Created by Connor on 4/19/17.
 */
public class Quad extends WorldObject {

    private Triangle t1, t2;

    public Quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, Material material) {
        this.material = material;

        //A    0, 0, 0   \
        //B    0, 0, 1   |      0, 0, 0     0, 0, 0
        //C    0, 1, 0   |  ->  0, 0, 1  &  0, 1, 0  ->  (A, B, D) & (A, C, D)
        //D    0, 1, 1   /      0, 1, 1     0, 1, 1

        this.t1 = new Triangle(a, b, d, material);
        this.t2 = new Triangle(a, c, d, material);
    }

    @Override
    public RayCollision rayIntersect(Ray ray, float tMin, float tMax) {
        RayCollision t1Hit = t1.rayIntersect(ray, tMin, tMax);

        if (t1Hit == null) {
            return t2.rayIntersect(ray, tMin, tMax);
        }

        return t1Hit;
    }
}
