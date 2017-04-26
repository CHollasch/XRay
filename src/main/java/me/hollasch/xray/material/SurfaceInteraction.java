package me.hollasch.xray.material;

import lombok.Getter;
import lombok.Setter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 24, 12:53 AM
 */
public class SurfaceInteraction {

    @Getter
    private final Vec3 lightContribution;

    @Getter
    private final Ray scattered;

    @Getter
    @Setter
    private boolean isEmissive = false;

    public SurfaceInteraction(final Vec3 attenuation, final Ray scattered) {
        this.lightContribution = attenuation;
        this.scattered = scattered;
    }
}
