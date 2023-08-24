package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.ArrayList;
import java.util.List;

public class VibrantBobber extends Item {
    public VibrantBobber() {
        super(Material.PURPLE_DYE, ChatColor.GREEN+"Vibrant Bobber", "VIBRANT_BOBBER");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"Increases the chance of catching");
        lore.add(ChatColor.GRAY+"higher tier fish but reduces the");
        lore.add(ChatColor.GRAY+"chance of catching treasure.");
        return lore;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();
        if (key.equals("chests/shipwreck_treasure"))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
