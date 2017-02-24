package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 24, 12:53 AM
 */
public class SurfaceInteraction {

    @Getter private final Vec3 attenuation;
    @Getter private final Ray scattered;

    public SurfaceInteraction(final Vec3 attenuation, final Ray scattered) {
        this.attenuation = attenuation;
        this.scattered = scattered;
    }
}
