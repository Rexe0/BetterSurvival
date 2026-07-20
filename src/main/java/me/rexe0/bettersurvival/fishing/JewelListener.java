package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JewelListener implements Listener {

    public static ItemStack upgradeHelmet(ItemStack helmet) {
        ItemMeta meta = helmet.getItemMeta();
        String name = meta.hasDisplayName() ? meta.getDisplayName() : "Strider Helmet";
        meta.setDisplayName(ChatColor.RED+"✯ "+ChatColor.RESET+name);
        helmet.setItemMeta(meta);
        helmet.setItemMeta(ItemDataUtil.setStringValue(helmet, "jewelUpgrade", "true"));
        return helmet;
    }
    public static boolean isUpgraded(ItemStack item) {
        return ItemDataUtil.getStringValue(item, "jewelUpgrade").equals("true");
    }
    public static void helmetCheck(Player player) {
        ItemStack helmet = player.getEquipment().getHelmet();
        if (!isUpgraded(helmet)) return;
        // 10 Seconds of Fire Resistance
        if (player.getFireTicks() <= -20)
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 205, 0, true, true));
    }


    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        ItemStack item = e.getInventory().getItem(0);
        ItemStack pearl = e.getInventory().getItem(1);

        if (item == null || pearl == null) return;
        if (!item.getType().name().endsWith("HELMET")) return;
        if (!ItemDataUtil.isItem(pearl, ItemType.STRIDERS_JEWEL.getItem().getID())) return;
        if (isUpgraded(item)) {
            e.setResult(null);
            return;
        }

        e.setResult(upgradeHelmet(item.clone()));
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!(e.getInventory() instanceof AnvilInventory inv)) return;
        if (e.getSlot() != 2) return;

        ItemStack result = e.getCurrentItem();
        if (result == null || result.getType().isAir()) return;

        ItemStack item = inv.getItem(0);
        ItemStack pearl = inv.getItem(1);

        if (item == null || pearl == null) return;
        if (!item.getType().name().endsWith("HELMET")) return;
        if (!ItemDataUtil.isItem(pearl, ItemType.STRIDERS_JEWEL.getItem().getID())) return;

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
