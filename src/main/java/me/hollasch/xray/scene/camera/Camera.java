package me.hollasch.xray.scene.camera;

import me.hollasch.xray.render.Ray;

/**
 * @author Connor Hollasch
 * @since Feb 24, 11:21 AM
 */
public interface Camera {

    Ray projectRay(double x, double y);
}
