package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class VillagerChange implements Listener {
    public static void limitMinorPositiveMax() {
        try {
            // Get Max field
            Field field = GossipType.class.getField("k");
            field.setAccessible(true);
            field.setInt(GossipType.MINOR_POSITIVE, 100);
            field.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onCure(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CURED) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Villager villager = ((CraftVillager)e.getEntity()).getHandle();
                villager.getGossips().remove(GossipType.MAJOR_POSITIVE);
            }
        }.runTaskLater(BetterSurvival.getInstance(), 1);
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Villager villager)) return;
        if (villager.getProfession() != org.bukkit.entity.Villager.Profession.CARTOGRAPHER) return;

        // Replace banner trade once for cartographers
        if (!e.getRecipe().getResult().getType().toString().contains("BANNER")) return;
        for (MerchantRecipe recipe : villager.getRecipes())
            if (ItemDataUtil.isItem(recipe.getResult(), ItemType.WEATHER_RADIO.getItem().getID())) return;

        MerchantRecipe trade = new MerchantRecipe(ItemType.WEATHER_RADIO.getItem().getItem(), 0, 4, true, 15, 0);
        trade.addIngredient(new ItemStack(Material.EMERALD, 16));
        trade.addIngredient(new ItemStack(Material.GOLD_INGOT, 16));
        e.setRecipe(trade);
    }
}
