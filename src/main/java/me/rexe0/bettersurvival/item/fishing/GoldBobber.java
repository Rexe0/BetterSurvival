package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.ArrayList;
import java.util.List;

public class GoldBobber extends Item {
    public GoldBobber() {
        super(Material.GOLD_NUGGET, ChatColor.GREEN+"Gold Bobber", "GOLD_BOBBER");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"Increases the chance of catching");
        lore.add(ChatColor.GRAY+"treasure but reduces the chance");
        lore.add(ChatColor.GRAY+"of catching high tier fish.");
        return lore;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();
        if (key.equals("chests/desert_pyramid"))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
