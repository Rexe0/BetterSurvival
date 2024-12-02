package me.rexe0.bettersurvival.item.drugs;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.CocaineListener;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BlockOfCocaine extends Item {

    private final int potency;
    public BlockOfCocaine(int potency) {
        super(Material.CALCITE, ChatColor.DARK_GREEN+"Block of Cocaine", "BLOCK_OF_COCAINE");
        this.potency = potency;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setIntegerValue(item, "potency", potency));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A large compact block of");
        lore.add(ChatColor.GRAY+"cocaine.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Potency: "+(ItemDataUtil.getFormattedColorString(potency+"", potency, 100))+"%");
        return lore;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(BetterSurvival.getInstance(), getID()), ItemType.BLOCK_OF_COCAINE.getItem().getItem());

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i <= 100; i++)
            ingredients.add(new Cocaine(i).getItem());

        recipe.shape("###", "###", "###");
        recipe.setIngredient('#', new RecipeChoice.ExactChoice(ingredients));
        return recipe;
    }


    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        if (e.isCancelled()) return;
        Block block = e.getBlock();
        block.setType(Material.CALCITE);

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        int potency = ItemDataUtil.getIntegerValue(e.getItemInHand(), "potency");
        data.set(CocaineListener.COCAINE_KEY, PersistentDataType.INTEGER, potency);
    }
}