package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.WeatherRadio;
import me.rexe0.bettersurvival.item.fishing.CopperFishingRod;
import me.rexe0.bettersurvival.item.fishing.JumboHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Map;

public class VillagerChange implements Listener {
    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Villager villager)) return;
        ((WeatherRadio)ItemType.WEATHER_RADIO.getItem()).onAcquireTrade(villager, e);
        ((JumboHook)ItemType.JUMBO_HOOK.getItem()).onAcquireTrade(villager, e);
        ((CopperFishingRod)ItemType.COPPER_FISHING_ROD.getItem()).onAcquireTrade(villager, e);
        onTradeWolfArmor(villager, e);

    }

    private void onTradeWolfArmor(Villager villager, VillagerAcquireTradeEvent e) {
        if (villager.getProfession() != Villager.Profession.SHEPHERD) return;

        // Replace banner trade
        if (!e.getRecipe().getResult().getType().toString().contains("BANNER")) return;

        ItemStack armor = new ItemStack(Material.WOLF_ARMOR);
        // Add lvl 30 enchants to wolf armor
        for (Map.Entry<Enchantment, Integer> entry : Bukkit.getItemFactory().enchantItem(new ItemStack(Material.DIAMOND_LEGGINGS), 30, false).getEnchantments().entrySet())
            armor.addUnsafeEnchantment(entry.getKey(), entry.getValue());

        MerchantRecipe trade = new MerchantRecipe(armor, 0, 2, true, 15, 0);
        trade.addIngredient(new ItemStack(Material.EMERALD, 30));
        trade.addIngredient(new ItemStack(Material.WOLF_ARMOR, 1));
        e.setRecipe(trade);
    }
}
