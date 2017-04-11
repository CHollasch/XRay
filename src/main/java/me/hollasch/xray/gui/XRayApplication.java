package me.hollasch.xray.gui;

import me.hollasch.xray.light.PointLight;
import me.hollasch.xray.material.*;
import me.hollasch.xray.material.texture.SingleColorTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.object.TriangleObject;
import me.hollasch.xray.render.RenderProperties;
import me.hollasch.xray.render.Renderer;
import me.hollasch.xray.render.TileTracer;
import me.hollasch.xray.scene.Scene;
import me.hollasch.xray.scene.camera.PerspectiveCamera;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:26 PM
 */
public class XRayApplication {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    private static Scene scene;

    private static Renderer renderer;
    private static Vec3[][] pixelData;

    private static JPanel renderPanel = new JPanel() {
        {
            setSize(WIDTH, HEIGHT);
        }

        @Override
        public void paint(Graphics g) {
            if (pixelData == null) {
                return;
            }

            for (int i = 0; i < pixelData.length; ++i) {
                for (int j = 0; j < pixelData[i].length; ++j) {
                    Vec3 color = pixelData[i][j];

                    if (color == null) {
                        g.setColor(Color.black);
                    } else {
                        int rgb = color.toRGB();
                        g.setColor(new Color(rgb));
                    }

                    g.drawLine(i, j, i, j);
                }
            }
        }
    };

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("XRay - Connor Hollasch");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JButton render = new JButton("Render");
        render.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (renderer != null && renderer.isRendering()) {
                    return;
                }

                pixelData = new Vec3[WIDTH][HEIGHT];
                renderer = new Renderer(scene, RenderProperties.SAMPLE_COUNT.get(4), RenderProperties.TILE_SIZE_X.get(256), RenderProperties.TILE_SIZE_Y.get(256));
                renderer.registerProgressListener(new Renderer.Listener() {
                    @Override
                    public void onPixelFinish(int x, int y, Vec3 color) {
                        pixelData[x][y] = color;
                        renderPanel.repaint();
                    }

                    public void onTileFinish(TileTracer tracer) {}
                    public void onRenderFinish(Vec3[][] finalImage) {
                        try {
                            File file = new File("render.png");

                            if (!file.exists()) {
                                file.createNewFile();
                            }

                            ImageIO.write(renderer.writeToImage(), "PNG", file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                renderer.render();
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(renderPanel, BorderLayout.CENTER);
        frame.add(render, BorderLayout.PAGE_END);

        frame.setVisible(true);

        scene = new Scene(WIDTH, HEIGHT);
        scene.setBackgroundColor(Vec3.of(0.4f, 0.2f, 0.8f));
        //scene.getSceneLights().add(new PointLight(Vec3.of(0, 2, 0), Vec3.of(1f, 0f, 0f), 1f));

        Vec3 origin = Vec3.of(1, 1, 2);
        Vec3 lookAt = Vec3.of(0, 0, -1);
        float distanceToFocus = origin.subtract(lookAt).length();

        scene.setCameraObject(new PerspectiveCamera(origin, lookAt, Vec3.of(0, 1, 0), 80, (float) WIDTH / HEIGHT));
        //scene.setCameraObject(new OrthographicCamera(origin, lookAt, Vec3.of(0, 1, 0), 15, 15));

        //scene.getSceneObjects().add(new Sphere(Vec3.of(0, 0, -1), .6f, new Emission(new Vec3(2.8f, 2.3f, 2.3f))));
        //scene.getSceneObjects().add(new Sphere(Vec3.of(0, 0, 1), .5f, new Diffuse(Vec3.of(.5f, .8f, .8f))));
        scene.getSceneObjects().add(new Sphere(Vec3.of(0, -1000f, -1), 999.5f, new Diffuse(new SingleColorTexture(new Vec3(0.2f, 0.5f, 0.3f)))));

        for (int i = 0; i < 100; ++i) {
            Material m = Math.random() > .66 ? Math.random() > .5 ? new Diffuse(new SingleColorTexture(Vec3.rand())) : new Glossy(new SingleColorTexture(Vec3.rand()), (float) (Math.random() * .15f)) : new Glass((float) (Math.random() + 1));
            scene.getSceneObjects().add(new Sphere(Vec3.of(
                    (float) (Math.random() * 50 - 25f),
                    (float) (Math.random() * 3),
                    (float) (Math.random() * 50 - 25f)
            ), (float) (Math.random() + .5f), m));
        }

        scene.getSceneObjects().add(new TriangleObject(Vec3.of(1, 2, -1), Vec3.of(1, 1, 3), Vec3.of(3, -1, -1), new Diffuse(new SingleColorTexture(Vec3.of(0.1f, 0.1f, 0.8f)))));

        scene.getSceneObjects().add(new Sphere(Vec3.of(1, 0, -1), 0.5f, new Glossy(new SingleColorTexture(Vec3.of(0.8f, 0.6f, 0.2f)), .2f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), 0.5f, new Glass(1.5f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), -0.45f, new Glass(1.5f)));
    }
}
