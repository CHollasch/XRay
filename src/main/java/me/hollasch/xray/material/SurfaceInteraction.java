package me.hollasch.xray.material;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 24, 12:53 AM
 */
public class SurfaceInteraction {

    @Getter private final Vec3 lightContribution;
    @Getter private final Ray scattered;

    @Getter private final boolean isEmissive;

    public SurfaceInteraction(final Vec3 attenuation, final Ray scattered) {
        this(attenuation, scattered, false);
    }

    public SurfaceInteraction(final Vec3 attenuation, final Ray scattered, boolean isEmissive) {
        this.lightContribution = attenuation;
        this.scattered = scattered;
        this.isEmissive = isEmissive;
    }
}
