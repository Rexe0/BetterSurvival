package me.rexe0.bettersurvival.farming;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.Cannabis;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CannabisListener implements Listener {
    public static final NamespacedKey CANNABIS_KEY = new NamespacedKey(BetterSurvival.getInstance(), "CANNABIS_POTENCY");


    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();

        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(CANNABIS_KEY, PersistentDataType.INTEGER)) {
            e.setDropItems(false);
            int potency = data.get(CANNABIS_KEY, PersistentDataType.INTEGER);

            if (block.getType() == Material.LARGE_FERN) {
                Player player = e.getPlayer();
                NamespacedKey key = new NamespacedKey(BetterSurvival.getInstance(), ItemType.SMOKE_PIPE.getItem().getID());
                if (!player.getDiscoveredRecipes().contains(key)) {
                    player.sendMessage(ChatColor.GREEN+"You have discovered a new recipe: "+ChatColor.DARK_GREEN+"Smoke Pipe"+ChatColor.GREEN+"!");
                    player.discoverRecipe(key);
                }

                for (int i = -1; i < 2; i++)
                    new CustomBlockData(block.getRelative(0, i, 0), BetterSurvival.getInstance()).remove(CANNABIS_KEY);
            } else data.remove(CANNABIS_KEY);

            ItemStack item = new Cannabis(potency).getItem();
            if (block.getType() == Material.LARGE_FERN) item.setAmount(2);
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }

    public static void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block;
            for (int i = 0; i < 80; i++) {
                Location loc = player.getLocation().add(
                        RandomUtil.getRandom().nextInt(-64, 64),
                        RandomUtil.getRandom().nextInt(-64, 64),
                        RandomUtil.getRandom().nextInt(-64, 64));
                block = loc.getBlock();
                if (block.getLightLevel() < 5) continue;
                if (block.getType() == Material.FERN && block.getRelative(0, 1, 0).getType() == Material.AIR) {
                    PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());

                    if (data.has(CANNABIS_KEY, PersistentDataType.INTEGER)) {
                        int potency = data.get(CANNABIS_KEY, PersistentDataType.INTEGER);
                        int change = switch (Season.getSeason()) {
                            default -> 4;
                            case AUTUMN -> 3;
                            case WINTER -> 2;
                        };

                        if (data.has(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER)) {
                            change += data.get(HarvestModifier.BONEMEAL_KEY, PersistentDataType.INTEGER);
                            data.remove(HarvestModifier.BONEMEAL_KEY);
                        }
                        Biome biome = ((CraftWorld) block.getWorld()).getHandle().getBiome(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).value();
                        if (biome.climateSettings.temperature() >= 0.8f) change++;

                        potency += RandomUtil.getRandom().nextInt(change-6, change);
                        potency = Math.min(64+change*4, Math.max(0, potency));
                        setCannabisPlant(block, Bisected.Half.BOTTOM, potency, data);

                        block = block.getRelative(0, 1, 0);
                        data = new CustomBlockData(block, BetterSurvival.getInstance());
                        setCannabisPlant(block, Bisected.Half.TOP, potency, data);
                    }
                }
            }
        }
    }
    private static void setCannabisPlant(Block block, Bisected.Half half, int potency, PersistentDataContainer data) {
        block.setType(Material.LARGE_FERN, false);
        Bisected bisected = ((Bisected) block.getBlockData());
        bisected.setHalf(half);
        block.setBlockData(bisected);

        data.set(CANNABIS_KEY, PersistentDataType.INTEGER, potency);
    }
}