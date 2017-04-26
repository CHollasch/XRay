package me.hollasch.xray.render;

import lombok.Getter;
import me.hollasch.xray.render.engine.multithreaded.TileDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since Feb 24, 6:59 PM
 */
public enum Integrator {

    THREAD_COUNT("Amount of threads for multi-core processing", Runtime.getRuntime().availableProcessors()),
    TILE_SIZE_X("Size of each render tile in the x direction", 32),
    TILE_SIZE_Y("Size of each render tile in the y direction", 32),

    TILE_DIRECTION("Which direction to sample tiles in", TileDirection.CENTER),
    RAY_SAMPLER("Method used when sampling individual pixels", RaySampler.RANDOM),

    SAMPLE_COUNT("Amount of samples per pixel", 32),
    BLUR_FACTOR("Spread of the samples across multiple pixels", 1.0),

    T_MIN("Minimum t-value for ray cutoff", 0.001),
    T_MAX("Maximum t-value for ray cutoff", Double.MAX_VALUE),

    MAX_DEPTH("Maximum amount of bounces per ray", 12),
    PIR_START_DEPTH("Starting size of PIR chunks", 8);

    @Getter private String description;
    @Getter private Object defaultValue;

    Integrator(String description, Object defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public <T> Value<T> get(T value) {
        return new Value<T>(this, value, (T) this.defaultValue);
    }

    public class Value<T> {
        @Getter private Integrator containerProperty;

        @Getter private T assignedValue;
        @Getter private T defaultValue;

        private Value(Integrator containerProperty, T value, T defaultValue) {
            this.containerProperty = containerProperty;
            this.assignedValue = value;
            this.defaultValue = defaultValue;
        }

        public T get() {
            return (assignedValue == null ? defaultValue : assignedValue);
        }
    }

    public static Map<Integrator, Value<?>> buildPropertiesMap() {
        Map<Integrator, Value<?>> renderProperties = new HashMap<Integrator, Value<?>>();

        for (Integrator property : values()) {
            renderProperties.put(property, property.get(null));
        }

        return renderProperties;
    }
}
