package me.hollasch.xray.render;

import lombok.Getter;

/**
 * @author Connor Hollasch
 * @since Feb 24, 6:42 PM
 */
public class TileTracer implements Runnable {

    @Getter private int xi, yi;
    @Getter private int width, height;

    @Getter private final Renderer renderer;

    private float rnd1, rnd2, rnd3;
    {
        rnd1 = (float) Math.random();
        rnd2 = (float) Math.random();
        rnd3 = (float) Math.random();
    }

    @Getter private boolean rendering = false;

    public TileTracer(final Renderer renderer, final int xi, final int yi, final int width, final int height) {
        this.renderer = renderer;

        this.xi = xi;
        this.yi = yi;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        this.rendering = true;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                this.renderer.setPixelAtInstant(xi + x, yi + y, this.renderer.getColorAt(x + xi, y + yi));
            }
        }

        this.renderer.markTileCompletion(this);
        this.rendering = false;
    }
}
