package com.minecraftserver.improvedsignlifts;

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
    private final ImprovedSignLift plugin;

    public SignLiftBlockListener(ImprovedSignLift instance) {
        plugin = instance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        final Block block = event.getBlock();
        boolean canBreak = true;
        boolean isSign = false;
        boolean isSignOnBlock = false;
        BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.UP };

        if (block.getType() == Material.SIGN||block.getType() == Material.WALL_SIGN) {
            if (plugin.isBlockSignLift(block)) canBreak = canBreakBlock(block, player);
            isSign = true;
        } else {
            for (BlockFace face : faces) {
                if (plugin.isBlockSignLift(block.getRelative(face))) {
                    canBreak &= canBreakBlock(block.getRelative(face), player);
                    isSignOnBlock = true;
                }
            }
        }

        if (canBreak) {
            if (isSign)
                LiftDataManager.remLift(block.getLocation(), event.getPlayer().getName());
            else if (isSignOnBlock)
                for (BlockFace face : faces)
                    if (plugin.isBlockSignLift(block.getRelative(face)))
                        LiftDataManager.remLift(block.getRelative(face).getLocation(), event
                                .getPlayer().getName());

        } else {
            event.setCancelled(true);
            player.sendMessage(plugin.getDeniedDestroy());
            if (isSign) {
                plugin.getServer().getScheduler()
                        .scheduleAsyncDelayedTask(this.plugin, new Runnable() {
                            public void run() {
                                BlockState state = block.getState();
                                if (state instanceof Sign) {
                                    ((Sign) state).update();
                                }
                            }
                        });
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String lineLift = event.getLine(1);
        if (lineLift.length() < 3) return;
        boolean isPrivate = false;
        if (lineLift.startsWith(plugin.getPrivateOpen())
                && lineLift.endsWith(plugin.getPrivateClose())) {
            isPrivate = true;
        }
        // Remove the prefix and suffix
        lineLift = lineLift.substring(1, lineLift.length() - 1);
        if (lineLift.equalsIgnoreCase(plugin.getLiftString())
                || lineLift.equalsIgnoreCase(plugin.getLiftUpString())
                || lineLift.equalsIgnoreCase(plugin.getLiftDownString())) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            event.setLine(3, plugin.shortPlayerName(player.getName()));
            if (isPrivate) {
                if (player.hasPermission("signlift.create.private.own")) {
                    LiftDataManager.addLift(event.getBlock().getLocation(), player.getName());
                    return;
                }
            } else if (player.hasPermission("signlift.create.normal")) {
                return;
            }
            player.sendMessage(plugin.getDeniedCreate());
            event.setCancelled(true);
            block.setType(Material.AIR);
            block.getWorld()
                    .dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
        }
    }

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        BlockState block = event.getBlock().getState();
        if (block instanceof Sign) {
            Sign sign = (Sign) block;
            String line = sign.getLine(1);
            if (line.length() > 2) {
                line = line.substring(1, line.length() - 1);
                if (line.equalsIgnoreCase(plugin.getLiftString())
                        || line.equalsIgnoreCase(plugin.getLiftUpString())
                        || line.equalsIgnoreCase(plugin.getLiftDownString())) {
                    event.setBuildable(false);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockState block = event.getBlockAgainst().getState();
        if (block instanceof Sign) {
            Sign sign = (Sign) block;
            String line = sign.getLine(1);
            if (line.length() > 2) {
                line = line.substring(1, line.length() - 1);
                if (line.equalsIgnoreCase(plugin.getLiftString())
                        || line.equalsIgnoreCase(plugin.getLiftUpString())
                        || line.equalsIgnoreCase(plugin.getLiftDownString())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean canBreakBlock(Block block, Player player) {
        Sign sign = (Sign) block;
        String lineOwner = sign.getLine(3).toString();
        if (lineOwner.equals(plugin.shortPlayerName(player.getName()))) {
            return true;
        } else {
            return false;
        }
    }

}
