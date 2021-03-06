package me.hollasch.xray.material.texture;

import me.hollasch.xray.math.Vec3;

/**
 * @author Connor Hollasch
 * @since Feb 23, 9:26 PM
 */
public interface SurfaceTexture {

    Vec3 getRGBAt(Vec3 vec3);
}
