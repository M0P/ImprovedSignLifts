package com.minecraftserver.SignLift;

import java.io.Serializable;
import java.util.List;

import org.bukkit.Location;

public class LiftData implements Serializable {
    Location location;
    List<String> members;
    
    public LiftData(Location loc, List<String> members){
	this.location=loc;
	this.members=members;
    }
    
    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }
    public Location getLocation() {
        return location;
    }
    
    public void addMember(String memberName){
	
    }
    
    public void remMember(String memberName){
	
    }
}
