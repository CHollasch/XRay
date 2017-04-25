package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.scene.Scene;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Connor Hollasch
 * @since April 12, 2:06 PM
 */
public class PIRRenderer extends Renderer {

    //==============================================================================================
    // INSTANCE VARIABLES
    //==============================================================================================

    @Getter
    private final int startLevel;

    @Getter
    private final int samples;

    //==============================================================================================
    // CONSTRUCTORS
    //==============================================================================================

    public PIRRenderer(final Scene scene, final RenderProperties.Value<?>... renderProps) {
        super(scene);

        // Build properties map.
        Map<RenderProperties, RenderProperties.Value<?>> propertyMap = RenderProperties.buildPropertiesMap();
        for (RenderProperties.Value<?> renderProperty : renderProps) {
            propertyMap.put(renderProperty.getContainerProperty(), renderProperty);
        }

        this.startLevel = (Integer) propertyMap.get(RenderProperties.PIR_START_DEPTH).get();
        this.samples = (Integer) propertyMap.get(RenderProperties.SAMPLE_COUNT).get();

        // Initialize renderer properties.

        this.blurFactor = (Double) propertyMap.get(RenderProperties.BLUR_FACTOR).get();
        this.tMin = (Double) propertyMap.get(RenderProperties.T_MIN).get();
        this.tMax = (Double) propertyMap.get(RenderProperties.T_MAX).get();
        this.maxDepth = (Integer) propertyMap.get(RenderProperties.MAX_DEPTH).get();
    }

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================

    public void render() {
        ExecutorService s = Executors.newSingleThreadExecutor();
        s.submit(() -> {
            try {
                setRendering(true);

                int width = scene.getScreenWidth();
                int height = scene.getScreenHeight();
                PIRRenderer.this.renderData = new Vec3[width][height];

                int samples;
                double size, size2;
                int iX, iY;

                size = 1 << startLevel;

                for (iY = 0; iY < height; iY += size) {
                    for (iX = 0; iX < width; iX += size) {
                        setRectangle(iX, iY, (int) size, (int) size, getColorAt(iX, iY));
                    }
                }

                samples = 0;
                size2 = size / 2;

                while (size > 1 || samples < this.samples) {
                    if (size2 < 1) {
                        samples = Math.min(this.samples, (int) (1 / size2));
                    }

                    for (iY = 0; iY < height; iY += size) {
                        for (iX = 0; iX < width; iX += size) {
                            if (samples > 0) {
                                setPixelAtInstant(iX, iY, getColorWithSamples(iX, iY, samples));
                            } else {
                                setRectangle(
                                        iX,
                                        (int) (iY + size2),
                                        (int) size2, (int) size2,
                                        getColorAt(iX, iY + size2)
                                );

                                setRectangle(
                                        (int) (iX + size2),
                                        (int) (iY + size2),
                                        (int) size2, (int) size2,
                                        getColorAt(iX + size2, iY + size2)
                                );

                                setRectangle(
                                        (int) (iX + size2),
                                        iY,
                                        (int) size2, (int) size2,
                                        getColorAt(iX + size2, iY)
                                );
                            }
                        }
                    }

                /* The new region edge length is half the old edge length. */

                    size = Math.max(1, size2);
                    size2 = size2 / 2;
                }

                setRendering(false);
                getProgressListeners().forEach(l -> l.onRenderFinish(this.getRenderData()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //==============================================================================================
    // PRIVATE HELPER METHODS
    //==============================================================================================

    private Vec3 getColorWithSamples(double x, double y, int samples) {
        Vec3 color = new Vec3();

        for (int i = 0; i < samples; ++i) {
            color = color.add(getColorAt(x, y).divideScalar(samples));
        }

        return color;
    }

    private void setRectangle(int x, int y, int sx, int sy, Vec3 color) {
        for (int xOff = 0; xOff < sx; ++xOff) {
            for (int yOff = 0; yOff < sy; ++yOff) {
                int pX = x + xOff;
                int pY = y + yOff;

                setPixelAtInstant(pX, pY, color);
            }
        }
    }
}
