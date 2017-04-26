package me.hollasch.xray.light;

import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;
import me.hollasch.xray.render.Renderer;

/**
 * @author Connor Hollasch
 * @since Feb 27, 4:31 PM
 */
public class PointLight extends Light {

    public PointLight(Vec3 lightLocation, Vec3 lightColor, float intensity) {
        super(lightLocation, lightColor, intensity);
    }

    @Override
    public Vec3 getLightContribution(Renderer renderer, RayCollision objectCollision, Ray toObject) {
        Vec3 toLight = getLightLocation().subtract(objectCollision.getPoint()).normalize();
        Ray lightRay = new Ray(objectCollision.getPoint(), toLight);

        RayCollision obstructing = renderer.findObjectCollision(lightRay);

        if (obstructing != null) {
            return new Vec3();
        }

        double attenuation = 1.0 / getLightLocation().subtract(objectCollision.getPoint()).length();
        return getLightColor().multiplyScalar(attenuation).multiplyScalar(getIntensity());
    }
}
