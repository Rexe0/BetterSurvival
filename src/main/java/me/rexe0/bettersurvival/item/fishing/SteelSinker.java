package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SteelSinker extends Item {
    public SteelSinker() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Steel Sinker", "STEEL_SINKER");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"Increases the chance to catch");
        lore.add(ChatColor.GRAY+"heavier fish the deeper");
        lore.add(ChatColor.GRAY+"the water is.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();
        if (key.equals("chests/underwater_ruin_big"))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
