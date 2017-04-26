package me.hollasch.xray.gui;

import me.hollasch.xray.light.Light;
import me.hollasch.xray.material.*;
import me.hollasch.xray.material.texture.SingleColorTexture;
import me.hollasch.xray.material.texture.SurfaceTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.object.surface.Quad;
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
            //{v(-1, 0, 2), v(1, 0, 2), v(-1, 2, 2), v(1, 2, 2)},
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
                return Vec3.of(0); //v(lerp(-.1, 2, vec.getY()) * .4, .1, .5);
            }

            private double lerp(double v0, double v1, double t) {
                return (1 - t) * v0 + t * v1;
            }
        });

        PerspectiveCamera camera = new PerspectiveCamera(
                v(0, .8, 2),
                v(0, .5, 0),
                v(0, 1, 0),
                90,
                getScreenWidth() / getScreenHeight(),
                .075,
                2
        );

        setCameraObject(camera);

        Material diffuseWhite = new Diffuse(new SingleColorTexture(Vec3.of(1)));

        for (Vec3[] quad : this.quads) {
            Vec3 a = quad[0], b = quad[1], c = quad[2], d = quad[3];
            if (quad.length > 4) {
                //add(new Quad(a, b, c, d, new Glossy(new SingleColorTexture(quad[4]), .05)));
            } else {
                //add(new Quad(a, b, c, d, diffuseWhite));
            }
        }

        add(new Quad(
                v(-2.5, 3, 0),
                v(-2.5, 3, -2.5),
                v(-1, 3, 0),
                v(-1, 3, -2.5),
                new Emission(new SingleColorTexture(Vec3.of(.8, .1, .3)), 30))
        );

        add(new Quad(
                v(2.5, 3, 0),
                v(2.5, 3, -2.5),
                v(1, 3, 0),
                v(1, 3, -2.5),
                new Emission(new SingleColorTexture(Vec3.of(.2, .8, .3)), 30))
        );

        //add(new AABB(Vec3.of(-1, 0, -1), Vec3.of(1, 2, 2), new Diffuse(new SingleColorTexture(v(.7, .1, .2)))));
        //add(new Quad(v(-100, 0, 100), v(100, 0, 100), v(-100, 0, -100), v(100, 0, -100), new Diffuse(new SingleColorTexture(v(.9, .9, .9)))));
        add(new Sphere(v(0, -500, 0), 500, new Diffuse(new SingleColorTexture(Vec3.of(.9)))));
        //add(new Sphere(v(-.25, .5, 0), .5, new Diffuse(new SingleColorTexture(v(0, .3, .7)))));
        //add(new Sphere(v(.6, .25, .7), .25, new Glossy(new SingleColorTexture(v(.1, .5, .3)), 0.01)));
        //add(new Sphere(v(.1, .35, 1.1), .35, new Glass(1.33)));
        //add(new Sphere(v(.1, .345, 1.1), .34, new Glass(.9)));

        for (int i = 0; i < 100; ++i) {
            double xR = Math.random() * 2;
            double yR = Math.random() * 1.5;
            double zR = Math.random() * 1.5;

            Vec3 loc = Vec3.of(xR - 1, .1 + yR, zR - 1);
            add(new Sphere(loc, (Math.random() / 6) + .05, new Glass(Math.random() + 1,
                    new SingleColorTexture(v(Math.random()/5 + .79, Math.random()/5 + .79, Math.random()/5 + .79)))));
        }
    }

    private void add(WorldObject object) {
        getSceneObjects().add(object);
    }

    private void add(Light light) {
        getSceneLights().add(light);
    }
}