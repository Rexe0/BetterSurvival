package me.rexe0.bettersurvival.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onBlockPlace(e);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onBlockBreak(e);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        DrillBlock drillBlock = (DrillBlock) ItemType.DRILL_BLOCK.getItem();
        drillBlock.onRightClick(e);
    }
}
