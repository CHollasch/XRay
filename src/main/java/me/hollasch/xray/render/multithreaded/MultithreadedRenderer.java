package me.hollasch.xray.render.multithreaded;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.render.RenderProperties;
import me.hollasch.xray.render.Renderer;
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
public class MultithreadedRenderer extends Renderer {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

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

    //===============================================
    // Properties
    //===============================================

    // Sampling
    protected int samples;

    //===============================================
    // Multi-threading
    //===============================================

    protected int threadCount;
    private ExecutorService threadPool;

    protected int tileSizeX;
    protected int tileSizeY;

    protected TileDirection tileDirection;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public MultithreadedRenderer(final Scene scene, final RenderProperties.Value<?>... renderProps) {
        super(scene);

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
        this.tileDirection = (TileDirection) propertyMap.get(RenderProperties.TILE_DIRECTION).get();

        this.tracersLeft = new HashSet<>();

        this.samples = (Integer) propertyMap.get(RenderProperties.SAMPLE_COUNT).get();

        // Initialize renderer properties.

        this.blurFactor = (Float) propertyMap.get(RenderProperties.BLUR_FACTOR).get();
        this.tMin = (Float) propertyMap.get(RenderProperties.T_MIN).get();
        this.tMax = (Float) propertyMap.get(RenderProperties.T_MAX).get();
        this.maxDepth = (Integer) propertyMap.get(RenderProperties.MAX_DEPTH).get();
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public void render() {
        if (this.scene.getCameraObject() == null) {
            throw new RuntimeException("Camera cannot be null for render to start!");
        }

        setRendering(true);

        int width = scene.getScreenWidth();
        int height = scene.getScreenHeight();
        this.renderData = new Vec3[width][height];

        this.samplesNeeded = height * width * this.samples;
        this.samplesLeft = this.samplesNeeded;

        synchronized (this.tracersLeft) {
            this.tileDirection.createTileTracers(this, width, height, this.tileSizeX, this.tileSizeY)
                    .forEach(t -> {
                this.threadPool.submit(t);
                this.tracersLeft.add(t);
            });
        }
    }

    public void cancel() {
        this.threadPool.shutdownNow();
    }

    //==============================================================================================
    // STATIC ACCESS
    //==============================================================================================

    public static final BufferedImage renderToImage(Scene scene) {
        return MultithreadedRenderer.renderToImage(scene, new RenderProperties.Value[0]);
    }

    public static final BufferedImage renderToImage(Scene scene, RenderProperties.Value<?>... renderProperties) {
        MultithreadedRenderer render = new MultithreadedRenderer(scene, renderProperties);
        render.render();
        return render.writeToImage();
    }

    //==============================================================================================
    // PROGRESS WATCHER
    //==============================================================================================

    protected void setPixelAtInstant(int x, int y, int passes, Vec3 color) {
        // Adjust y before updating.
        y = this.scene.getScreenHeight() - 1 - y;
        int finalY = y;

        // Compute render statistics.
        --this.samplesLeft;
        this.percentageComplete = (int) ((1f - ((float) this.samplesLeft / this.samplesNeeded)) * 100f);

        // For speed purposes, we will assume there is no error during render writes as each pixel is rendered
        // individually on multiple threads, so there is very little chance we modify the same value in this
        // array. No point synchronizing then.
        this.renderData[x][y] = color;
        getProgressListeners().forEach(c -> c.onPixelFinish(x, finalY, color.divideScalar(passes)));
    }

    protected void markTileCompletion(TileTracer tracer) {
        synchronized (this.tracersLeft) {
            this.tracersLeft.remove(tracer);

            if (this.tracersLeft.size() == 0) {
                // Render complete
                setRendering(false);
                this.threadPool.shutdownNow();

                getProgressListeners().forEach(c -> c.onRenderFinish(renderData));
            }
        }
    }
}