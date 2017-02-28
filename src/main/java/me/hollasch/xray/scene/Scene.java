package me.hollasch.xray.scene;

import lombok.Getter;
import lombok.Setter;
import me.hollasch.xray.light.Light;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.scene.camera.Camera;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:16 PM
 */
public class Scene {

    @Getter
    @Setter
    private Camera cameraObject;

    @Getter private int screenWidth;
    @Getter private int screenHeight;

    @Getter
    @Setter
    private Vec3 backgroundColor = Vec3.of(0.15f, 0.15f, 0.15f);

    @Getter private Set<WorldObject> sceneObjects;
    @Getter private Set<Light>       sceneLights;

    public Scene(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.sceneObjects = new HashSet<>();
        this.sceneLights = new HashSet<>();
    }
}
