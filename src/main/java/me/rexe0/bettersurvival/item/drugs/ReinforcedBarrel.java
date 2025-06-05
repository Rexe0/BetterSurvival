package me.rexe0.bettersurvival.item.drugs;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.CocaineListener;
import me.rexe0.bettersurvival.farming.alcohol.AlcoholListener;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReinforcedBarrel extends Item {
    private final BarrelType type;
    private List<ItemStack> previousProducts;

    public ReinforcedBarrel(BarrelType type) {
        super(Material.BARREL, ChatColor.GREEN+"Reinforced Barrel", "REINFORCED_BARREL");
        this.type = type;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "barrelType", type.name()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A special barrel able to ferment");
        lore.add(ChatColor.GRAY+"and age alcoholic beverages.");
        lore.add(ChatColor.GRAY+"The type of wood the barrel is made");
        lore.add(ChatColor.GRAY+"from affects the flavours of the product.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Type: " + type.getName());
        return lore;
    }
    @Override
    public Map<NamespacedKey, Recipe> getRecipes() {
        Map<NamespacedKey, Recipe> recipes = new HashMap<>();

        for (BarrelType type : BarrelType.values()) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()+"_"+type.name()), new ReinforcedBarrel(type).getItem());
            recipe.shape("###", "$@$", "###");
            recipe.setIngredient('#', type.getPlanks());
            recipe.setIngredient('@', Material.BARREL);
            recipe.setIngredient('$', Material.IRON_INGOT);
        }

        return recipes;
    }
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        if (e.isCancelled()) return;
        ItemStack item = e.getItemInHand();
        BarrelType barrelType = BarrelType.valueOf(ItemDataUtil.getStringValue(item, "barrelType"));

        Block block = e.getBlock();
        block.setType(Material.BARREL);
        Barrel barrel = (Barrel) block.getState();
        barrel.setCustomName("Reinforced "+barrelType.getName()+" Barrel");
        barrel.update();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        data.set(AlcoholListener.BARREL_TYPE_KEY, PersistentDataType.STRING, type.name());
    }
}
