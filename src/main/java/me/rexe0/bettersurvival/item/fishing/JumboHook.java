package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class JumboHook extends Item {
    public JumboHook() {
        super(Material.IRON_NUGGET, ChatColor.GREEN+"Jumbo Hook", "JUMBO_HOOK");
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY+"Tackle");
        lore.add(ChatColor.GRAY+"When reeling in higher tier fish, the");
        lore.add(ChatColor.GRAY+"green zone reduces in size much slower");
        lore.add(ChatColor.GRAY+"when the fish is not within it.");
        return lore;
    }
}
