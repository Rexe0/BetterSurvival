package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import java.util.List;

public class Yeast extends Item {
    public Yeast() {
        super(Material.FROGSPAWN, ChatColor.GREEN+"Yeast", "YEAST");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A fungus vital in the production");
        lore.add(ChatColor.GRAY+"of alcoholic beverages.");
        return lore;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        if (e.getLootTable().getKey().equals(LootTables.SIMPLE_DUNGEON.getKey())) {
            for (int i = 0; i < RandomUtil.getRandom().nextInt(1, 4); i++)
                e.getLoot().add(getItem());
        }

        if (e.getLootTable().getKey().equals(LootTables.ZOMBIE.getKey())) {
            if (RandomUtil.getRandom().nextInt(200) == 0)
                e.getLoot().add(getItem());
        }
    }

    @Override
    public boolean onBlockPlace(Player player, Block block, ItemStack item) {
        return true;
    }

}
