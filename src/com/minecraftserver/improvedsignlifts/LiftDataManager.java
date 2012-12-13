package com.minecraftserver.improvedsignlifts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.acl.Owner;
import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class LiftDataManager {
    static private ImprovedSignLift plugin;

    public static void init(ImprovedSignLift parent) {
        plugin = parent;

    }

    public static void saveLifts(List<LiftData> ld, String ownerName) {
        File dir = liftDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File ownerFile = new File(dir + File.separator + ownerName);
        try {
            if (!ownerFile.exists()) ownerFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(ownerFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ld);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LiftData> loadLifts(String ownerName) {
        File dir = liftDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File ownerFile = new File(dir + File.separator + ownerName);
        try {
            if (!ownerFile.exists()) {
                Bukkit.broadcastMessage("no file found");
                List<LiftData> list = new Vector<>();
                saveLifts(list, ownerName);
            }
            FileInputStream fis = new FileInputStream(ownerFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<LiftData> lifts = (List<LiftData>) ois.readObject();
            ois.close();
            fis.close();
            return lifts;
        } catch (Exception e) {
            Bukkit.broadcastMessage("error reading file:" + e);
            e.printStackTrace();
        }
        return null;
    }

    public static void addLift(Location loc, String ownerName) {
        List<LiftData> lifts = loadLifts(ownerName);
        if (lifts == null) lifts = new Vector<>();
        lifts.add(new LiftData(loc, null));
        saveLifts(lifts, ownerName);
    }

    public static void remLift(Location loc, String ownerName) {
        List<LiftData> lifts = loadLifts(ownerName);
        if (lifts != null) for (LiftData ld : lifts)
            if (ld.getLocation().equals(loc)) {
                lifts.remove(ld);
                break;
            }
        saveLifts(lifts, ownerName);
    }

    public static List<LiftData> getLifts(String ownerName) {
        return loadLifts(ownerName);
    }

    public static List<String> getMembersOfLift(Location loc, String ownerName) {
        List<LiftData> lifts = loadLifts(ownerName);
        Bukkit.broadcastMessage("Load successfull?:" + lifts == null ? "N" : "Y");
        if (lifts != null) for (LiftData ld : lifts)
            if (ld.getLocation().equals(loc)) {
                Bukkit.broadcastMessage("found the lift");
                return ld.getMembers();
            }
        return null;
    }

    public static boolean isMemberOfLift(Location loc, String ownerName, String playerName) {
        List<LiftData> lifts = loadLifts(ownerName);
        if (lifts != null) for (LiftData ld : lifts)
            if (ld.getLocation().equals(loc)) {
                return ld.getMembers().contains(playerName);
            }
        return false;
    }

    public static void addMemberToLift(Location loc, String ownerName, String playerName) {
        Bukkit.broadcastMessage("trying to load lifts");
        List<LiftData> lifts = loadLifts(ownerName);
        if (lifts != null) {
            Bukkit.broadcastMessage("Loaded successfulll");
            for (LiftData ld : lifts) {
                Bukkit.broadcastMessage("Current Lift Location:" + ld.getLocation().getBlockX()
                        + ", " + ld.getLocation().getBlockY() + ", " + ld.getLocation().getBlockZ());
                Bukkit.broadcastMessage("Current Sign Location:" + loc.getBlockX() + ", "
                        + loc.getBlockY() + ", " + loc.getBlockZ());
                if (ld.getLocation().equals(loc)) {
                    Bukkit.broadcastMessage("Found lift at: " + ld.getLocation().getBlockX());
                    lifts.remove(ld);
                    ld.addMember(playerName);
                    lifts.add(ld);
                    break;
                }
            }
            saveLifts(lifts, ownerName);
        }
    }

    public static void remMemberFromLift(Location loc, String ownerName, String playerName) {
        List<LiftData> lifts = loadLifts(ownerName);
        if (lifts != null) for (LiftData ld : lifts)
            if (ld.getLocation().equals(loc)) {
                lifts.remove(ld);
                ld.remMember(playerName);
                lifts.add(ld);
                break;
            }
        saveLifts(lifts, ownerName);
    }

    private static File liftDir() {
        return new File(plugin.getDataFolder() + File.separator + "players");
    }
}
