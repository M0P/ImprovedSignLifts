package com.minecraftserver.improvedsignlifts;

import java.io.Serializable;

import org.bukkit.World;

public class LocationSerializable implements Serializable {
    private static final long serialVersionUID = 1L;
    public String             world;
    public double             x;
    public double             y;
    public double             z;
    public float              pitch;
    public float              yaw;

    public LocationSerializable(final String world, final double x, final double y, final double z,
            final float yaw, final float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
