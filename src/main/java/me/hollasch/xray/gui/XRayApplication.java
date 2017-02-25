package me.hollasch.xray.gui;

import me.hollasch.xray.material.Diffuse;
import me.hollasch.xray.material.Glass;
import me.hollasch.xray.material.Glossy;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.Sphere;
import me.hollasch.xray.render.RenderProperties;
import me.hollasch.xray.render.Renderer;
import me.hollasch.xray.render.TileTracer;
import me.hollasch.xray.scene.Scene;
import me.hollasch.xray.scene.camera.PerspectiveCamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:26 PM
 */
public class XRayApplication {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 640;

    private static Scene scene;

    private static Renderer renderer;
    private static Vec3[][] pixelData;

    private static JPanel renderPanel = new JPanel() {
        {
            setSize(WIDTH, HEIGHT - 20);
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
                        g.setColor(new Color(color.getX(), color.getY(), color.getZ()));
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
                renderer = new Renderer(scene, RenderProperties.SAMPLE_COUNT.get(100));
                renderer.registerProgressListener(new Renderer.Listener() {
                    @Override
                    public void onPixelFinish(int x, int y, Vec3 color) {
                        pixelData[x][y] = color;
                        renderPanel.repaint();
                    }

                    public void onTileFinish(TileTracer tracer) {}
                    public void onRenderFinish(Vec3[][] finalImage) {}
                });
                renderer.render();
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(renderPanel, BorderLayout.CENTER);
        frame.add(render, BorderLayout.PAGE_END);

        frame.setVisible(true);

        scene = new Scene(WIDTH, HEIGHT);

        Vec3 origin = Vec3.of(3, 3, 2);
        Vec3 lookAt = Vec3.of(0, 0, -1);
        float distanceToFocus = origin.subtract(lookAt).length();

        scene.setCameraObject(new PerspectiveCamera(origin, lookAt, Vec3.of(0, 1, 0), 80, (float) WIDTH / HEIGHT));
        //scene.setCameraObject(new OrthographicCamera(origin, lookAt, Vec3.of(0, 1, 0), WIDTH, HEIGHT));

        scene.getSceneObjects().add(new Sphere(Vec3.of(0, 0, -1), .6f, new Diffuse(new Vec3(0.8f, 0.3f, 0.3f))));
        scene.getSceneObjects().add(new Sphere(Vec3.of(0, -1000f, -1), 999.5f, new Diffuse(new Vec3(0.2f, 0.5f, 0.3f))));

        for (int i = 0; i < 20; ++i) {
            scene.getSceneObjects().add(new Sphere(Vec3.of(
                    (float) (Math.random() * 20 - 10f),
                    0f,
                    (float) (Math.random() * 20 - 10f)
            ), (float) (Math.random() * .25 + .25f), new Diffuse(new Vec3(
                    (float) Math.random(),
                    (float) Math.random(),
                    (float) Math.random()
            ))));
        }

        scene.getSceneObjects().add(new Sphere(Vec3.of(1, 0, -1), 0.5f, new Glossy(Vec3.of(0.8f, 0.6f, 0.2f), .034f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), 0.5f, new Glass(1.5f)));
        scene.getSceneObjects().add(new Sphere(Vec3.of(-1, 0, -1), -0.45f, new Glass(1.5f)));
    }
}
