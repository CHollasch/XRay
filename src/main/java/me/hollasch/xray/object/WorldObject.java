package me.hollasch.xray.object;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 23, 9:24 PM
 */
public abstract class WorldObject {

    @Getter protected Material material;

    public final void setMaterial(Material material) {
        this.material = material;
    }

    public abstract RayCollision rayIntersect(Ray ray, float tMin, float tMax);
}
