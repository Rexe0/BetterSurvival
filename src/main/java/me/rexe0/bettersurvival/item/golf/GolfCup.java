package me.rexe0.bettersurvival.item.golf;

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

public class GolfCup extends Item {
    public static final NamespacedKey GOLF_CUP_KEY = new NamespacedKey(BetterSurvival.getInstance(), "GOLF_CUP");
    public GolfCup() {
        super(Material.CAULDRON, ChatColor.GREEN+"Golf Hole", "GOLF_HOLE");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A cup that can be placed");
        lore.add(ChatColor.GRAY+"wherever you want.");
        lore.add(ChatColor.GRAY+"Upon sinking a golf ball");
        lore.add(ChatColor.GRAY+"in a cup, a message will be");
        lore.add(ChatColor.GRAY+"broadcasted to all nearby");
        lore.add(ChatColor.GRAY+"players.");
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

        data.set(GOLF_CUP_KEY, PersistentDataType.BOOLEAN, true);
        return false;
    }
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(GOLF_CUP_KEY, PersistentDataType.BOOLEAN)) {
            data.remove(GOLF_CUP_KEY);

            e.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), new GolfCup().getItem());
        }
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("# #", "# #", "###");
        recipe.setIngredient('#', Material.COPPER_INGOT);
        recipe.setGroup("GOLF");
        return recipe;
    }
}
