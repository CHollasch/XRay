package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.math.Vec3;

/**
 * @author Connor Hollasch
 * @since Feb 24, 6:42 PM
 */
public class TileTracer implements Runnable {

    @Getter private int xi, yi;
    @Getter private int width, height;

    @Getter private final MultithreadedRenderer renderer;

    @Getter private boolean rendering = false;

    public TileTracer(final MultithreadedRenderer renderer, final int xi, final int yi, final int width, final int height) {
        this.renderer = renderer;

        this.xi = xi;
        this.yi = yi;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        this.rendering = true;

        for (int sample = 1; sample <= Math.max(1, this.renderer.samples); ++sample) {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    Vec3 current = this.renderer.getPixelAtInstant(xi + x, yi + y);
                    current = current == null ? Vec3.of(0f, 0f, 0f) : current;
                    this.renderer.setPixelAtInstant(xi + x, yi + y, sample, current.add(this.renderer.getColorAt(x + xi, y + yi)));
                }
            }
        }

        this.renderer.markTileCompletion(this);
        this.rendering = false;
    }
}
