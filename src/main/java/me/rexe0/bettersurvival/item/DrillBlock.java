package me.rexe0.bettersurvival.item;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DrillBlock extends Item {
    public DrillBlock() {
        super(Material.DROPPER, ChatColor.GREEN+"Drill Block", "DRILL_BLOCK");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);

        return item;
    }

    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        Dropper dropper = (Dropper) block.getState();
        dropper.setCustomName(ChatColor.DARK_GREEN+"Add Fuel");
        dropper.update();

        DrillEntity.addDrillEntity(block.getLocation());
        return false;
    }
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (!data.has(DrillEntity.key, PersistentDataType.BOOLEAN)) return;
        DrillEntity.removeDrillEntity(block.getLocation(), false);

        e.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), ItemType.DRILL_BLOCK.getItem().getItem());
    }
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (!data.has(DrillEntity.key, PersistentDataType.BOOLEAN)) return;
        DrillEntity.addDrillEntity(block.getLocation());
    }

    public ShapedRecipe getRecipe() {
        ItemStack item = ItemType.DRILL_BLOCK.getItem().getItem();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), item);
        recipe.shape("###", "#@#", "#&#");
        recipe.setIngredient('#', Material.COPPER_BLOCK);
        recipe.setIngredient('@', Material.DIAMOND_PICKAXE);
        recipe.setIngredient('&', Material.REDSTONE);

        return recipe;
    }
}
