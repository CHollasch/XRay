package me.hollasch.xray.gui;

import me.hollasch.xray.light.Light;
import me.hollasch.xray.light.PointLight;
import me.hollasch.xray.material.Diffuse;
import me.hollasch.xray.material.Emission;
import me.hollasch.xray.material.Glossy;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.texture.SingleColorTexture;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Quad;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.scene.Scene;
import me.hollasch.xray.scene.camera.PerspectiveCamera;

/**
 * @author Connor Hollasch
 * @since April 19, 10:30 PM
 */
public class DemoScene extends Scene {

    private Vec3[][] quads = {
            // Left wall
            {v(-1, 0, 2), v(-1, 0, -1), v(-1, 2, 2), v(-1, 2, -1), v(.1, .8, .1)},

            // Right wall
            {v(1, 0, 2), v(1, 0, -1), v(1, 2, 2), v(1, 2, -1), v(.8, .1, .1)},

            //Floor
            {v(-1, 0, 2), v(1, 0, 2), v(-1, 0, -1), v(1, 0, -1)},

            // Roof
            {v(-1, 2, 2), v(-.5, 2, 2), v(-1, 2, -1), v(-.5, 2, -1)},
            {v(.5, 2, 2), v(1, 2, 2), v(.5, 2, -1), v(1, 2, -1)},
            {v(-.5, 2, -1), v(.5, 2, -1), v(-.5, 2, -.5), v(.5, 2, -.5)},
            {v(-.5, 2, 0), v(.5, 2, 0), v(-.5, 2, 2), v(.5, 2, 2)},
            //{v(-1, 2, 2), v(1, 2, 2), v(-1, 2, -1), v(1, 2, -1)},

            // Front
            {v(-1, 0, -1), v(1, 0, -1), v(-1, 2, -1), v(1, 2, -1)},

            // Back
            {v(-1, 0, 2), v(1, 0, 2), v(-1, 2, 2), v(1, 2, 2)},
    };

    static Vec3 v(double x, double y, double z) {
        return Vec3.of(x, y, z);
    }

    public DemoScene(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);

        initScene();
    }

    private void initScene() {
        setBackground(new SurfaceTexture() {
            @Override
            public Vec3 getRGBAt(Vec3 vec) {
                return v(lerp(-.1f, 2f, vec.getY()) * .4f, .1f, .5f);
            }

            private float lerp(float v0, float v1, float t) {
                return (1 - t) * v0 + t * v1;
            }
        });

        PerspectiveCamera camera = new PerspectiveCamera(
                v(0, .8, 2),
                v(0, .5, 0),
                v(0, 1, 0),
                90,
                ((float) getScreenWidth() / getScreenHeight())
        );

        setCameraObject(camera);

        Material diffuseWhite = new Diffuse(new SingleColorTexture(Vec3.of(1)));

        for (Vec3[] quad : this.quads) {
            Vec3 a = quad[0], b = quad[1], c = quad[2], d = quad[3];
            if (quad.length > 4) {
                add(new Quad(a, b, c, d, new Glossy(new SingleColorTexture(quad[4]), .05f)));
            } else {
                add(new Quad(a, b, c, d, diffuseWhite));
            }
        }

        add(new Quad(
                v(-.5, 2, 0),
                v(-.5, 2, -.5),
                v(.5, 2, 0),
                v(.5, 2, -.5),
                new Emission(new SingleColorTexture(Vec3.of(6f))))
        );

        add(new Sphere(v(-.25, .5, 0), .5f, new Diffuse(new SingleColorTexture(v(0, .3, .7)))));
        add(new Sphere(v(.6, .25, .7), .25f, new Glossy(new SingleColorTexture(v(.1f, .5f, .3f)), 0.01f)));
    }

    private void add(WorldObject object) {
        getSceneObjects().add(object);
    }

    private void add(Light light) {
        getSceneLights().add(light);
    }
}
