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
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.List;

public class BarbedHook extends Item {
    public BarbedHook() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Barbed Hook", "BARBED_HOOK");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"When reeling in higher tier fish, the");
        lore.add(ChatColor.GRAY+"fish is less likely to make erratic movements");
        lore.add(ChatColor.GRAY+"while in the green zone.");
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
        if (e.getLootTable().getKey().equals(LootTables.ABANDONED_MINESHAFT.getKey()))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
