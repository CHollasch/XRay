/*
 * Copyright (C) 2017 IModZombies - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, April 2017
 */

package me.hollasch.xray.object.surface;

import me.hollasch.xray.object.AABB;
import me.hollasch.xray.object.WorldObject;
import me.hollasch.xray.render.Ray;
import me.hollasch.xray.render.RayCollision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Connor Hollasch
 * @since Apr 19, 4:26 PM
 */
public class Mesh extends WorldObject {

    private Collection<Surface> surfaces = new ArrayList<>();

    public Mesh(Surface... surfaces) {
        this(Arrays.asList(surfaces));
    }

    public Mesh(Collection<? extends Surface> surfaces) {
        this.surfaces.addAll(surfaces);
    }

    @Override
    public RayCollision rayIntersect(Ray ray, double tMin, double tMax) {
        RayCollision currentRecord = null;
        double closestCollision = tMax;

        for (WorldObject object : this.surfaces) {
            RayCollision possibleRecord = object.rayIntersect(ray, tMin, closestCollision);

            if (possibleRecord != null) {
                closestCollision = possibleRecord.getTValue();
                currentRecord = possibleRecord;
            }
        }

        return currentRecord;
    }

    @Override
    public AABB getBoundingBox() {
        return null;
    }
}
