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
        MerchantRecipe trade = new MerchantRecipe(RandomUtil.getRandom().nextBoolean() ? ItemType.STOPWATCH.getItem().getItem() : ItemType.METAL_DETECTOR.getItem().getItem(), 1);
        trade.addIngredient(new ItemStack(Material.EMERALD, 30));
        trade.addIngredient(new ItemStack(trade.getResult().getType(), 1));
        trader.setRecipe(trader.getRecipeCount()-2, trade);
    }
}
