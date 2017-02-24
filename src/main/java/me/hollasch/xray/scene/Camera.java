package me.hollasch.xray.scene;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:16 PM
 */
public class Camera {

    @Getter private Vec3 origin;
    @Getter private Vec3 lowerLeftCorner;
    @Getter private Vec3 cols;
    @Getter private Vec3 rows;

    public Camera(Vec3 origin, Vec3 lookAt, Vec3 up, float verticalFOV, float aspectRatio) {
        Vec3 u, v, w;

        float theta = (float) (verticalFOV * Math.PI / 180f);
        float halfHeight = (float) Math.tan(theta / 2.0f);
        float halfWidth = aspectRatio * halfHeight;

        this.origin = origin;

        w = origin.subtract(lookAt).normalize();
        u = up.cross(w).normalize();
        v = w.cross(u);

        this.lowerLeftCorner = origin.subtract(u.multiplyScalar(halfWidth)).subtract(v.multiplyScalar(halfHeight)).subtract(w);
        this.cols = u.multiplyScalar(halfWidth * 2.0f);
        this.rows = v.multiplyScalar(halfHeight * 2.0f);
    }

    public Ray createRay(float u, float v) {
        return new Ray(
                this.origin,
                this.lowerLeftCorner.add(
                        this.cols.multiplyScalar(u)).add(this.rows.multiplyScalar(v)
                ).subtract(origin)
        );
    }
}
