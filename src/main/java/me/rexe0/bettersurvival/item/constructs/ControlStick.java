package me.rexe0.bettersurvival.item.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.constructs.GhastConstruct;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ControlStick extends Item {
    public ControlStick() {
        super(Material.STICK, ChatColor.GREEN+"Control Stick", "CONTROL_STICK");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A tool used to activate");
        lore.add(ChatColor.GRAY+"abilities related to");
        lore.add(ChatColor.GRAY+"Ghast Constructs.");
        lore.add(" ");
        lore.add(ChatColor.YELLOW+"Left Click to activate the");
        lore.add(ChatColor.YELLOW+"Load ability.");
        lore.add(" ");
        lore.add(ChatColor.YELLOW+"Right Click to activate the");
        lore.add(ChatColor.YELLOW+"Miscellaneous ability.");
        return lore;
    }

    @Override
    public boolean onLeftClick(Player player) {
        if (!(player.getVehicle() instanceof HappyGhast ghast)) return false;
        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());
        construct.getLoad().onLeftClick(construct, player);
        return false;
    }

    @Override
    public boolean onRightClick(Player player) {
        if (!(player.getVehicle() instanceof HappyGhast ghast)) return false;
        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());
        construct.getMiscellaneous().onRightClick(construct, player);
        return false;
    }


    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
    @Override
    public Recipe getRecipe() {
        ItemStack item = ItemType.CONTROL_STICK.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape(" $ ", " # ", " # ");
        recipe.setIngredient('#', Material.STICK);
        recipe.setIngredient('$', Material.AMETHYST_SHARD);
        return recipe;
    }
}
