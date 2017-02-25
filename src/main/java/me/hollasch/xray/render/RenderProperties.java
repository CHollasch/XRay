package me.hollasch.xray.render;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since Feb 24, 6:59 PM
 */
public enum RenderProperties {

    THREAD_COUNT("Amount of threads for multi-core processing", Runtime.getRuntime().availableProcessors()),
    TILE_SIZE_X("Size of each render tile in the x direction", 32),
    TILE_SIZE_Y("Size of each render tile in the y direction", 32),

    SAMPLE_COUNT("Amount of samples per pixel", 32),
    BLUR_FACTOR("Spread of the samples across multiple pixels", 1.0f),

    T_MIN("Minimum t-value for ray cutoff", 0.01f),
    T_MAX("Maximum t-value for ray cutoff", Float.MAX_VALUE),

    MAX_DEPTH("Maximum amount of bounces per ray", 12);

    @Getter private String description;
    @Getter private Object defaultValue;

    RenderProperties(String description, Object defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public <T> Value<T> get(T value) {
        return new Value<T>(this, value, (T) this.defaultValue);
    }

    public class Value<T> {
        @Getter private RenderProperties containerProperty;

        @Getter private T assignedValue;
        @Getter private T defaultValue;

        private Value(RenderProperties containerProperty, T value, T defaultValue) {
            this.containerProperty = containerProperty;
            this.assignedValue = value;
            this.defaultValue = defaultValue;
        }

        public T get() {
            return (assignedValue == null ? defaultValue : assignedValue);
        }
    }

    public static Map<RenderProperties, Value<?>> buildPropertiesMap() {
        Map<RenderProperties, Value<?>> renderProperties = new HashMap<RenderProperties, Value<?>>();

        for (RenderProperties property : values()) {
            renderProperties.put(property, property.get(null));
        }

        return renderProperties;
    }
}
