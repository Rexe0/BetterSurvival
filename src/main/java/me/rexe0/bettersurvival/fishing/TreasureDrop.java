package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.RandomUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.RandomSourceWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public record TreasureDrop(ItemStack item, int minAmount, int maxAmount, int weight) {
    public static TreasureDrop[] treasureDrops = new TreasureDrop[]{
            new TreasureDrop(ItemType.BAIT.getItem().getItem(), 3, 7, 14),
            new TreasureDrop(new ItemStack(Material.RAW_IRON), 1, 4, 14),
            new TreasureDrop(new ItemStack(Material.RAW_GOLD), 1, 3, 10),
            new TreasureDrop(new ItemStack(Material.NAUTILUS_SHELL), 1, 2, 10),
            new TreasureDrop(new ItemStack(Material.SADDLE), 1, 1, 8),
            new TreasureDrop(new ItemStack(ItemType.PLATINUM_ORE.getItem().getItem()), 1, 1, 6),
            new TreasureDrop(new ItemStack(Material.HEART_OF_THE_SEA), 1, 1, 4),
            new TreasureDrop(new ItemStack(Material.DIAMOND), 1, 3, 2),
            new TreasureDrop(new ItemStack(Material.BOOK), 1, 1, 2), // Lvl 30 Book
            new TreasureDrop(new ItemStack(Material.TRIDENT), 1, 1, 1),
    };


    public static ItemStack getTreasureItem() {
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
            item = CraftItemStack.asBukkitCopy(EnchantmentHelper.enchantItem(new RandomSourceWrapper(new Random()), CraftItemStack.asNMSCopy(item), 30, false));
        item.setAmount(RandomUtil.getRandom().nextInt(treasureDrops[idx].minAmount(), treasureDrops[idx].maxAmount()+1));
        return item;
    }

}
