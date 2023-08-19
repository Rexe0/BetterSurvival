package me.rexe0.bettersurvival.item;

import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

    public void onRightClick(Player player) {

    }

    public void onLeftClick(Player player) {

    }


    public void armorEquipped(Player player) {

    }


    public List<Recipe> getRecipes() {
        return new ArrayList<>();
    }
    public Recipe getRecipe() {
        return null;
    }
    public List<String> getLore() {
        return new ArrayList<>();
    }

}
