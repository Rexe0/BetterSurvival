package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.ArrayList;
import java.util.List;

public class BarbedHook extends Item {
    public BarbedHook() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Barbed Hook", "BARBED_HOOK");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"When reeling in higher tier fish, the");
        lore.add(ChatColor.GRAY+"fish is less likely to make erratic movements");
        lore.add(ChatColor.GRAY+"while in the green zone.");
        return lore;
    }

    @Override
    public void onLootGenerate(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().getKey();
        if (key.equals("chests/abandoned_mineshaft"))
            if (RandomUtil.getRandom().nextBoolean()) e.getLoot().add(getItem());
    }
}
