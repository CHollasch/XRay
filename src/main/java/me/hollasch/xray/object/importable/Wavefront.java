/*
 * Copyright (C) 2017 IModZombies - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, April 2017
 */

package me.hollasch.xray.object.importable;

import lombok.Getter;
import me.hollasch.xray.material.Diffuse;
import me.hollasch.xray.material.texture.SingleColorTexture;
import me.hollasch.xray.math.Vec3;
import me.hollasch.xray.object.surface.Quad;
import me.hollasch.xray.object.surface.Surface;
import me.hollasch.xray.object.surface.Triangle;

import java.util.*;

/**
 * @author Connor Hollasch
 * @since Apr 12, 6:46 PM
 */
public class Wavefront {

    @Getter
    private final String rawWavefrontContent;

    @Getter
    private Collection<Surface> surfaces;

    public Wavefront(final String rawWavefrontContent) {
        this.rawWavefrontContent = rawWavefrontContent;
        this.surfaces = new HashSet<>();

        List<Vec3> vertices = new ArrayList<>();

        for (String line : this.rawWavefrontContent.split("\n")) {
            line = line.replaceAll("( )+", " ").trim();

            if (line.startsWith("v")) {
                String[] args = line.split(" ");

                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);

                vertices.add(Vec3.of(x, y, z));
            } else if (line.startsWith("f")) {
                String[] args = line.split(" ");

                int v1 = Integer.parseInt(args[1]) - 1;
                int v2 = Integer.parseInt(args[2]) - 1;
                int v3 = Integer.parseInt(args[3]) - 1;

                if (args.length > 4) {
                   int v4 = Integer.parseInt(args[4]) - 1;

                    if (v4 < 0 || v4 >= vertices.size()) {
                        continue;
                    }

                    this.surfaces.add(
                            new Quad(
                                    vertices.get(v1),
                                    vertices.get(v2),
                                    vertices.get(v3),
                                    vertices.get(v4),
                                    new Diffuse(new SingleColorTexture(Vec3.of(.8f, .1f, .1f)))
                            )
                    );
                } else {

                    if (v1 < 0 || v1 >= vertices.size() || v2 < 0 || v2 >= vertices.size() || v3 < 0 || v3 > vertices.size()) {
                        continue;
                    }

                    this.surfaces.add(
                            new Triangle(
                                    vertices.get(v1),
                                    vertices.get(v2),
                                    vertices.get(v3),
                                    new Diffuse(new SingleColorTexture(Vec3.of(.8f, .1f, .1f)))
                            )
                    );
                }
            }
        }
    }
}
