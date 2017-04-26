/*
 * Copyright (C) 2017 IModZombies - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, April 2017
 */

package me.hollasch.xray.render;

import me.hollasch.xray.math.Vec2;

/**
 * @author Connor Hollasch
 * @since Apr 25, 6:41 PM
 */
public enum RaySampler {

    NONE {
        @Override
        public Vec2 generateSampleOffset(int n) {
            return Vec2.of(0.5, 0.5);
        }
    },
    RANDOM {
        @Override
        public Vec2 generateSampleOffset(int n) {
            return Vec2.of(Math.random(), Math.random());
        }
    };

    public abstract Vec2 generateSampleOffset(int n);
}
