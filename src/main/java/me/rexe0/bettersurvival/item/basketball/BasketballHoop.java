package me.rexe0.bettersurvival.item.basketball;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BasketballHoop extends Item {
    public static final NamespacedKey BASKETBALL_HOOP_KEY = new NamespacedKey(BetterSurvival.getInstance(), "BASKETBALL_HOOP");
    public BasketballHoop() {
        super(Material.CAULDRON, ChatColor.GREEN+"Basketball Hoop", "BASKETBALL_HOOP");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A hoop that allows you to");
        lore.add(ChatColor.GRAY+"score with a basketball.");
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

    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        data.set(BASKETBALL_HOOP_KEY, PersistentDataType.BOOLEAN, true);
        return false;
    }
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(BASKETBALL_HOOP_KEY, PersistentDataType.BOOLEAN)) {
            data.remove(BASKETBALL_HOOP_KEY);

            e.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), new BasketballHoop().getItem());
        }
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("###", "$ $", "$ $");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setIngredient('$', Material.STRING);
        recipe.setGroup("BASKETBALL");
        return recipe;
    }
}
