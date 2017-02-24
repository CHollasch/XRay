package me.hollasch.xray.scene;

import lombok.Getter;
import lombok.Setter;
import me.hollasch.xray.object.WorldObject;

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

    @Getter private Set<WorldObject> sceneObjects;

    public Scene(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.sceneObjects = new HashSet<WorldObject>();
    }
}
