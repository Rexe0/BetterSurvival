package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

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
        for (ItemType itemType : ItemType.values())
            if (ItemDataUtil.isItem(e.getItem(), itemType.getItem().getID())) {
                if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
                    itemType.getItem().onRightClick(e.getPlayer());
                if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
                    itemType.getItem().onLeftClick(e.getPlayer());
            }
    }


    @EventHandler
    public void onPlant(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();
        if (ItemDataUtil.isItem(item, "BAIT"))
            e.setCancelled(true);
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

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Horse horse)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        // If the horse is wearing Saddle 'n' Horseshoe, halve its fall damage
        if (ItemDataUtil.isItem(horse.getInventory().getSaddle(), "SADDLE_N_HORSESHOE")) e.setDamage(e.getDamage()*0.5f);
    }
}
