package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.scene.Scene;

import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:20 PM
 */
public class RenderEngine implements Runnable {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter private final Scene scene;
    @Getter private final Properties renderProperties;

    @Getter private Vec3[][] renderData;
    @Getter private boolean isRendering = false;

    @Getter private long renderDuration;

    @Getter private long samplesNeeded;
    @Getter private long samplesLeft;

    //===============================================
    // Properties
    //===============================================

    // Sampling
    private int samples;
    private float blurFactor;

    // Ray management
    private float tMin;
    private float tMax;
    private int maxDepth;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public RenderEngine(final Scene scene) {
        this(scene, new Properties());
    }

    public RenderEngine(final Scene scene, final Properties renderProperties) {
        this.scene = scene;
        this.renderProperties = renderProperties;

        this.samples = renderProperties.containsKey("samples") ? (Integer) renderProperties.get("samples") : 32;
        this.blurFactor = renderProperties.containsKey("blur_factor") ? (Float) renderProperties.get("blur_factor") : 1.0f;
        this.tMin = renderProperties.containsKey("t_min") ? (Float) renderProperties.get("t_min") : 0.01f;
        this.tMax = renderProperties.containsKey("t_max") ? (Float) renderProperties.get("t_max") : Float.MAX_VALUE;
        this.maxDepth = renderProperties.contains("max_depth") ? (Integer) renderProperties.get("max_depth") : 50;
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public void run() {
        if (this.scene.getCameraObject() == null) {
            throw new RuntimeException("Camera cannot be null for render to start!");
        }

        isRendering = true;

        // We can handle the scene size as our width height combo.
        int width = scene.getScreenWidth();
        int height = scene.getScreenHeight();

        this.renderData = new Vec3[width][height];
        long renderStart = System.currentTimeMillis();

        this.samplesNeeded = height * width * this.samples;
        this.samplesLeft = this.samplesNeeded;

        for (int py = height - 1; py >= 0; --py) {
            for (int px = 0; px < width; ++px) {
                for (int sample = 1; sample <= Math.max(1, this.samples); ++sample) {
                    float u = (float) (px + (Math.random() * this.blurFactor)) / this.scene.getScreenWidth();
                    float v = (float) (py + (Math.random() * this.blurFactor)) / this.scene.getScreenHeight();

                    if (this.samples <= 0) {
                        u = (float) px / this.scene.getScreenWidth();
                        v = (float) py / this.scene.getScreenHeight();
                    }

                    Ray ray = this.scene.getCameraObject().createRay(u, v);
                    Vec3 color = getColorAt(ray, 0);

                    Vec3 storedColor = this.renderData[px][height - 1 - py];
                    if (storedColor == null) {
                        this.renderData[px][height - 1 - py] = color;
                    } else {
                        this.renderData[px][height - 1 - py] = storedColor.add(color);
                    }

                    // Benchmark render times for possible realtime updates.
                    this.renderDuration = System.currentTimeMillis() - renderStart;
                }

                this.samplesLeft -= this.samples;
                Vec3 color = this.renderData[px][height - 1 - py].divideScalar(Math.max(1, this.samples));
                color = Vec3.of((float) Math.sqrt(color.getX()), (float) Math.sqrt(color.getY()), (float) Math.sqrt(color.getZ()));

                this.renderData[px][height - 1 - py] = color;

                float percent = (this.samplesLeft / (float) this.samplesNeeded) * 100f;

                if (percent % 1 == 0) {
                    System.out.println(percent + "% (" + (this.samplesNeeded - this.samplesLeft) + " / " + this.samplesNeeded + ")");
                }
            }
        }

        // Benchmark render time for final pixel.
        this.renderDuration = System.currentTimeMillis() - renderStart;
        this.isRendering = false;
    }

    public Vec3 getColorAt(Ray ray, int depth) {
        RayCollision interaction = findCollision(ray);

        if (interaction != null) {
            if (interaction.getMaterial() == null) {
                return Vec3.of(0, 0, 0);
            }

            Material material = interaction.getMaterial();
            SurfaceInteraction surfaceInteraction = material.scatter(ray, interaction);

            if (depth < this.maxDepth && surfaceInteraction != null) {
                return surfaceInteraction.getAttenuation().multiply(getColorAt(surfaceInteraction.getScattered(), depth + 1));
            } else {
                return Vec3.of(0, 0, 0);
            }
        } else {
            Vec3 unitVec = ray.getDirection().normalize();
            float t = (float) (0.5 * (unitVec.getY() + 1.0));

            return Vec3.of(1.0f, 1.0f, 1.0f).multiplyScalar(1f - t).add(Vec3.of(0.5f, 0.7f, 1.0f).multiplyScalar(t));
        }
    }

    public RayCollision findCollision(Ray ray) {
        RayCollision currentRecord = null;
        float closestCollision = this.tMax;

        for (WorldObject object : this.scene.getSceneObjects()) {
            RayCollision possibleRecord = object.rayIntersect(ray, tMin, closestCollision);

            if (possibleRecord != null) {
                closestCollision = possibleRecord.getTValue();
                currentRecord = possibleRecord;
            }
        }

        return currentRecord;
    }

    public BufferedImage writeToImage() {
        if (this.isRendering) {
            throw new RuntimeException("Cannot write render result to image during render.");
        }

        // Make sure we accept alpha channels for transparent surfaces.
        BufferedImage renderResult = new BufferedImage(
                scene.getScreenWidth(),
                scene.getScreenHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // Dump pixel colors to image.
        for (int x = 0; x < this.renderData.length; ++x) {
            for (int y = this.renderData[x].length - 1; y >= 0; --y) {
                renderResult.setRGB(x, y, this.renderData[x][y].toRGB());
            }
        }

        return renderResult;
    }

    //==============================================================================================
    // STATIC ACCESS
    //==============================================================================================

    public static final BufferedImage renderToImage(Scene scene) {
        return RenderEngine.renderToImage(scene, new Properties());
    }

    public static final BufferedImage renderToImage(Scene scene, Properties renderProperties) {
        RenderEngine render = new RenderEngine(scene, renderProperties);
        render.run();
        return render.writeToImage();
    }
}
