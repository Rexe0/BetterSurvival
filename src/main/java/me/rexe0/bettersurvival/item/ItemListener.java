package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;

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

    @EventHandler
    public void onSmith(PrepareSmithingEvent e) {
        Inventory inv = e.getInventory();

        for (Recipe recipe : BetterSurvival.getInstance().getRecipes().values()) {
            if (!(recipe instanceof SmithingTransformRecipe smithingRecipe)) continue;

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                for (int i = 0; i < 3; i++) if (inv.getItem(i) == null) return;

                if (smithingRecipe.getTemplate().test(inv.getItem(0))
                        && smithingRecipe.getBase().test(inv.getItem(1))
                        && smithingRecipe.getAddition().test(inv.getItem(2)))
                    inv.setItem(3, smithingRecipe.getResult());

            }, 1);
        }
    }
}
