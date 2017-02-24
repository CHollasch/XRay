package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

/**
 * @author Connor Hollasch
 * @since Feb 23, 9:26 PM
 */
public abstract class Material {

    @Getter private SurfaceTexture surfaceTexture;

    public abstract SurfaceInteraction scatter(Ray incoming, RayCollision collision);
}
