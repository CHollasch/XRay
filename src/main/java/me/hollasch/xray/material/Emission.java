package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 11:09 PM
 */
public class Emission extends Material {

    @Getter private Vec3 albedo;

    public Emission(Vec3 albedo) {
        this.albedo = albedo;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        return null;
    }
}
