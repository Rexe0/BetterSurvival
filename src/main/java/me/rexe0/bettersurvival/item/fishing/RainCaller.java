package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.SkullUtil;
import me.rexe0.bettersurvival.weather.Holiday;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RainCaller extends Item {
    public RainCaller() {
        super(Material.PLAYER_HEAD, ChatColor.DARK_PURPLE+"Rain Caller", "RAIN_CALLER");
    }


    @Override
    public java.util.List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A supernatural artifact that can");
        lore.add(ChatColor.GRAY+"be consumed to force it to rain");
        lore.add(ChatColor.GRAY+"or snow tomorrow, depending on");
        lore.add(ChatColor.GRAY+"the current season.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);

        return SkullUtil.getCustomSkull(item, "http://textures.minecraft.net/texture/977c1fc93216e96d435cf962e1173de8d1a249b644894d72676eba732fcd56e7"
                , UUID.fromString("da333b52-e998-4f3d-85a4-8d8d60f3398a"));
    }

    @Override
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return true;
    }

    @Override
    public boolean onRightClick(Player player, ItemStack item) {

        if (SeasonListener.getWeatherForecast() != SeasonListener.Weather.CLEAR
                && SeasonListener.getWeatherForecast() != SeasonListener.Weather.WINDY) return true;

        // Can't override holiday weather
        for (Holiday holiday : Holiday.values()) {
            if (holiday.isDay(SeasonListener.getDays() + 1)) {
                player.sendMessage(ChatColor.RED+"Tomorrow is a special day and its weather cannot be changed.");
                return true;
            }
        }

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 0.75f);
        player.playSound(player.getLocation(), Sound.ENTITY_ALLAY_DEATH, 0.7f, 0.75f);


        SeasonListener.setWeatherForecast(Season.getSeason() == Season.WINTER ? SeasonListener.Weather.SNOW : SeasonListener.Weather.RAIN);

        item.setAmount(0);

        return true;
    }

    public void onAcquireTrade(Villager villager, VillagerAcquireTradeEvent e) {
        if (villager.getProfession() != Villager.Profession.FISHERMAN) return;

        if (e.getRecipe().getIngredients().get(0).getType().toString().contains("BOAT")) {
            MerchantRecipe trade = new MerchantRecipe(ItemType.RAIN_CALLER.getItem().getItem(), 0, 1, true, 15, 0);
            trade.addIngredient(new ItemStack(Material.EMERALD, 16));
            trade.addIngredient(new ItemStack(Material.HEART_OF_THE_SEA, 1));
            e.setRecipe(trade);
        }
    }
}
