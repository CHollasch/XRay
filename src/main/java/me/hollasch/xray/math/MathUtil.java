/*
 * Copyright (C) 2017 IModZombies - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, April 2017
 */

package me.hollasch.xray.math;

/**
 * @author Connor Hollasch
 * @since Apr 19, 6:00 PM
 */
public class MathUtil {

    private static final int RANDOM_UNIT_SPHERE_VECTOR_COUNT = 10000;
    private static Vec3[] bakedUnitSphereVectors;

    static {
        bakedUnitSphereVectors = new Vec3[RANDOM_UNIT_SPHERE_VECTOR_COUNT];

        for (int i = 0; i < RANDOM_UNIT_SPHERE_VECTOR_COUNT; ++i) {
            bakedUnitSphereVectors[i] = Vec3.randomInUnitSphere();
        }
    }

    public static Vec3 bakedRandomInUnitSphere() {
        return bakedUnitSphereVectors[(int) (System.nanoTime() % bakedUnitSphereVectors.length)];
    }
}
