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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

public class SignLiftPlayerListener implements Listener {
    static SignLift plugin;

    public SignLiftPlayerListener(SignLift parent) {
	plugin = parent;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
	if (!event.hasBlock())
	    return;
	if (event.getAction()==Action.RIGHT_CLICK_BLOCK&&(event.getClickedBlock().getType() == Material.STONE_BUTTON || event
		.getClickedBlock().getType() == Material.WOOD_BUTTON)) {
	    Block block = event.getClickedBlock();
	    byte data = block.getData();
	    Button btn = new Button(event.getClickedBlock().getType(), data);
	    BlockFace face = btn.getAttachedFace();
	    if (face == null)
		return;
	    block = block.getRelative(face, 2);
	}
	try {
	    LiftSign liftSign = new LiftSign(block);
	    liftSign.activate(event.getPlayer());
	} catch (NotASignLiftException e) {
	    // Nothin here
	}
    }
}