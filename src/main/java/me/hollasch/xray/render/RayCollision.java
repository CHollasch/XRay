package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.math.Vec3;

/**
 * @author Connor Hollasch
 * @since Feb 23, 11:37 PM
 */
public class RayCollision {

    @Getter private float tValue;
    @Getter private Vec3 point;
    @Getter private Vec3 normal;

    @Getter private Material material;

    public RayCollision(float tValue, Vec3 point, Vec3 normal, Material material) {
        this.tValue = tValue;
        this.point = point;
        this.normal = normal;
        this.material = material;
    }
}
