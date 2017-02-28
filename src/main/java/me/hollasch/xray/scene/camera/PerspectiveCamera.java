package me.hollasch.xray.scene.camera;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:16 PM
 */
public class PerspectiveCamera implements Camera {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    // Orientation of the perspective camera
    @Getter private Vec3 origin;
    @Getter private Vec3 lowerLeftCorner;
    @Getter private Vec3 screenWidth;
    @Getter private Vec3 screenHeight;

    // Depth of field parameters.
    @Getter private float lensRadius;
    @Getter private boolean hasDOF = true;

    // Axis in 3space for camera orientation.
    private Vec3 u, v, w;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public PerspectiveCamera(
            final Vec3 origin,
            final Vec3 lookAt,
            final Vec3 up,
            final float vFov,
            final float aspectRatio
    ) {
        this(origin, lookAt, up, vFov, aspectRatio, 1, 1);
        this.hasDOF = false;
    }

    public PerspectiveCamera(
            final Vec3 origin,
            final Vec3 lookAt,
            final Vec3 up,
            final float verticalFOV,
            final float aspectRatio,
            final float aperture,
            final float focus_distance
    ) {
        this.lensRadius = aperture / 2.0f;

        // Take the FOV and calculate the height and width of the projection plane.
        float theta = (float) (verticalFOV * Math.PI / 180f);
        float halfHeight = (float) Math.tan(theta / 2.0f);
        float halfWidth = aspectRatio * halfHeight;

        this.origin = origin;

        // Calculate normalized direction of camera projection (Z axis), use the up vector to cross into projection
        // vector to get a Y axis and cross the Z and Y axis to get the X axis. Creating an axis in 3space for
        // camera orientation.
        this.w = origin.subtract(lookAt).normalize();
        this.u = up.cross(this.w).normalize();
        this.v = this.w.cross(this.u);

        this.lowerLeftCorner = origin.subtract(this.u.multiplyScalar(halfWidth * focus_distance))
                .subtract(this.v.multiplyScalar(halfHeight * focus_distance))
                .subtract(this.w.multiplyScalar(focus_distance));

        this.screenWidth = this.u.multiplyScalar(halfWidth * 2.0f * focus_distance);
        this.screenHeight = this.v.multiplyScalar(halfHeight * 2.0f * focus_distance);
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public Ray projectRay(float x, float y) {
        if (this.hasDOF) {
            Vec3 randomDOF = Vec3.randomInUnitDisk().multiplyScalar(this.lensRadius);
            Vec3 offset = this.u.multiplyScalar(randomDOF.getX()).add(this.v.multiplyScalar(randomDOF.getY()));

            return new Ray(
                    this.origin.add(offset),
                    this.lowerLeftCorner.add(
                            this.screenWidth.multiplyScalar(x)).add(this.screenHeight.multiplyScalar(y)
                    ).subtract(origin).subtract(offset)
            );
        } else {
            return new Ray(
                    this.origin,
                    this.lowerLeftCorner.add(
                            this.screenWidth.multiplyScalar(x)).add(this.screenHeight.multiplyScalar(y)
                    ).subtract(origin));
        }
    }
}
