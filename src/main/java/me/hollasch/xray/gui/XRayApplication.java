package me.hollasch.xray.gui;

import me.hollasch.xray.math.Vec2;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.RaySampler;
import me.hollasch.xray.render.engine.multithreaded.MultithreadedRenderer;
import me.hollasch.xray.render.Integrator;
import me.hollasch.xray.render.engine.Renderer;
import me.hollasch.xray.render.engine.multithreaded.TileDirection;
import me.hollasch.xray.scene.Scene;

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

        public void paint(Graphics g) {
            if (pixelData == null) {
                return;
            }

            for (int i = 0; i < pixelData.length; ++i) {
                for (int j = 0; j < pixelData[i].length; ++j) {
                    Vec3 color = pixelData[i][j];

                    if (color == null) {
                        g.setColor(new Color(doChecker(Vec2.of(i, j)).toRGB()));
                    } else {
                        int rgb = color.toRGB();
                        g.setColor(new Color(rgb));
                    }

                    g.drawLine(i, j, i, j);
                }
            }
        }

        private Vec3 doChecker(Vec2 c) {
            c = c.divideScalar(10);

            if ((Math.floor(c.getX()) + Math.floor(c.getY())) % 2 == 0) {
                return Vec3.of(.275);
            }

            return Vec3.of(.2);
        }
    };

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("XRay - Connor Hollasch");
        frame.setSize(WIDTH + 25, HEIGHT + 75);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JButton render = new JButton("Render");
        render.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (renderer != null && renderer.isRendering()) {
                    return;
                }

                pixelData = new Vec3[WIDTH][HEIGHT];

                renderer = new MultithreadedRenderer(
                        scene,
                        Integrator.SAMPLE_COUNT.get(8192),
                        Integrator.RAY_SAMPLER.get(RaySampler.RANDOM),
                        Integrator.TILE_SIZE_X.get(50),
                        Integrator.TILE_SIZE_Y.get(50),
                        Integrator.T_MIN.get(.01),
                        Integrator.MAX_DEPTH.get(24),
                        Integrator.TILE_DIRECTION.get(TileDirection.BOTTOM_TO_TOP)
                );

                renderer.registerProgressListener(new MultithreadedRenderer.Listener() {
                    @Override
                    public void onPixelFinish(int x, int y, Vec3 color) {
                        pixelData[x][y] = color;
                        renderPanel.repaint();
                    }

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
        scene = new DemoScene(WIDTH, HEIGHT);
    }
}