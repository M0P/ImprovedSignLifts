package com.minecraftserver.improvedsignlifts;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LiftData implements Serializable {
    private static final long serialVersionUID = 1L;
    LocationSerializable      location;
    List<String>              members;

    public LiftData(Location loc, List<String> members) {
        this.location = new LocationSerializable(loc.getWorld().getName(), loc.getX(), loc.getY(),
                loc.getZ(), loc.getYaw(), loc.getPitch());
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(location.world), location.x, location.y, location.z,
                location.yaw, location.pitch);
    }

    public void addMember(String memberName) {
        if (members == null) members = new Vector<>();
        if (!members.contains(memberName)) members.add(memberName);
    }

    public void remMember(String memberName) {
        if (members != null &&  members.contains(memberName)) members.remove(memberName);
    }
}
