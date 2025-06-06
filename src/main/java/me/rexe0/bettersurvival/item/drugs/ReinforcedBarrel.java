package me.rexe0.bettersurvival.item.drugs;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.alcohol.AgingListener;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.FermentListener;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReinforcedBarrel extends Item {
    private final BarrelType type;
    private List<WineType> previousProducts;

    public ReinforcedBarrel(BarrelType type) {
        this(type, new ArrayList<>());
    }

    public ReinforcedBarrel(BarrelType type, List<WineType> previousProducts) {
        super(Material.BARREL, ChatColor.GREEN+"Reinforced Barrel", "REINFORCED_BARREL");
        this.type = type;
        this.previousProducts = previousProducts;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setStringValue(item, "barrelType", type.name()));
        item.setItemMeta(ItemDataUtil.setStringValue(item, "previousProducts", encodePreviousProducts(previousProducts)));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A special barrel able to ferment");
        lore.add(ChatColor.GRAY+"and age alcoholic beverages.");
        lore.add(ChatColor.GRAY+"The type of wood the barrel is made");
        lore.add(ChatColor.GRAY+"from affects the flavors of the product.");
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
            recipes.put(recipe.getKey(), recipe);
        }
        return recipes;
    }
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!ItemDataUtil.isItem(e.getItemInHand(), getID())) return;
        if (e.isCancelled()) return;
        ItemStack item = e.getItemInHand();
        BarrelType barrelType = BarrelType.valueOf(ItemDataUtil.getStringValue(item, "barrelType"));

        Block block = e.getBlock();

        Barrel barrel = (Barrel) block.getState();
        barrel.setCustomName("Reinforced "+ChatColor.stripColor(barrelType.getName())+" Barrel");
        barrel.update();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        BarrelType type = BarrelType.valueOf(ItemDataUtil.getStringValue(item, "barrelType"));
        String previousProductsString = ItemDataUtil.getStringValue(item, "previousProducts");

        data.set(FermentListener.BARREL_TYPE_KEY, PersistentDataType.STRING, type.name());
        data.set(FermentListener.BARREL_PRODUCTS_KEY, PersistentDataType.STRING, previousProductsString);
        data.set(FermentListener.LAST_FERMENT_KEY, PersistentDataType.LONG, System.currentTimeMillis());
        data.set(AgingListener.BARREL_AGE_KEY, PersistentDataType.LONG, System.currentTimeMillis());
    }

    public static List<WineType> decodePreviousProducts(String string) {
        List<WineType> products = new ArrayList<>();
        if (string == null || string.isEmpty()) return products;

        String[] parts = string.split(",");
        for (String part : parts) {
            try {
                WineType type = WineType.valueOf(part);
                products.add(type);
            } catch (IllegalArgumentException e) {
                // Ignore invalid types
            }
        }

        // Limit the size to the 10 most recent products
        if (products.size() > 10)
            products = products.subList(products.size()-10, products.size());
        return products;
    }
    public static String encodePreviousProducts(List<WineType> products) {
        if (products == null || products.isEmpty()) return "";
        return String.join(",", products.stream().map(WineType::name).toList());
    }
}
