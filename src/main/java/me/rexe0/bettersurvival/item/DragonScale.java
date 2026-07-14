package me.rexe0.bettersurvival.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public class DragonScale extends Item {
    public DragonScale() {
        super(Material.POPPED_CHORUS_FRUIT, ChatColor.LIGHT_PURPLE+"Dragon Scale", "DRAGON_SCALE");
    }


    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(ChatColor.GRAY+"A scale shed from a slain");
        lore.add(ChatColor.GRAY+"dragon.");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Used to repair elytras");
        lore.add(ChatColor.GRAY+"or craft the Weather Beacon.");
        return lore;
    }
}