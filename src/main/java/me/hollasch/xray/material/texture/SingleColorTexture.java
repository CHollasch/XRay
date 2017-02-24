package me.hollasch.xray.material.texture;

import lombok.AllArgsConstructor;
import me.hollasch.xray.math.Vec3;

/**
 * @author Connor Hollasch
 * @since Feb 23, 9:27 PM
 */
@AllArgsConstructor
public class SingleColorTexture implements SurfaceTexture {

    private final int color;

    public int getRGBAt(Vec3 vec3) {
        return this.color;
    }
}
