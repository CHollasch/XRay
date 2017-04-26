package me.hollasch.xray.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.hollasch.xray.light.Light;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.render.multithreaded.MultithreadedRenderer;
import me.hollasch.xray.scene.Scene;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Connor Hollasch
 * @since April 12, 10:02 AM
 */
public abstract class Renderer {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter
    protected final Scene scene;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean rendering;

    @Getter
    protected Vec3[][] renderData;

    @Getter
    private final Set<Listener> progressListeners;

    // Ray management

    @Getter
    protected double tMin;
    @Getter
    protected double tMax;
    @Getter
    protected int maxDepth;
    @Getter
    protected double blurFactor;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public Renderer(final Scene scene) {
        this.scene = scene;
        this.progressListeners = new HashSet<>();
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public void registerProgressListener(MultithreadedRenderer.Listener onProgress) {
        this.progressListeners.add(onProgress);
    }

    public Vec3 getColorAt(double x, double y) {
        Vec3 totalColor;

        double u = (x + (Math.random() * this.blurFactor)) / this.getScene().getScreenWidth();
        double v = (y + (Math.random() * this.blurFactor)) / this.getScene().getScreenHeight();

        Ray ray = this.getScene().getCameraObject().projectRay(u, v);
        totalColor = this.getColorAt(ray, 0);

        // Gamma correction.
        totalColor = Vec3.of(
                Math.sqrt(totalColor.getX()),
                Math.sqrt(totalColor.getY()),
                Math.sqrt(totalColor.getZ())
        );

        return totalColor;
    }

    public Vec3 getColorAt(Ray ray, int depth) {
        RayCollision interaction = findObjectCollision(ray);

        if (interaction != null) {
            if (interaction.getMaterial() == null) {
                return Vec3.of(0, 0, 0);
            }

            Material material = interaction.getMaterial();
            SurfaceInteraction surfaceInteraction = material.scatter(ray, interaction);

            if (depth < this.maxDepth && surfaceInteraction != null) {
                Vec3 fromLights = Vec3.of(0, 0, 0);

                for (Light light : this.scene.getSceneLights()) {
                    Vec3 lightContribution = light.getLightContribution(this, interaction, ray);
                    fromLights = fromLights.add(lightContribution);
                }

                if (surfaceInteraction.isEmissive()) {
                    return surfaceInteraction.getLightContribution();
                }

                return fromLights.add(surfaceInteraction.getLightContribution().multiply(getColorAt(surfaceInteraction.getScattered(), depth + 1)));
            } else {
                return new Vec3();
            }
        } else {
            return this.scene.getBackground().getRGBAt(ray.getDirection());
        }
    }

    public RayCollision findObjectCollision(Ray ray) {
        if (ray == null) {
            return null;
        }

        RayCollision currentRecord = null;
        double closestCollision = this.tMax;

        for (WorldObject object : this.scene.getSceneObjects()) {
            // Do bounding box check first for speed
            if (object.getBoundingBox().doesIntersect(ray)) {
                RayCollision possibleRecord = object.rayIntersect(ray, tMin, closestCollision);

                if (possibleRecord != null) {
                    closestCollision = possibleRecord.getTValue();
                    currentRecord = possibleRecord;
                }
            }
        }

        return currentRecord;
    }

    public BufferedImage writeToImage() {
        if (isRendering()) {
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
    // PROGRESS LISTENER
    //==============================================================================================

    public interface Listener {

        void onPixelFinish(int x, int y, Vec3 color);

        void onRenderFinish(Vec3[][] finalImage);
    }

    public void setPixelAtInstant(int x, int y, Vec3 color) {
        if (x < 0 || x >= this.renderData.length || y < 0 || y >= this.scene.getScreenHeight()) {
            return;
        }

        // Adjust y before updating.
        y = this.scene.getScreenHeight() - 1 - y;
        int finalY = y;

        // For speed purposes, we will assume there is no error during render writes as each pixel is rendered
        // individually on multiple threads, so there is very little chance we modify the same value in this
        // array. No point synchronizing then.
        this.renderData[x][y] = color;
        getProgressListeners().forEach(c -> c.onPixelFinish(x, finalY, color));
    }

    public Vec3 getPixelAtInstant(int x, int y) {
        y = this.scene.getScreenHeight() - 1 - y;
        return this.renderData[x][y];
    }

    //==============================================================================================
    // ABSTRACT METHODS
    //==============================================================================================

    public abstract void render();
}
