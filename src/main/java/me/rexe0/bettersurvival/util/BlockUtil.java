package me.rexe0.bettersurvival.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;

public final class BlockUtil {
    public static boolean isHot(Block block) {
        if (block.getType() == Material.FIRE) return true;
        if (!(block.getBlockData() instanceof Campfire campfire)) return false;
        return campfire.isLit();
    }
}
