package com.minecraftserver.improvedsignlifts;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SignLift for Bukkit
 */
public class ImprovedSignLift extends JavaPlugin {
    protected final static Logger        logger         = Logger.getLogger("Minecraft");
    public static final String           name           = "SignLift";

    private final SignLiftBlockListener  blockListener  = new SignLiftBlockListener(this);
    private final SignLiftPlayerListener playerListener = new SignLiftPlayerListener(this);

    private String                       liftString;
    private String                       liftUpString;
    private String                       liftDownString;
    private String                       defaultGoingUpString;
    private String                       defaultGoingDownString;
    private String                       goingUpStringFormat;
    private String                       goingDownStringFormat;
    private String                       normalOpen;
    private String                       normalClose;
    private String                       privateOpen;
    private String                       privateClose;

    private String                       deniedLift;
    private String                       deniedCreate;
    private String                       deniedDestroy;

    private Boolean                      sanityCheck;
    private HashMap<String, Location>    signEditStatus = new HashMap<String, Location>();

    public void loadConfiguration() {
        FileConfiguration cfg = this.getConfig();

        normalOpen = cfg.getString("string.normal.open", "[");
        normalClose = cfg.getString("string.normal.close", "]");
        privateOpen = cfg.getString("string.private.open", "{");
        privateClose = cfg.getString("string.private.close", "}");
        liftString = cfg.getString("string.lift", "LIFT");
        liftUpString = cfg.getString("string.up.lift", "LIFT UP");
        liftDownString = cfg.getString("string.down.lift", "LIFT DOWN");
        defaultGoingUpString = cfg.getString("string.up.default", "Going Up");
        defaultGoingDownString = cfg.getString("string.down.default", "Going Down");
        goingUpStringFormat = cfg.getString("string.up.format", "Going to %s");
        goingDownStringFormat = cfg.getString("string.down.format", "Going to %s");
        deniedLift = cfg.getString("string.message.lift.denied",
                "You don't have permission to use this lift");
        deniedCreate = cfg.getString("string.message.create.denied",
                "You don't have permission to create that sign lift");
        deniedDestroy = cfg.getString("string.message.destroy.denied",
                "You don't have permission to destroy that sign lift");

        sanityCheck = cfg.getBoolean("check.destination.paranoid", true);

    }

    public void saveConfiguration() {
        this.saveConfig();
    }

    public boolean getSanityCheck() {
        return sanityCheck;
    }

    public String getLiftString() {
        return liftString;
    }

    public String getLiftUpString() {
        return liftUpString;
    }

    public String getLiftDownString() {
        return liftDownString;
    }

    public String getDefaultGoingUpString() {
        return defaultGoingUpString;
    }

    public String getDefaultGoingDownString() {
        return defaultGoingDownString;
    }

    public String getGoingUpStringFormat() {
        return goingUpStringFormat;
    }

    public String getGoingDownStringFormat() {
        return goingDownStringFormat;
    }

    public String getDeniedLift() {
        return deniedLift;
    }

    public String getDeniedCreate() {
        return deniedCreate;
    }

    public String getDeniedDestroy() {
        return deniedDestroy;
    }

    public String getNormalOpen() {
        return normalOpen;
    }

    public String getNormalClose() {
        return normalClose;
    }

    public String getPrivateOpen() {
        return privateOpen;
    }

    public String getPrivateClose() {
        return privateClose;
    }

    public String shortPlayerName(String playerName) {
        if (playerName.length() > 15) {
            return playerName.substring(0, 15);
        }
        return playerName;
    }

    public boolean isBlockSignLift(Block block) {
        if (block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) block;
            String lineLift = sign.getLine(1).toString();
            String lineOwner = sign.getLine(3).toString();
            if (((lineLift.startsWith(getNormalOpen()) && lineLift.endsWith(getNormalClose())) || (lineLift
                    .startsWith(getPrivateOpen()) && lineLift.endsWith(getPrivateClose())))
                    && lineOwner.length() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param block
     * @return if the lift is private
     */
    public boolean isSignLiftPrivate(Block block) {
        Sign sign = (Sign) block;
        String lineLift = sign.getLine(1).toString();
        if ((lineLift.startsWith(getPrivateOpen()) && lineLift.endsWith(getPrivateClose())))
            return true;
        return false;
    }

    public void executeSignLiftAction(Sign sign, Player player) {
        LiftSign ls = new LiftSign(sign);
        ls.activate(player);
    }

    public void addSignEditStatus(final Player player, final Sign sign) {
        if (signEditStatus.containsKey(player.getName()))
            signEditStatus.remove(player.getName());
        else signEditStatus.put(player.getName(), sign.getLocation());
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                remSignEditStatus(player, sign);
            }
        }, 1200L);
        // 20=1 sec
    }

    public void remSignEditStatus(Player player, Sign sign) {
        if (signEditStatus.containsValue(sign.getLocation()))
            signEditStatus.remove(player.getName());
    }

    public boolean hasPlayerSignEditStatus(Player player) {
        return signEditStatus.containsKey(player.getName());
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender
     * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sl"))
                if (hasPlayerSignEditStatus(player)) {
                    Location loc = signEditStatus.get(player.getName());
                    if (args.length != 0) {
                        if (args[0].equals("add")) {
                            if (args.length > 1)
                                for (String s : args)
                                    if (!s.equals(args[0]))
                                        LiftDataManager.addMemberToLift(loc, player.getName(), s);

                        } else if (args[0].equals("rem")||args[0].equals("remove")) {
                            if (args.length > 1)
                                for (String s : args)
                                    if (!s.equals(args[0]))
                                        LiftDataManager.remMemberFromLift(loc, player.getName(), s);

                        }
                    }
                } else player.sendMessage("You have to select a sign lift first");

        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        PluginManager pm = getServer().getPluginManager();

        loadConfiguration();
        saveConfiguration();

        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);

        LiftDataManager.init(this);

        log("Improved Sign Lifts - Version " + pdfFile.getVersion() + " is enabled");

    }

    @Override
    public void onDisable() {
        log("Improved Sign Lifts is Disabled");
    }

    public static void log(String txt) {
        logger.log(Level.INFO, String.format("[%s] %s", name, txt));
    }

}