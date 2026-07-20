package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.List;

public class BattleBobber extends Item {
    public BattleBobber() {
        super(Material.COPPER_NUGGET, ChatColor.GREEN+"Battle Bobber", "BATTLE_BOBBER");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"Significantly increases the chance");
        lore.add(ChatColor.GRAY+"of pulling up hostile mobs while");
        lore.add(ChatColor.GRAY+"fishing in the nether.");
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
        NamespacedKey key = e.getLootTable().getKey();
        if (key.equals(LootTables.BASTION_BRIDGE.getKey())
                || key.equals(LootTables.BASTION_OTHER.getKey())
                || key.equals(LootTables.BASTION_HOGLIN_STABLE.getKey())
                || key.equals(LootTables.BASTION_TREASURE.getKey()))
            if (RandomUtil.getRandom().nextInt(4) == 0) e.getLoot().add(getItem());
    }
}
