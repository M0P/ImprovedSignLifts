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

package com.minecraftserver.SignLift;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class SignLiftBlockListener implements Listener {
	private final SignLift plugin;
	
	public SignLiftBlockListener(SignLift instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		final Block block = event.getBlock();
		Boolean canBreak = true;
		Boolean isSign = false;
		
		if (block.getState() instanceof Sign) {
			canBreak &= canBreakBlock(block, player);
			isSign = true;
		} else {
			BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
			for (BlockFace face : faces) {
				canBreak &= canBreakBlock(block.getRelative(face), player);
			}
		}
		
		if (canBreak) {
			LiftSign liftSign;
			try {
				liftSign = new LiftSign(block);
				liftSign.remove();
			}
			catch (NotASignLiftException e) {
				// Do nothing
			}
		} else {
			event.setCancelled(true);
			player.sendMessage(plugin.getDeniedDestroy());
			if (isSign) {
				plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() {
	                public void run() {
	                    BlockState state = block.getState();
	                    if (state instanceof Sign) {
	                    	((Sign)state).update();
	                    }
	                }
	            });
			}
		}
	}

	private Boolean canBreakBlock(Block block, Player player) {
		try {
			LiftSign liftSign = new LiftSign(block);
			return liftSign.checkBreak(player);
		}
		catch (NotASignLiftException e) {
			// Do nothing
		}
		return true;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		String line = event.getLine(1);
		if (line.length() < 3) return;
		
		Boolean isPrivate = false;
		if (line.startsWith(plugin.getPrivateOpen()) && line.endsWith(plugin.getPrivateClose())) {
			isPrivate = true;
		}

		// Remove the prefix and suffix
		line = line.substring(1, line.length() - 1);
		
		if (line.equalsIgnoreCase(plugin.getLiftString()) || line.equalsIgnoreCase(plugin.getLiftUpString()) || line.equalsIgnoreCase(plugin.getLiftDownString())) {
			Block block = event.getBlock();
			Player player = event.getPlayer();
			if (isPrivate) {
				if (player.hasPermission("signlift.create.private.own")) {
					LiftSign.writeOwner(block, player.getName());
					return;
				}
			}
			else if (player.hasPermission("signlift.create.normal")) {
				return;
			}
			player.sendMessage(plugin.getDeniedCreate());
			event.setCancelled(true);
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
		}
	}

	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		BlockState block = event.getBlock().getState();
		if (block instanceof Sign) {
			Sign sign = (Sign)block;
			String line = sign.getLine(1);
			if (line.length() > 2) {
				line = line.substring(1, line.length() - 1);
				if (line.equalsIgnoreCase(plugin.getLiftString()) || line.equalsIgnoreCase(plugin.getLiftUpString()) || line.equalsIgnoreCase(plugin.getLiftDownString())) {
					event.setBuildable(false);
				}
			}
		}
	}
		
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		BlockState block = event.getBlockAgainst().getState();
		if (block instanceof Sign) {
			Sign sign = (Sign)block;
			String line = sign.getLine(1);
			if (line.length() > 2) {
				line = line.substring(1, line.length() - 1);
				if (line.equalsIgnoreCase(plugin.getLiftString()) || line.equalsIgnoreCase(plugin.getLiftUpString()) || line.equalsIgnoreCase(plugin.getLiftDownString())) {
					event.setCancelled(true);
				}
			}
		}
    }
}
