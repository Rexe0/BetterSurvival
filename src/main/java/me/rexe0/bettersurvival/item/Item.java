package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Item {
    private Material material;
    private String name;
    private String ID;

    public Item(Material material, String name, String ID) {
        this.material = material;
        this.name = name;
        this.ID = ID;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(getLore());
        item.setItemMeta(meta);
        item.setItemMeta(ItemDataUtil.setStringValue(item, "itemID", ID));
        return item;
    }

    public void holdCheck(Player player) {

    }

    // Return whether to cancel the event
    public boolean onRightClick(Player player) {
        return false;
    }

    // Return whether to cancel the event
    public boolean onLeftClick(Player player) {
        return false;
    }


    public void armorEquipped(Player player) {

    }


    public void onLootGenerate(LootGenerateEvent e) {

    }


    public void onConsume(Player player) {

    }

    // Return whether to cancel the event
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return false;
    }

    public double onArrowDamage(LivingEntity entity, Player player, Arrow arrow, double damage) {
        return damage;
    }
    public void onArrowShoot(Player player, Arrow arrow) {

    }


    public Map<NamespacedKey, Recipe> getRecipes() {
        return new HashMap<>();
    }
    public Recipe getRecipe() {
        return null;
    }
    public List<String> getLore() {
        return new ArrayList<>();
    }

}
