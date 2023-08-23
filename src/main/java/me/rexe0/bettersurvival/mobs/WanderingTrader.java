package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class WanderingTrader implements Listener {
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.WanderingTrader trader)) return;
        ItemStack item = switch (RandomUtil.getRandom().nextInt(3)) {
            default -> ItemType.STOPWATCH.getItem().getItem();
            case 1 -> ItemType.METAL_DETECTOR.getItem().getItem();
            case 2 -> ItemType.FISH_CODEX.getItem().getItem();
        };
        MerchantRecipe trade = new MerchantRecipe(item, 1);
        trade.addIngredient(new ItemStack(Material.EMERALD, 20));
        trade.addIngredient(new ItemStack(trade.getResult().getType(), 1));
        trader.setRecipe(trader.getRecipeCount()-2, trade);
    }
}
