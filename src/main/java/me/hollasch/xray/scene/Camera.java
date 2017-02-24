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

    @Getter private float lensRadius;
    @Getter private boolean hasDOF = true;

    private Vec3 u, v, w;

    public Camera(Vec3 origin, Vec3 lookAt, Vec3 up, float vFov, float aspectRatio) {
        this(origin, lookAt, up, vFov, aspectRatio, 1, 1);
        this.hasDOF = false;
    }

    public Camera(Vec3 origin, Vec3 lookAt, Vec3 up, float verticalFOV, float aspectRatio, float aperture, float focus_distance) {
        this.lensRadius = aperture / 2.0f;

        float theta = (float) (verticalFOV * Math.PI / 180f);
        float halfHeight = (float) Math.tan(theta / 2.0f);
        float halfWidth = aspectRatio * halfHeight;

        this.origin = origin;

        this.w = origin.subtract(lookAt).normalize();
        this.u = up.cross(this.w).normalize();
        this.v = this.w.cross(this.u);

        this.lowerLeftCorner = origin.subtract(this.u.multiplyScalar(halfWidth * focus_distance))
                .subtract(this.v.multiplyScalar(halfHeight * focus_distance))
                .subtract(this.w.multiplyScalar(focus_distance));

        this.cols = this.u.multiplyScalar(halfWidth * 2.0f * focus_distance);
        this.rows = this.v.multiplyScalar(halfHeight * 2.0f * focus_distance);
    }

    public Ray createRay(float x, float y) {
        if (this.hasDOF) {
            Vec3 randomDOF = Vec3.randomInUnitDisk().multiplyScalar(this.lensRadius);
            Vec3 offset = this.u.multiplyScalar(randomDOF.getX()).add(this.v.multiplyScalar(randomDOF.getY()));

            return new Ray(
                    this.origin.add(offset),
                    this.lowerLeftCorner.add(
                            this.cols.multiplyScalar(x)).add(this.rows.multiplyScalar(y)
                    ).subtract(origin).subtract(offset)
            );
        } else {
            return new Ray(
                    this.origin,
                    this.lowerLeftCorner.add(
                            this.cols.multiplyScalar(x)).add(this.rows.multiplyScalar(y)
                    ).subtract(origin));
        }
    }
}
