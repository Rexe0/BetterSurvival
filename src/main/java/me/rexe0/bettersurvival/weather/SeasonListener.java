package me.rexe0.bettersurvival.weather;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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


    public static Set<Chunk> getLoadedChunks() {
        Set<Chunk> chunks = new HashSet<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            int cX = p.getLocation().getChunk().getX();
            int cZ = p.getLocation().getChunk().getZ();

            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    chunks.add(BetterSurvival.getInstance().getDefaultWorld().getChunkAt(cX + x, cZ + z));
                }
            }
        }
        return chunks;
    }

    // Run every tick
    public static void run() {
        World world = BetterSurvival.getInstance().getDefaultWorld();

        tick(world);

        Random random = RandomUtil.getRandom();

        for (Chunk chunk : getLoadedChunks()) {
            if (RandomUtil.getRandom().nextInt(20) == 0)
                tickHighest(world.getHighestBlockAt(random.nextInt(16) + chunk.getX() * 16,
                        random.nextInt(16) + chunk.getZ() * 16));
        }
    }

    private static void tick(World world) {
        Season season = Season.getSeason();

        for (Player player : world.getPlayers()) {
            Block block = player.getLocation().getBlock();
            if (block.getBiome() == org.bukkit.block.Biome.PLAINS
                    || block.getBiome() == org.bukkit.block.Biome.FOREST
                    || block.getBiome() == org.bukkit.block.Biome.FLOWER_FOREST
                    || block.getBiome() == org.bukkit.block.Biome.BIRCH_FOREST
                    || block.getBiome() == org.bukkit.block.Biome.SUNFLOWER_PLAINS
                    || block.getBiome() == org.bukkit.block.Biome.MEADOW) {
                Biome biome = ((CraftBlock) block).getHandle().getBiome(new BlockPos(block.getX(), block.getY(), block.getZ())).value();

                try {
                    BiomeSpecialEffects effects = biome.getSpecialEffects();
                    Field foliageColorOverride = effects.getClass().getDeclaredField("f"); // foliageColorOverride
                    foliageColorOverride.setAccessible(true);

                    Field grassColorOverride = effects.getClass().getDeclaredField("g"); // grassColorOverride
                    grassColorOverride.setAccessible(true);

                    if (season == Season.AUTUMN) {
                        if (((Optional<?>) foliageColorOverride.get(effects)).isEmpty())
                            foliageColorOverride.set(biome.getSpecialEffects(), Optional.of(Integer.parseInt("ff5e00", 16)));

                        if (((Optional<?>)grassColorOverride.get(effects)).isEmpty())
                            grassColorOverride.set(biome.getSpecialEffects(), Optional.of(Integer.parseInt("f77423", 16)));
                    } else {
                        if (((Optional<?>) foliageColorOverride.get(effects)).isPresent())
                            foliageColorOverride.set(biome.getSpecialEffects(), Optional.empty());

                        if (((Optional<?>)grassColorOverride.get(effects)).isPresent())
                            grassColorOverride.set(biome.getSpecialEffects(), Optional.empty());
                    }
                    foliageColorOverride.setAccessible(false);
                    grassColorOverride.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }



        // Ensures the weather stays as it is until the next day
        if (currentWeather == null) currentWeather = Weather.CLEAR;
        if (weatherForecast == null) weatherForecast = Weather.CLEAR;

        switch (currentWeather) {
            default -> world.setClearWeatherDuration(100);
            case RAIN -> {
                world.setStorm(true);
                world.setThundering(false);
            }
            case STORM -> {
                world.setStorm(true);
                world.setThundering(true);
            }
        }

        if (currentWeather == Weather.WINDY) {
            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if (loc.getY() <= world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ())) continue;

                if (season == Season.AUTUMN) {
                    player.spawnParticle(Particle.REDSTONE, loc.add(0, 5, 0), 10,
                            10, 10, 10, 0, new Particle.DustOptions(Color.fromRGB(255, 94, 0), 1.5f));
                } else
                    player.spawnParticle(Particle.CHERRY_LEAVES, loc.add(0, 5, 0), 5,
                            10, 10, 10, 0);

            }
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
                        world.setThundering(false);
                    }
                    case STORM -> {
                        currentWeather = Weather.STORM;
                        world.setStorm(true);
                        world.setThundering(true);
                    }
                    case SNOW -> currentWeather = Weather.SNOW;
                    case BLIZZARD -> currentWeather = Weather.BLIZZARD;
                    case WINDY -> currentWeather = Weather.WINDY;
                }
                weatherForecast = null;
            }

            // Holiday weather overrides default weather system
            for (Holiday holiday : Holiday.values()) {
                if (holiday.isDay(SeasonListener.getDays() + 1)) {
                    weatherForecast = holiday.getWeather();
                    return;
                }
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
            } else {
                if (season == Season.SPRING || season == Season.AUTUMN)
                    weatherForecast = RandomUtil.getRandom().nextDouble() < 0.2 ? Weather.WINDY : Weather.CLEAR;
                else weatherForecast = Weather.CLEAR;
            }
        }

    }

    private static void tickHighest(Block block) {
        Season season = Season.getSeason();

        Block above = block.getLocation().add(0, 1, 0).getBlock();
        if (block.getType().isOccluding() || Tag.LEAVES.isTagged(block.getType())) {
            Biome biome = ((CraftBlock) block).getHandle().getBiome(new BlockPos(block.getX(), block.getY(), block.getZ())).value();

            // Snow melts in sunny Spring, in certain warm-hot biomes
            if (biome.climateSettings.temperature() > 0.1 && season == Season.SPRING
                    && (currentWeather == Weather.CLEAR || currentWeather == Weather.WINDY))
                // Loop 0-9 to clean up any floating snow from cut down trees, etc.
                for (int i = 0; i < 10; i++) {
                    above = above.getLocation().add(0, 1, 0).getBlock();
                    if (above.getType() == Material.POWDER_SNOW) {
                        Block below = above.getLocation().subtract(0, 1, 0).getBlock();
                        // If the snow is floating, immediately remove it. Otherwise, melt it normally.
                        if (below.getType().isOccluding() || Tag.LEAVES.isTagged(below.getType())) {
                            above.setType(Material.SNOW);
                            Snow data = (Snow) above.getBlockData();
                            data.setLayers(7);
                            above.setBlockData(data);
                        } else above.setType(Material.AIR);
                    } else if (above.getType() == Material.SNOW) {
                        Snow data = (Snow) above.getBlockData();
                        int layers = data.getLayers() - 2;
                        if (layers <= 0) above.setType(Material.AIR);
                        else {
                            data.setLayers(layers);
                            above.setBlockData(data);
                        }
                    }
                }

            // Snow fals during blizzards and snowy days, in certain cold-warm biomes
            if (biome.climateSettings.temperature() < 0.95 && (currentWeather == Weather.SNOW || currentWeather == Weather.BLIZZARD)) {
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
        BLIZZARD,
        WINDY
    }
}
