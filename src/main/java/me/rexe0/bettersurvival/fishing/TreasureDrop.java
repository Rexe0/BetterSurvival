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

public record TreasureDrop(ItemStack item, int minAmount, int maxAmount, int weight) {
    public static TreasureDrop[] treasureDrops = new TreasureDrop[]{
            new TreasureDrop(ItemType.BAIT.getItem().getItem(), 3, 7, 14),
            new TreasureDrop(new ItemStack(Material.RAW_IRON), 2, 6, 14),
            new TreasureDrop(new ItemStack(Material.RAW_GOLD), 1, 3, 10),
            new TreasureDrop(new ItemStack(Material.NAUTILUS_SHELL), 1, 2, 10),
            new TreasureDrop(new ItemStack(Material.PRISMARINE_CRYSTALS), 3, 7, 7),
            new TreasureDrop(new ItemStack(Material.SADDLE), 1, 1, 6),
            new TreasureDrop(new ItemStack(ItemType.PLATINUM_ORE.getItem().getItem()), 1, 1, 6),
            new TreasureDrop(new ItemStack(Material.HEART_OF_THE_SEA), 1, 1, 4),
            new TreasureDrop(new ItemStack(Material.DIAMOND), 1, 3, 2),
            new TreasureDrop(new ItemStack(Material.BOOK), 1, 1, 2), // Lvl 30 Book
            new TreasureDrop(new ItemStack(Material.TRIDENT), 1, 1, 1),
    };


    public static ItemStack getTreasureItem(Player player) {
        if (!player.getDiscoveredRecipes().contains(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_FISHING_ROD.getItem().getID()))
                && RandomUtil.getRandom().nextInt(0, 100) == 0) {
            ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
            KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_AQUA+"Lost Fisherman Knowledge");
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_FISHING_ROD.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.RESONANT_INGOT.getItem().getID()));
            meta.addRecipe(new NamespacedKey(BetterSurvival.getInstance(), ItemType.DRILL_BLOCK.getItem().getID()));
            item.setItemMeta(meta);
            return item;
        }
        int totalWeight = 0;
        for (TreasureDrop drop : treasureDrops)
            totalWeight += drop.weight();

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < treasureDrops.length - 1; ++idx) {
            r -= treasureDrops[idx].weight();
            if (r <= 0.0) break;
        }
        ItemStack item = treasureDrops[idx].item();
        if (item.getType() == Material.BOOK)
            item = Bukkit.getItemFactory().enchantItem(item, 30, false);
        item.setAmount(RandomUtil.getRandom().nextInt(treasureDrops[idx].minAmount(), treasureDrops[idx].maxAmount()+1));
        return item;
    }

}
