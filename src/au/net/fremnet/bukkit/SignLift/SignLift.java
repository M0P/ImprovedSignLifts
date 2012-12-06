/*
    SignLift Bukkit plugin for Minecraft
    Copyright (C) 2011 Shannon Wynter (http://fremnet.net/)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package au.net.fremnet.bukkit.SignLift;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SignLift for Bukkit
 *
 * @author freman
 */
public class SignLift extends JavaPlugin {
	protected final static Logger logger = Logger.getLogger("Minecraft");
    public static final String name = "SignLift";

	private final SignLiftBlockListener blockListener = new SignLiftBlockListener(this);
	private final SignLiftPlayerListener playerListener = new SignLiftPlayerListener(this);
	
	private String liftString;
	private String liftUpString;
	private String liftDownString;
	private String defaultGoingUpString;
	private String defaultGoingDownString;
	private String goingUpStringFormat;
	private String goingDownStringFormat;
	private String normalOpen;
	private String normalClose;
	private String privateOpen;
	private String privateClose;
	
	private String deniedLift;
	private String deniedCreate;
	private String deniedDestroy;
	
	private Boolean sanityCheck;
	
	public SignLift() {
		super();
		LiftSign.init(this);
	}
	
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
        deniedLift = cfg.getString("string.message.lift.denied", "You don't have permission to use this lift");
        deniedCreate = cfg.getString("string.message.create.denied", "You don't have permission to create that sign lift");
        deniedDestroy = cfg.getString("string.message.destroy.denied", "You don't have permission to destroy that sign lift");

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
			return playerName.substring(0,15);
		}
		return playerName;
	}
	
    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        PluginManager pm = getServer().getPluginManager();

        loadConfiguration();
        saveConfiguration();

        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        
        log("Version " + pdfFile.getVersion()+ " - Copyright 2012 - Shannon Wynter (http://fremnet.net) is enabled");

	}

    @Override
    public void onDisable() {
        log("Disabled");
    }

	public static void log(String txt) {
		logger.log(Level.INFO, String.format("[%s] %s", name, txt));
    }

}