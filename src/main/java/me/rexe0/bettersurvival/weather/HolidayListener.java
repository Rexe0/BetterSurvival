package me.rexe0.bettersurvival.weather;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowSquid;

import java.util.Random;

public class HolidayListener {
    private static int i = 0;
    public static void run() {
        i = i == 1 ? 0 : 1;

        Random random = RandomUtil.getRandom();
        World world = BetterSurvival.getInstance().getDefaultWorld();

        // Winter Solstice extends the night. Summer Solstice extends the day
//        if ((Holiday.WINTER_SOLSTICE.isDay(SeasonListener.getDays()) && world.getTime() >= 13000)
//                || (Holiday.SUMMER_SOLSTICE.isDay(SeasonListener.getDays()) && world.getTime() < 13000)) {
        if (Holiday.WINTER_SOLSTICE.isDay(SeasonListener.getDays()) && world.getTime() >= 13000) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

            if (i == 1) world.setTime(world.getTime()+1);
        } else
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

        for (Chunk chunk : SeasonListener.getLoadedChunks())
            tickHighest(world.getHighestBlockAt(random.nextInt(16) + chunk.getX() * 16,
                    random.nextInt(16) + chunk.getZ() * 16));
    }

    private static void tickHighest(Block block) {
        World world = block.getWorld();
        if (Holiday.WINTER_SOLSTICE.isDay(SeasonListener.getDays()) && world.getTime() >= 12000) winterSolsticeTick(block);

    }

    private static void winterSolsticeTick(Block blk) {
        Location loc = blk.getLocation();

        Block block = loc.getWorld().getBlockAt(loc.getBlockX(), 62, loc.getBlockZ());
        if (block.getType() != Material.WATER || !block.getBiome().toString().contains("OCEAN")) return;

        int currentAmount = (int) block.getWorld().getEntitiesByClass(GlowSquid.class).stream()
                .filter(e -> e.getLocation().distanceSquared(block.getLocation()) < 2500)
                .count();
        if (currentAmount > 25) return;
        if (RandomUtil.getRandom().nextInt(250) != 0) return;

        GlowSquid squid = (GlowSquid) block.getLocation().getWorld().spawnEntity(block.getLocation(), EntityType.GLOW_SQUID);
        // Subtract 6 from ChatColor.values().length to ensure it doesn't select italic/bold/etc.
        EntityDataUtil.setStringValue(squid, "solsticeSquidColor", ChatColor.values()[RandomUtil.getRandom().nextInt(ChatColor.values().length-6)].name());

    }
}
