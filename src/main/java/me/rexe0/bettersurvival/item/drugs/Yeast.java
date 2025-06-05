package me.rexe0.bettersurvival.item.drugs;

import me.rexe0.bettersurvival.item.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
}
