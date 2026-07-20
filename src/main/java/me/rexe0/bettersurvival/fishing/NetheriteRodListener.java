package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class NetheriteRodListener implements Listener {
    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent e) {
        SmithingInventory inv = e.getInventory();
        if (inv.getInputTemplate() == null || inv.getInputTemplate().getType() != Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE || inv.getInputMineral() == null || inv.getInputMineral().getType() != Material.NETHERITE_INGOT) return;
        ItemStack item = inv.getInputEquipment();
        ItemType type = ItemDataUtil.getItemType(item);
        if (type != ItemType.TUNGSTEN_FISHING_ROD) return;
        ItemStack result = ItemType.NETHERITE_FISHING_ROD.getItem().getItem();
        ItemMeta meta = item.getItemMeta();
        ItemMeta resultMeta = result.getItemMeta();

        if (PearlListener.isUpgraded(item))
            PearlListener.upgradeRod(result);

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        result.setItemMeta(resultMeta);
        e.setResult(result);
    }
}
