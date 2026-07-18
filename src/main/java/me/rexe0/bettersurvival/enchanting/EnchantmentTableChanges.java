package me.rexe0.bettersurvival.enchanting;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantmentTableChanges implements Listener {
    private final Material[] customEnchantMaterials = new Material[] {
            Material.AMETHYST_SHARD,
            Material.NAUTILUS_SHELL
    };
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Inventory inv = e.getInventory();

        ItemStack enchantingMaterial = inv.getItem(1);
        if (enchantingMaterial == null) return;
        Material material = enchantingMaterial.getType();

        boolean isCustomEnchantingMaterial = false;
        for (Material mat : customEnchantMaterials) {
            if (mat == material) {
                isCustomEnchantingMaterial = true;
                break;
            }
        }
        if (!isCustomEnchantingMaterial) return;
        int tier = e.whichButton()+1;
        enchantingMaterial.setAmount(enchantingMaterial.getAmount()-tier);

        if (material == Material.AMETHYST_SHARD) {
            Player player = e.getEnchanter();
            // Only costs 1 level
            player.setLevel(player.getLevel() + tier - 1);
            return;
        }

        if (material == Material.NAUTILUS_SHELL) {
            ItemStack item = e.getItem();
            int enchantLevel = e.getExpLevelCost();

            Enchantment hintEnch = e.getEnchantmentHint();
            int hintLvl = e.getLevelHint();

            Map<Enchantment, Integer> enchantments = new HashMap<>(item.enchantWithLevels(enchantLevel*2, false, new Random()).getEnchantments());

            Set<Enchantment> conflicts = new HashSet<>();
            for (Enchantment ench : enchantments.keySet())
                if (hintEnch.conflictsWith(ench))
                    conflicts.add(ench);

            for (Enchantment ench : conflicts)
                enchantments.remove(ench);

            enchantments.put(hintEnch, hintLvl);

            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                item.removeEnchantments();
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                }
            }, 1);
        }
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        Inventory inv = e.getClickedInventory();
        if (inv.getType() != InventoryType.ENCHANTING) return;
        if (e.getSlot() != 1) return;
        ItemStack cursor = e.getCursor();
        Material material = cursor.getType();

        if (material == Material.LAPIS_LAZULI || material.isAir()) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        boolean isCustomEnchantingMaterial = false;
        for (Material mat : customEnchantMaterials) {
            if (mat == material) {
                isCustomEnchantingMaterial = true;
                break;
            }
        }
        if (!isCustomEnchantingMaterial) return;


        if (e.getClick() == ClickType.LEFT || (e.getClick() == ClickType.RIGHT && item != null && !item.getType().isAir() && item.getType() != material)) {
            e.getWhoClicked().setItemOnCursor(e.getCurrentItem());
            e.setCurrentItem(cursor);
        } else if (e.getClick() == ClickType.RIGHT) {
            if (item != null && item.getType() == material) {
                if (item.getAmount() < item.getMaxStackSize()) {
                    item.setAmount(item.getAmount()+1);
                    cursor.setAmount(cursor.getAmount()-1);
                }
            } else {
                item = cursor.clone();
                item.setAmount(1);
                e.setCurrentItem(item);
                cursor.setAmount(cursor.getAmount()-1);
            }
        }
    }
}
