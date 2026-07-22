package me.rexe0.bettersurvival.item.basketball;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Basketball extends Item {
    public Basketball() {
        super(Material.PLAYER_HEAD, ChatColor.GREEN+"Basketball", "BASKETBALL");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Left-Click to dribble the ball");
        lore.add(ChatColor.GRAY+"Right-Click to throw the ball");
        lore.add(ChatColor.GRAY+"Right-Click the top of");
        lore.add(ChatColor.GRAY+"a hoop to dunk.");
        return lore;
    }

    @Override
    public boolean onRightClick(Player player) {

        return true;
    }

    @Override
    public boolean onLeftClick(Player player) {

        return true;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        ItemMeta meta = item.getItemMeta();
//        meta.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, new AttributeModifier(new NamespacedKey(BetterSurvival.getInstance(), "basketball"), -4.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        item.setItemMeta(meta);
        return SkullUtil.getCustomSkull(item, "http://textures.minecraft.net/texture/8c240881b12729a51525517d2dd074e370006028fc044a030189cb1de08e2805");
    }

    @Override
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return true;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), getItem());
        recipe.shape("###", "# #", "###");
        recipe.setIngredient('#', Material.LEATHER);
        recipe.setGroup("BASKETBALL");
        return recipe;
    }
}
