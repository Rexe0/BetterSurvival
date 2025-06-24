package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.Map;

public class PearlListener implements Listener {

    private ItemStack upgradeRod(ItemStack fishingRod) {
        ItemMeta meta = fishingRod.getItemMeta();
        String name = meta.hasDisplayName() ? meta.getDisplayName() : "Fishing Rod";
        meta.setDisplayName(ChatColor.GOLD+"✯ "+ChatColor.RESET+name);

        for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet())
            meta.addEnchant(enchant.getKey(), enchant.getValue()+1, true);

        fishingRod.setItemMeta(meta);
        fishingRod.setItemMeta(ItemDataUtil.setStringValue(fishingRod, "pearlUpgrade", "true"));
        return fishingRod;
    }
    private boolean isUpgraded(ItemStack item) {
        return ItemDataUtil.getStringValue(item, "pearlUpgrade").equals("true");
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        String name = e.getInventory().getRenameText();
        if (name == null || name.isEmpty()) return;

        ItemStack item = e.getInventory().getItem(0);
        ItemStack pearl = e.getInventory().getItem(1);

        if (item == null || pearl == null) return;
        if (item.getType() != Material.FISHING_ROD) return;
        if (!ItemDataUtil.isItem(pearl, ItemType.GLEAMING_PEARL.getItem().getID())) return;
        if (isUpgraded(item)) {
            e.setResult(null);
            return;
        }

        e.setResult(upgradeRod(item.clone()));
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!(e.getInventory() instanceof AnvilInventory inv)) return;
        if (e.getSlot() != 2) return;

        ItemStack result = e.getCurrentItem();
        if (result == null) return;

        ItemStack item = inv.getItem(0);
        ItemStack pearl = inv.getItem(1);

        if (item == null || pearl == null) return;
        if (item.getType() != Material.FISHING_ROD) return;
        if (!ItemDataUtil.isItem(pearl, ItemType.GLEAMING_PEARL.getItem().getID())) return;

        e.setCancelled(true);

        int cost = (item.getItemMeta() instanceof Repairable repairable) ? repairable.getRepairCost()+1 : 1;

        if (cost > player.getLevel()) {
            player.sendMessage(ChatColor.RED+"You need at least "+cost+" Levels to rename this item.");
            return;
        }
        if (e.getClick().isShiftClick()) {
            if (player.getInventory().firstEmpty() == -1) return;
            player.getInventory().addItem(result.clone());
        } else {
            if (e.getCursor().getType() != Material.AIR) return;
            player.setItemOnCursor(result.clone());
        }

        player.setLevel(player.getLevel()-cost);

        inv.setItem(0, new ItemStack(Material.AIR));
        inv.setItem(2, new ItemStack(Material.AIR));

        pearl.setAmount(pearl.getAmount()-1);

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.8f);
    }
}
