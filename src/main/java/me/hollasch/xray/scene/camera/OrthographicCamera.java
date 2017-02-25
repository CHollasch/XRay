package me.hollasch.xray.scene.camera;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 24, 11:25 AM
 */
public class OrthographicCamera implements Camera {

    @Getter private final Vec3 origin;
    @Getter private final Vec3 lowerLeftCorner;
    @Getter private final Vec3 rows;
    @Getter private final Vec3 cols;

    private Vec3 u, v, w;

    public OrthographicCamera(final Vec3 origin, final Vec3 lookAt, final Vec3 up, final float width, final float height) {
        this.origin = origin;

        this.w = origin.subtract(lookAt).normalize();
        this.u = up.negate().cross(this.w).normalize();
        this.v = this.w.cross(this.u);

        this.lowerLeftCorner = origin.subtract(this.u.multiplyScalar(width / 2f)).subtract(this.v.multiplyScalar(height / 2f)).subtract(this.w);
        this.rows = this.v.multiplyScalar(height);
        this.cols = this.u.multiplyScalar(width);
    }

    public Ray projectRay(float x, float y) {
        return new Ray(this.lowerLeftCorner.add(this.rows.multiplyScalar(y)).add(this.cols.multiplyScalar(x)), this.w);
    }
}
