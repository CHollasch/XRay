package me.hollasch.xray.gui;

import me.hollasch.xray.material.impl.Diffuse;
import me.hollasch.xray.material.impl.Glass;
import me.hollasch.xray.material.impl.Glossy;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.render.RenderEngine;
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

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    public static void main(String[] args) throws IOException {
        Scene scene = new Scene(WIDTH, HEIGHT);

        Vec3 origin = Vec3.of(3, 3, 2);
        Vec3 lookAt = Vec3.of(0, 0, -1);
        float distanceToFocus = origin.subtract(lookAt).length();

        scene.setCameraObject(new Camera(origin, lookAt, Vec3.of(0, 1, 0), 80, (float) WIDTH / HEIGHT));

        scene.getSceneObjects().add(new Sphere(Vec3.of(0, 0, -1), .6f, new Diffuse(new Vec3(0.8f, 0.3f, 0.3f))));
        scene.getSceneObjects().add(new Sphere(Vec3.of(0, -1000f, -1), 999.5f, new Diffuse(new Vec3(0.2f, 0.3f, 0.3f))));

        scene.getSceneObjects().add(new Sphere(Vec3.of(1, 0, -1), 0.5f, new Glossy(Vec3.of(0.8f, 0.6f, 0.2f), .2f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), 0.5f, new Glass(1.5f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), -0.45f, new Glass(1.5f)));

        Properties renderProperties = new Properties();
        renderProperties.put("samples", 512);
        renderProperties.put("blur_factor", 1.0f);

        BufferedImage rendered = RenderEngine.renderToImage(scene, renderProperties);

        File file = new File("render.png");

        if (!file.exists()) {
            file.createNewFile();
        }

        ImageIO.write(rendered, "PNG", file);
    }
}
