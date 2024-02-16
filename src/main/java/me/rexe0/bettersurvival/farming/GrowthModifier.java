package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GrowthModifier implements Listener {
    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        Block block = e.getBlock();
        if (!(block.getBlockData() instanceof Ageable ageable)) return;

        // Crop grow depends on season. Spring: 60% Growth, Summer or Autumn: 50%, Winter: 20%
        double growthChance = switch (Season.getSeason()) {
            default -> 0.5;
            case SPRING -> 0.6;
            case WINTER -> 0.2;
        };

        // If its raining, increase growth chance by 20%
        if (SeasonListener.getCurrentWeather() == SeasonListener.Weather.RAIN
        || SeasonListener.getCurrentWeather() == SeasonListener.Weather.STORM) growthChance += 0.2;

        // If there is a sniffer within 20 blocks of the crop, increase the growth chance by 20%
        for (Sniffer sniffer : block.getWorld().getEntitiesByClass(Sniffer.class)) {
            if (sniffer.getLocation().distanceSquared(block.getLocation()) >= 400) continue;
            // Increase by additional 20%
            growthChance += 0.2;
            break;
        }

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

        if (data.has(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER))
            growthChance += 0.15*data.get(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER);

        if (RandomUtil.getRandom().nextDouble() >= growthChance) {
            e.setCancelled(true);
            return;
        }
        int extraGrowth = 0;
        while (growthChance > 1) {
            growthChance -= 1;
            if (RandomUtil.getRandom().nextDouble() < growthChance) extraGrowth++;
        }
        ageable.setAge(Math.min(ageable.getMaximumAge(), ageable.getAge()+extraGrowth));
        block.setBlockData(ageable);
    }
}
