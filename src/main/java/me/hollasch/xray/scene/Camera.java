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
    @Getter private Vec3 bottomLeftCorner;
    @Getter private Vec3 cols;
    @Getter private Vec3 rows;

    public Camera(Vec3 origin, Vec3 bottomLeftCorner, Vec3 cols, Vec3 rows) {
        this.origin = origin;
        this.bottomLeftCorner = bottomLeftCorner;
        this.cols = cols;
        this.rows = rows;
    }

    public Ray createRay(float u, float v) {
        return new Ray(this.origin, this.bottomLeftCorner.add(this.cols.multiplyScalar(u)).add(this.rows.multiplyScalar(v)).subtract(origin));
    }
}
