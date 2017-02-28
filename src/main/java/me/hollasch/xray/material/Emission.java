package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 24, 11:09 PM
 */
public class Emission extends Material {

    @Getter private SurfaceTexture emissionColor;

    public Emission(SurfaceTexture emissionColor) {
        this.emissionColor = emissionColor;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        return new SurfaceInteraction(this.emissionColor.getRGBAt(collision.getPoint()), null, true);
    }
}
