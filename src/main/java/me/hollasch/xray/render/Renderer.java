package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.material.Material;
import me.hollasch.xray.material.SurfaceInteraction;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.scene.Scene;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Connor Hollasch
 * @since Feb 23, 6:20 PM
 */
public class Renderer implements Runnable {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter
    private final Scene scene;

    @Getter
    private Vec3[][] renderData;

    @Getter
    private boolean isRendering = false;

    @Getter
    private long renderDuration;

    @Getter
    private long samplesNeeded;
    @Getter
    private long samplesLeft;

    @Getter
    private float percentageComplete;

    @Getter
    private final Set<TileTracer> tracersLeft;

    @Getter
    private final Set<Listener> progressListeners;

    //===============================================
    // Properties
    //===============================================

    // Sampling
    protected int samples;
    protected float blurFactor;

    // Ray management
    protected float tMin;
    protected float tMax;
    protected int maxDepth;

    //===============================================
    // Multi-threading
    //===============================================

    protected int threadCount;
    private ExecutorService threadPool;

    protected int tileSizeX;
    protected int tileSizeY;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public Renderer(final Scene scene) {
        this(scene, new RenderProperties.Value<?>[0]);
    }

    public Renderer(final Scene scene, final RenderProperties.Value<?>... renderProps) {
        this.scene = scene;

        // Build properties map.
        Map<RenderProperties, RenderProperties.Value<?>> propertyMap = RenderProperties.buildPropertiesMap();
        for (RenderProperties.Value<?> renderProperty : renderProps) {
            propertyMap.put(renderProperty.getContainerProperty(), renderProperty);
        }

        this.threadCount = (Integer) propertyMap.get(RenderProperties.THREAD_COUNT).get();
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        if (this.threadCount <= 0 || threadCount > availableProcessors) {
            this.threadCount = availableProcessors;
        }

        this.threadPool = Executors.newFixedThreadPool(this.threadCount);
        this.tileSizeX = Math.min((Integer) propertyMap.get(RenderProperties.TILE_SIZE_X).get(), this.scene.getScreenWidth());
        this.tileSizeY = Math.min((Integer) propertyMap.get(RenderProperties.TILE_SIZE_Y).get(), this.scene.getScreenHeight());

        this.tracersLeft = new HashSet<>();
        this.progressListeners = new HashSet<>();

        this.samples = (Integer) propertyMap.get(RenderProperties.SAMPLE_COUNT).get();
        this.blurFactor = (Float) propertyMap.get(RenderProperties.BLUR_FACTOR).get();
        this.tMin = (Float) propertyMap.get(RenderProperties.T_MIN).get();
        this.tMax = (Float) propertyMap.get(RenderProperties.T_MAX).get();
        this.maxDepth = (Integer) propertyMap.get(RenderProperties.MAX_DEPTH).get();
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    // Naming conventions.
    public void render() {
        run();
    }

    public void run() {
        if (this.scene.getCameraObject() == null) {
            throw new RuntimeException("PerspectiveCamera cannot be null for render to start!");
        }

        isRendering = true;

        // We can handle the scene size as our width height combo.
        int width = scene.getScreenWidth();
        int height = scene.getScreenHeight();

        this.renderData = new Vec3[width][height];
        long renderStart = System.currentTimeMillis();

        this.samplesNeeded = height * width * this.samples;
        this.samplesLeft = this.samplesNeeded;

        registerProgressListener(new Listener() {
            @Override
            public void onPixelFinish(int x, int y, Vec3 color) {

            }

            @Override
            public void onTileFinish(TileTracer tracer) {
            }

            @Override
            public void onRenderFinish(Vec3[][] finalImage) {
                // Benchmark render time for final pixel.
                Renderer.this.renderData = finalImage;
                Renderer.this.renderDuration = System.currentTimeMillis() - renderStart;
                Renderer.this.isRendering = false;
            }
        });

        for (int ty = height - 1; ty >= 0; ty -= this.tileSizeY) {
            for (int tx = 0; tx < width; tx += this.tileSizeX) {
                TileTracer t = new TileTracer(this, tx, ty, Math.min(this.tileSizeX, width - tx), Math.min(this.tileSizeY, height - ty));
                this.threadPool.submit(t);

                synchronized (this.tracersLeft) {
                    this.tracersLeft.add(t);
                }
            }
        }
    }

    public void cancel() {
        this.threadPool.shutdownNow();
    }

    public Vec3 getColorAt(int x, int y) {
        Vec3 totalColor = Vec3.of(0f, 0f, 0f);

        for (int sample = 1; sample <= Math.max(1, this.samples); ++sample) {
            float u = (float) (x + (Math.random() * this.blurFactor)) / this.getScene().getScreenWidth();
            float v = (float) (y + (Math.random() * this.blurFactor)) / this.getScene().getScreenHeight();

            if (this.samples <= 0) {
                u = (float) (x) / this.getScene().getScreenWidth();
                v = (float) (y) / this.getScene().getScreenHeight();
            }

            Ray ray = this.getScene().getCameraObject().projectRay(u, v);
            totalColor = totalColor.add(this.getColorAt(ray, 0));
        }

        totalColor = totalColor.divideScalar(Math.max(1, this.samples));

        // Gamma correction.
        totalColor = Vec3.of(
                (float) Math.sqrt(totalColor.getX()),
                (float) Math.sqrt(totalColor.getY()),
                (float) Math.sqrt(totalColor.getZ())
        );

        return totalColor;
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
        return Renderer.renderToImage(scene, new RenderProperties.Value[0]);
    }

    public static final BufferedImage renderToImage(Scene scene, RenderProperties.Value<?>... renderProperties) {
        Renderer render = new Renderer(scene, renderProperties);
        render.run();
        return render.writeToImage();
    }

    //==============================================================================================
    // PROGRESS WATCHER
    //==============================================================================================

    public interface Listener {

        void onPixelFinish(int x, int y, Vec3 color);

        void onTileFinish(TileTracer tracer);

        void onRenderFinish(Vec3[][] finalImage);
    }

    public void registerProgressListener(Listener onProgress) {
        this.progressListeners.add(onProgress);
    }

    public void setPixelAtInstant(int x, int y, Vec3 color) {
        // Adjust y before updating.
        y = this.scene.getScreenHeight() - 1 - y;
        int finalY = y;

        // For speed purposes, we will assume there is no error during render writes as each pixel is rendered
        // individually on multiple threads, so there is very little chance we modify the same value in this
        // array. No point synchronizing then.
        renderData[x][y] = color;
        this.progressListeners.forEach(c -> c.onPixelFinish(x, finalY, color));
    }

    public void markTileCompletion(TileTracer tracer) {
        synchronized (this.tracersLeft) {
            this.tracersLeft.remove(tracer);

            if (this.tracersLeft.size() == 0) {
                // Render complete
                this.progressListeners.forEach(c -> c.onRenderFinish(renderData));
            } else {
                this.progressListeners.forEach(c -> c.onTileFinish(tracer));
            }
        }
    }
}
