package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class TreasureDrop {
    private final ItemStack item;
    private final int minAmount;
    private final int maxAmount;
    private int weight;

    public TreasureDrop(ItemStack item, int minAmount, int maxAmount, int weight) {
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }



    public static List<TreasureDrop> getTreasureDrops(ItemType fishingRod) {
        List<TreasureDrop> drops = new ArrayList<>();
        drops.add(new TreasureDrop(ItemType.BAIT.getItem().getItem(), 3, 7, 14));
        drops.add(new TreasureDrop(new ItemStack(Material.RAW_IRON), 2, 6, 14));
        drops.add(new TreasureDrop(new ItemStack(Material.RAW_GOLD), 1, 3, 10));
        drops.add(new TreasureDrop(new ItemStack(Material.NAUTILUS_SHELL), 1, 2, 10));
        drops.add(new TreasureDrop(new ItemStack(Material.PRISMARINE_CRYSTALS), 3, 7, 7));
        drops.add(new TreasureDrop(new ItemStack(ItemType.PLATINUM_ORE.getItem().getItem()), 1, 1, 6));
        drops.add(new TreasureDrop(getWaterBreathingPotion(), 1, 1, 5));
        drops.add(new TreasureDrop(new ItemStack(Material.HEART_OF_THE_SEA), 1, 1, 4));
        drops.add(new TreasureDrop(new ItemStack(Material.DIAMOND), 1, 3, 2));
        drops.add(new TreasureDrop(new ItemStack(Material.BOOK), 1, 1, 2)); // Lvl 30 Book
        drops.add(new TreasureDrop(new ItemStack(Material.TRIDENT), 1, 1, 1));

        int level = 0;

        if (fishingRod != null) level = switch (fishingRod) {
            case COPPER_FISHING_ROD -> 1;
            case PLATINUM_FISHING_ROD -> 2;
            case RESONANT_FISHING_ROD -> 3;
            default -> 0;
        };
        for (TreasureDrop drop : drops)
            drop.weight += level - 1;

        return drops;
    }
    private static ItemStack getWaterBreathingPotion() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionType(PotionType.WATER_BREATHING);
        item.setItemMeta(meta);
        return item;
    }


    public static ItemStack getTreasureItem(Player player, ItemType fishingRod) {
        if (!player.getDiscoveredRecipes().contains(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_FISHING_ROD.getItem().getID()))
                && RandomUtil.getRandom().nextInt(0, 100) == 0) {
            ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
            KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_AQUA+"Lost Fisherman Knowledge");
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.COPPER_FISHING_ROD.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.PLATINUM_FISHING_ROD.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_FISHING_ROD.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_INGOT.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.DULL_LURE.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.SHINY_LURE.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.MAGNET.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.PREMIUM_BAIT.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.DRILL_BLOCK.getItem().getID()));
            item.setItemMeta(meta);
            return item;
        }
        List<TreasureDrop> drops = getTreasureDrops(fishingRod);

        int totalWeight = 0;
        for (TreasureDrop drop : drops)
            totalWeight += drop.getWeight();

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < drops.size() - 1; ++idx) {
            r -= drops.get(idx).getWeight();
            if (r <= 0.0) break;
        }

        TreasureDrop drop = drops.get(idx);

        ItemStack item = drop.getItem();
        if (item.getType() == Material.BOOK)
            item = Bukkit.getItemFactory().enchantItem(item, 30, false);
        item.setAmount(RandomUtil.getRandom().nextInt(drop.getMinAmount(), drop.getMaxAmount()+1));
        return item;
    }

}
