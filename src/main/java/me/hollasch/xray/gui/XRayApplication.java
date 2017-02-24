package me.hollasch.xray.gui;

import me.hollasch.xray.material.impl.Diffuse;
import me.hollasch.xray.material.impl.Glossy;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.render.Render;
import me.hollasch.xray.scene.Camera;
import me.hollasch.xray.scene.Scene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:26 PM
 */
public class XRayApplication {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 250;

    public static void main(String[] args) throws IOException {
        Scene scene = new Scene(WIDTH, HEIGHT);

        scene.setCameraObject(new Camera(new Vec3(0, 0, 0), new Vec3(-2, -1, -1), new Vec3(4, 0, 0), new Vec3(0, 2, 0)));

        scene.getSceneObjects().add(new Sphere(Vec3.of(0, 0, -1), .6f, new Diffuse(new Vec3(0.8f, 0.3f, 0.3f))));
        scene.getSceneObjects().add(new Sphere(Vec3.of(0, -100.5f, -1), 100, new Diffuse(new Vec3(0.2f, 0.3f, 0.3f))));

        scene.getSceneObjects().add(new Sphere(Vec3.of(1, 0, -1), 0.5f, new Glossy(Vec3.of(0.8f, 0.6f, 0.2f), .2f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), 0.5f, new Glossy(Vec3.of(0.8f, 0.8f, 0.8f), .05f)));

        Properties renderProperties = new Properties();
        renderProperties.put("samples", 100);
        renderProperties.put("blur_factor", 1.0f);

        BufferedImage rendered = Render.renderToImage(scene, renderProperties);

        File file = new File("render.png");

        if (!file.exists()) {
            file.createNewFile();
        }

        ImageIO.write(rendered, "PNG", file);
    }
}
