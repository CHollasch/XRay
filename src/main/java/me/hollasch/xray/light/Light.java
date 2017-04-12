package me.hollasch.xray.light;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;
import me.hollasch.xray.render.MultithreadedRenderer;
import me.hollasch.xray.render.Renderer;

/**
 * @author Connor Hollasch
 * @since Feb 27, 4:18 PM
 */
public abstract class Light {

    @Getter private Vec3 lightLocation;
    @Getter private Vec3 lightColor;
    @Getter private float intensity;

    public Light(Vec3 lightLocation, Vec3 lightColor, float intensity) {
        this.lightLocation = lightLocation;
        this.lightColor = lightColor;
        this.intensity = intensity;
    }

    public abstract Vec3 getLightContribution(Renderer renderer, RayCollision objectCollision, Ray toObject);
}
