package me.rexe0.bettersurvival.gear;

import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Map;

public class AnvilRepair implements Listener {
    private final Map<Material, ItemStack> repairMaterial;
    private final Map<ItemStack, Double> repairValue;

    public AnvilRepair() {
        repairMaterial = new HashMap<>();
        repairValue = new HashMap<>();

        repairMaterial.put(Material.ELYTRA, ItemType.DRAGON_SCALE.getItem().getItem());
        repairValue.put(ItemType.DRAGON_SCALE.getItem().getItem(), 1.0);

        repairMaterial.put(Material.TRIDENT, new ItemStack(Material.PRISMARINE_CRYSTALS));
        repairValue.put(new ItemStack(Material.PRISMARINE_CRYSTALS), 0.1);
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        ItemStack tool = e.getInventory().getItem(0);
        ItemStack repairItem = e.getInventory().getItem(1);

        if (tool == null || repairItem == null || repairMaterial.get(tool.getType()) == null) return;

        if (!repairMaterial.get(tool.getType()).isSimilar(repairItem) || !(tool.getItemMeta() instanceof Damageable meta))
            return;
        if (meta.getDamage() == 0) return;
        if (((Repairable)meta).getRepairCost() >= 40) {
            e.getViewers().forEach(p -> p.sendMessage(ChatColor.RED+"Too expensive!"));
            return;
        }

        ItemStack material = repairItem.clone();
        material.setAmount(1);

        double value = repairValue.get(material);

        int needed = (int) Math.min(1/value, meta.getDamage() / (tool.getType().getMaxDurability() * value) + 1);

        ItemStack result = tool.clone();
        meta = (Damageable) result.getItemMeta();
        meta.setDamage((int) Math.max(0, meta.getDamage() - Math.min(needed, repairItem.getAmount()) * tool
                .getType().getMaxDurability() * value));
        ((Repairable) meta).setRepairCost((((Repairable) meta).getRepairCost() + 1) * 2 - 1);
        result.setItemMeta(meta);
        e.setResult(result);
    }


    @EventHandler
    public void click(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!(e.getInventory() instanceof AnvilInventory inv)) return;
        if (e.getSlot() != 2) return;

        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        ItemStack tool = inv.getItem(0);
        ItemStack repairItem = inv.getItem(1);

        if (tool == null || repairItem == null || repairMaterial.get(tool.getType()) == null) return;
        if (!repairMaterial.get(tool.getType()).isSimilar(repairItem) || !(tool.getItemMeta() instanceof Damageable meta))
            return;

        e.setCancelled(true);

        ItemStack material = repairItem.clone();
        material.setAmount(1);

        double value = repairValue.get(material);

        int needed = (int) Math.min(1/value, meta.getDamage() / (tool.getType().getMaxDurability() * value) + 1);
        int cost = ((Repairable)tool.getItemMeta()).getRepairCost()+Math.min(needed, repairItem.getAmount());

        if (cost > player.getLevel()) {
            player.sendMessage(ChatColor.RED+"You need at least "+cost+" Levels to repair this item.");
            return;
        }
        if (e.getClick().isShiftClick()) {
            if (player.getInventory().firstEmpty() == -1) return;
            player.getInventory().addItem(item.clone());
        } else {
            if (e.getCursor().getType() != Material.AIR) return;
            player.setItemOnCursor(item.clone());
        }

        player.setLevel(player.getLevel()-cost);

        inv.setItem(0, new ItemStack(Material.AIR));
        inv.setItem(2, new ItemStack(Material.AIR));

        repairItem.setAmount(Math.max(0, repairItem.getAmount() - needed));

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

    }


    @EventHandler
    public void onPrepareNetherite(PrepareAnvilEvent e) {
        ItemStack tool = e.getInventory().getItem(0);
        ItemStack repairItem = e.getInventory().getItem(1);

        if (tool == null || repairItem == null) return;
        if (!tool.getType().toString().contains("NETHERITE") || !(tool.getItemMeta() instanceof Damageable meta) || repairItem.getType() != Material.DIAMOND)
            return;
        if (meta.getDamage() == 0) return;
        if (((Repairable)meta).getRepairCost() >= 40) {
            e.getViewers().forEach(p -> p.sendMessage(ChatColor.RED+"Too expensive!"));
            return;
        }

        int needed = Math.min(4, meta.getDamage() / (tool.getType().getMaxDurability() / 4) + 1);

        ItemStack result = tool.clone();
        meta = (Damageable) result.getItemMeta();
        meta.setDamage(Math.max(0, meta.getDamage() - Math.min(needed, repairItem.getAmount()) * tool
                .getType().getMaxDurability() / 4));
        ((Repairable) meta).setRepairCost((((Repairable) meta).getRepairCost() + 1) * 2 - 1);
        result.setItemMeta(meta);
        e.setResult(result);
    }


    @EventHandler
    public void clickNetherite(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!(e.getInventory() instanceof AnvilInventory inv)) return;
        if (e.getSlot() != 2) return;

        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        ItemStack tool = inv.getItem(0);
        ItemStack repairItem = inv.getItem(1);

        if (tool == null || repairItem == null) return;
        if (!tool.getType().toString().contains("NETHERITE") || !(tool.getItemMeta() instanceof Damageable meta) || repairItem.getType() != Material.DIAMOND)
            return;

        e.setCancelled(true);

        int needed = Math.min(4, meta.getDamage() / (tool.getType().getMaxDurability() / 4) + 1);
        int cost = ((Repairable)tool.getItemMeta()).getRepairCost()+Math.min(needed, repairItem.getAmount());

        if (cost > player.getLevel()) {
            player.sendMessage(ChatColor.RED+"You need at least "+cost+" Levels to repair this item.");
            return;
        }
        if (e.getClick().isShiftClick()) {
            if (player.getInventory().firstEmpty() == -1) return;
            player.getInventory().addItem(item.clone());
        } else {
            if (e.getCursor().getType() != Material.AIR) return;
            player.setItemOnCursor(item.clone());
        }

        player.setLevel(player.getLevel()-cost);

        inv.setItem(0, new ItemStack(Material.AIR));
        inv.setItem(2, new ItemStack(Material.AIR));

        repairItem.setAmount(Math.max(0, repairItem.getAmount() - needed));

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

    }


}
