package me.rexe0.bettersurvival.weather;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SeasonListener {
    private static Weather currentWeather;
    private static Weather weatherForecast;

    public static Weather getCurrentWeather() {
        return currentWeather;
    }

    public static Weather getWeatherForecast() {
        return weatherForecast;
    }

    public static int getDays() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = board.getObjective("day");

        if (objective == null) return 0;

        return objective.getScore("counter").getScore();
    }

    public static void addDay() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = board.getObjective("day");
        if (objective == null) {
            objective = board.registerNewObjective("day", Criteria.DUMMY, "Day");
            objective.getScore("counter").setScore(0);
            return;
        }

        objective.getScore("counter").setScore(objective.getScore("counter").getScore()+1);
    }

    // Run every tick
    public static void run() {
        World world = BetterSurvival.getInstance().getDefaultWorld();

        tick(world);

        for (Chunk chunk : world.getLoadedChunks())
            if (RandomUtil.getRandom().nextInt(10) == 0)
                tick(world.getHighestBlockAt(RandomUtil.getRandom().nextInt(16) + chunk.getX() * 16,
                        RandomUtil.getRandom().nextInt(16) + chunk.getZ() * 16));
    }

    private static void tick(World world) {
        Season season = Season.getSeason();

        // Ensures the weather stays as it is until the next day
        if (currentWeather == null) currentWeather = Weather.CLEAR;
        if (weatherForecast == null) weatherForecast = Weather.CLEAR;

        switch (currentWeather) {
            default -> world.setClearWeatherDuration(100);
            case RAIN -> world.setStorm(true);
            case STORM -> world.setThundering(true);
        }

        if (currentWeather == Weather.SNOW || currentWeather == Weather.BLIZZARD) {
            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if (loc.getY() <= world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ())) continue;

                // Ensure they are in a biome that it can snow in
                Biome biome = ((CraftWorld) world).getHandle().getBiome(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).value();
                if (biome.climateSettings.temperature() >= 0.95) continue;

                if (currentWeather == Weather.BLIZZARD) {
                    // Leather armor slows or stops rate of freezing
                    int leather = getLeatherPiecesWorn(player);
                    if (leather == 0 || (leather <= 2 && world.getGameTime() % 2 == 0))
                        // +3/+5 to combat freeze reduction because player isn't necessarily in powder snow
                        player.setFreezeTicks(Math.min(player.getMaxFreezeTicks(), player.getFreezeTicks() + (leather > 0 ? 5 : 3)));

                    // We have to tick damage ourselves because if the player isn't in powdered snow, the tick down prevents the damage from happening.
                    if (player.isFrozen() && world.getGameTime() % 20 == 0)
                        player.damage(1);
                }

                player.spawnParticle(Particle.SNOWFLAKE, loc.add(0, 5, 0), currentWeather == Weather.BLIZZARD ? 40 : 10,
                        10, 10, 10, 0);
            }
        }

        // Start of the day
        if (world.getTime() == 1) {
            // Increment day counter
            addDay();

            if (weatherForecast != null) {
                switch (weatherForecast) {
                    case CLEAR -> currentWeather = Weather.CLEAR;
                    case RAIN -> {
                        currentWeather = Weather.RAIN;
                        world.setStorm(true);
                    }
                    case STORM -> {
                        currentWeather = Weather.STORM;
                        world.setThundering(true);
                    }
                    case SNOW -> currentWeather = Weather.SNOW;
                    case BLIZZARD -> currentWeather = Weather.BLIZZARD;
                }
                weatherForecast = null;
            }

            // Weather Check
            double rainChance = switch (season) {
                default -> 0.2;
                case SUMMER, WINTER -> 0.5;
            };
            if (RandomUtil.getRandom().nextDouble() < rainChance) {
                // Storm Chance
                if (RandomUtil.getRandom().nextDouble() < rainChance)
                    weatherForecast = season == Season.WINTER ? Weather.BLIZZARD : Weather.STORM;
                else
                    weatherForecast = season == Season.WINTER ? Weather.SNOW : Weather.RAIN;
            } else weatherForecast = Weather.CLEAR;
        }

    }

    private static void tick(Block block) {
        Season season = Season.getSeason();

        Block above = block.getLocation().add(0, 1, 0).getBlock();
        if (block.getType().isOccluding() || Tag.LEAVES.isTagged(block.getType())) {
            Biome biome = ((CraftBlock)block).getHandle().getBiome(new BlockPos(block.getX(), block.getY(), block.getZ())).value();

            // Snow melts in sunny Spring, in certain warm-hot biomes
            if (biome.climateSettings.temperature() > 0.1) {
                if (season == Season.SPRING && currentWeather == Weather.CLEAR) {
                    if (above.getType() == Material.POWDER_SNOW) {
                        above.setType(Material.SNOW);
                        Snow data = (Snow) above.getBlockData();
                        data.setLayers(7);
                        above.setBlockData(data);
                    } else if (above.getType() == Material.SNOW) {
                        Snow data = (Snow) above.getBlockData();
                        int layers = data.getLayers()-2;
                        if (layers <= 0) above.setType(Material.AIR);
                        else {
                            data.setLayers(layers);
                            above.setBlockData(data);
                        }
                    }
                }
            }

            // Snow fals during blizzards and snowy days, in certain cold-warm biomes
            if (biome.climateSettings.temperature() < 0.95) {
                if (currentWeather == Weather.SNOW || currentWeather == Weather.BLIZZARD) {
                    // nextBoolean decreases chance for snow to fall in a regular snowy day
                    if (above.getType() == Material.AIR || above.getType() == Material.GRASS || above.getType() == Material.TALL_GRASS
                            && (currentWeather == Weather.BLIZZARD || RandomUtil.getRandom().nextBoolean())) {
                        above.setType(Material.SNOW);
                    } else if (above.getType() == Material.SNOW && currentWeather == Weather.BLIZZARD) {
                        Snow data = (Snow) above.getBlockData();
                        int layers = data.getLayers() + 1;

                        if (layers >= data.getMaximumLayers()) above.setType(Material.POWDER_SNOW);
                        else {
                            data.setLayers(layers);
                            above.setBlockData(data);
                        }
                    }
                }
            }
        }
    }


    private static int getLeatherPiecesWorn(Player player) {
        int leatherAmount = 0;
        for (ItemStack item : player.getEquipment().getArmorContents()) {
            if (item == null) continue;
            if (item.getType().toString().contains("LEATHER")) leatherAmount++;
        }
        return leatherAmount;
    }
    public enum Weather {
        CLEAR,
        RAIN,
        STORM,
        SNOW,
        BLIZZARD
    }
}