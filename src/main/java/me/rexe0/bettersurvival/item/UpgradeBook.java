package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UpgradeBook extends Item {
    private Upgrade upgrade;
    public UpgradeBook(Upgrade upgrade) {
        super(Material.BOOK, ChatColor.GOLD+"Book of "+upgrade.getName(), "UPGRADE_BOOK");
        this.upgrade = upgrade;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        if (upgrade == Upgrade.BREWING) {
            lore.add(ChatColor.GRAY+"Reduces the chance for distilled");
            lore.add(ChatColor.GRAY+"spirits to contain high methanol");
            lore.add(ChatColor.GRAY+"concentrations.");
        } else if (upgrade == Upgrade.FISHING) {
            lore.add(ChatColor.GRAY+"Increases the chance to fish up");
            lore.add(ChatColor.GRAY+"two treasure items at once.");
        } else if (upgrade == Upgrade.COMBAT) {
            lore.add(ChatColor.GRAY+"Increases your base armor");
            lore.add(ChatColor.GRAY+"toughness, which reduces the");
            lore.add(ChatColor.GRAY+"damage of high strength attacks.");
        } else if (upgrade == Upgrade.MINING) {
            lore.add(ChatColor.GRAY+"Increases the chance for");
            lore.add(ChatColor.GRAY+"ores to drop additional items.");
        } else if (upgrade == Upgrade.FARMING) {
            lore.add(ChatColor.GRAY+"Increases the chance for");
            lore.add(ChatColor.GRAY+"crops to drop additional items.");
        }
        lore.add(" ");
        lore.add(ChatColor.GREEN+"Right Click to read.");
        return lore;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(1);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        item.setItemMeta(ItemDataUtil.setStringValue(item, "upgradeType", upgrade.name()));
        return item;
    }

    @Override
    public void onRightClick(Player player) {
        Upgrade upgrade = Upgrade.valueOf(ItemDataUtil.getStringValue(player.getInventory().getItemInMainHand(), "upgradeType"));
        int level = EntityDataUtil.getIntegerValue(player, "upgradeLevel."+upgrade.name());
        if (level >= 5) {
            player.sendMessage(ChatColor.RED+"You have already read this book for the maximum amount of times.");
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        player.sendMessage(ChatColor.GREEN+"You learned some things about "+upgrade.getName()+"!");

        player.getEquipment().getItemInMainHand().setAmount(0);
        EntityDataUtil.setIntegerValue(player, "upgradeLevel."+upgrade.name(), level+1);
        if (upgrade == Upgrade.COMBAT)
            player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(
                    player.getAttribute(Attribute.ARMOR_TOUGHNESS).getBaseValue() + 0.5);
    }

    public enum Upgrade {
        BREWING("Brewing"),
        FISHING("Fishing"),
        COMBAT("Combat"),
        MINING("Mining"),
        FARMING("Farming");

        private String name;
        private Upgrade(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }
}
