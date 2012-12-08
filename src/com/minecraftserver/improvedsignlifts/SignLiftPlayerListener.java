package com.minecraftserver.improvedsignlifts;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

public class SignLiftPlayerListener implements Listener {
    static ImprovedSignLift plugin;

    public SignLiftPlayerListener(ImprovedSignLift parent) {
        plugin = parent;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.STONE_BUTTON
                    || event.getClickedBlock().getType() == Material.WOOD_BUTTON) {
                Block block = event.getClickedBlock();
                byte data = block.getData();
                Button btn = new Button(event.getClickedBlock().getType(), data);
                BlockFace face = btn.getAttachedFace();
                if (face == null) return;
                block = block.getRelative(face, 2);
                if (plugin.isBlockSignLift(block))
                    plugin.executeSignLiftAction((Sign) block, event.getPlayer());
            }
            else if(event.getClickedBlock().getType()==Material.SIGN||event.getClickedBlock().getType()==Material.WALL_SIGN){
                Block block=event.getClickedBlock();
                if (plugin.isBlockSignLift(block))
                    plugin.executeSignLiftAction((Sign) block, event.getPlayer());
                
            }
        }
    }

}