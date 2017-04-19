package me.hollasch.xray.render.multithreaded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Connor on 4/19/17.
 */
public enum TileDirection {

    BOTTOM_TO_TOP {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                           final int width,
                                                           final int height,
                                                           final int tileSizeX,
                                                           final int tileSizeY) {
            List<TileTracer> tracers = new ArrayList<>();

            for (int ty = 0; ty < height; ty += tileSizeY) {
                for (int tx = 0; tx < width; tx += tileSizeX) {
                    int xMin = Math.min(tileSizeX, width - tx);
                    int yMin = Math.min(tileSizeY, height - ty);

                    tracers.add(new TileTracer(renderer, tx, ty, xMin, yMin));
                }
            }

            return tracers;
        }
    },
    TOP_TO_BOTTOM {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                 final int width,
                                                 final int height,
                                                 final int tileSizeX,
                                                 final int tileSizeY) {
            List<TileTracer> tracers = new ArrayList<>();

            for (int ty = height; ty >= 0; ty -= tileSizeY) {
                for (int tx = 0; tx < width; tx += tileSizeX) {
                    int xMin = Math.min(tileSizeX, width - tx);
                    int yMin = Math.min(tileSizeY, height - ty);

                    tracers.add(new TileTracer(renderer, tx, ty, xMin, yMin));
                }
            }

            return tracers;
        }
    },
    LEFT_TO_RIGHT {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                           final int width,
                                                           final int height,
                                                           final int tileSizeX,
                                                           final int tileSizeY) {
            List<TileTracer> tracers = new ArrayList<>();

            for (int tx = 0; tx < width; tx += tileSizeX) {
                for (int ty = 0; ty < height; ty += tileSizeY) {
                    int xMin = Math.min(tileSizeX, width - tx);
                    int yMin = Math.min(tileSizeY, height - ty);

                    tracers.add(new TileTracer(renderer, tx, ty, xMin, yMin));
                }
            }

            return tracers;
        }
    },
    RIGHT_TO_LEFT {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                           final int width,
                                                           final int height,
                                                           final int tileSizeX,
                                                           final int tileSizeY) {
            List<TileTracer> tracers = new ArrayList<>();

            for (int tx = width; tx >= 0; tx -= tileSizeX) {
                for (int ty = 0; ty < height; ty += tileSizeY) {
                    int xMin = Math.min(tileSizeX, width - tx);
                    int yMin = Math.min(tileSizeY, height - ty);

                    tracers.add(new TileTracer(renderer, tx, ty, xMin, yMin));
                }
            }

            return tracers;
        }
    },
    RANDOM {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                           final int width,
                                                           final int height,
                                                           final int tileSizeX,
                                                           final int tileSizeY) {

            List<TileTracer> tracers = TOP_TO_BOTTOM.createTileTracers(renderer, width, height, tileSizeX, tileSizeY);
            Collections.shuffle(tracers);
            return tracers;
        }
    },
    CENTER {
        protected List<TileTracer> createTileTracers(final MultithreadedRenderer renderer,
                                                           final int width,
                                                           final int height,
                                                           final int tileSizeX,
                                                           final int tileSizeY) {
            List<TileTracer> tracers = new ArrayList<>();

            int sx = width / 2;
            int sy = height / 2;

            int direction = 1;

            for (int ty = 0; ty < height; ty += tileSizeY) {
                for (int tx = 0; tx < width; tx += tileSizeX) {
                    int xMin = Math.min(tileSizeX, width - tx);
                    int yMin = Math.min(tileSizeY, height - ty);

                    tracers.add(new TileTracer(renderer, tx, ty, xMin, yMin));
                }
            }

            return tracers;
        }
    };

    protected abstract List<TileTracer> createTileTracers(
            final MultithreadedRenderer renderer,
            final int width,
            final int heigth,
            final int tileSizeX,
            final int tileSizeY);
}
