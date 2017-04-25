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
    @Getter private double intensity = 1d;

    public Emission(SurfaceTexture emissionColor, double intensity) {
        this.emissionColor = emissionColor;
        this.intensity = intensity;
    }

    public SurfaceInteraction scatter(Ray incoming, RayCollision collision) {
        double distance = Vec3.distanceBetween(incoming.getOrigin(), collision.getPoint());
        return new SurfaceInteraction(this.emissionColor.getRGBAt(collision.getPoint()).multiplyScalar(distance * this.intensity), null, true);
    }
}
