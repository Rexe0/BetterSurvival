package me.rexe0.bettersurvival.worldgen;

import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.List;
import java.util.Random;

public class OceanCave extends BlockPopulator {

    private static final List<Biome> OCEAN_BIOMES = List.of(
            Biome.DEEP_OCEAN,
            Biome.OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN
    );

    private static final Material[] coralBlocks = {
            Material.BRAIN_CORAL_BLOCK,
            Material.BUBBLE_CORAL_BLOCK,
            Material.FIRE_CORAL_BLOCK,
            Material.HORN_CORAL_BLOCK,
            Material.TUBE_CORAL_BLOCK
    };
    private static final Material[] coralFans = {
            Material.BRAIN_CORAL_FAN,
            Material.BUBBLE_CORAL_FAN,
            Material.FIRE_CORAL_FAN,
            Material.HORN_CORAL_FAN,
            Material.TUBE_CORAL_FAN
    };
    private static final Material[] coralWallFans = {
            Material.BRAIN_CORAL_WALL_FAN,
            Material.BUBBLE_CORAL_WALL_FAN,
            Material.FIRE_CORAL_WALL_FAN,
            Material.HORN_CORAL_WALL_FAN,
            Material.TUBE_CORAL_WALL_FAN
    };
    private static final Material[] ores = {
            Material.COPPER_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.REDSTONE_ORE,
            Material.LAPIS_ORE,
    };

    private static final Material[] deepslateOres = {
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
    };

    private static final Material[] amethystBuds = {
            Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD,
            Material.LARGE_AMETHYST_BUD,
            Material.AMETHYST_CLUSTER
    };


    private SimplexOctaveGenerator generator;
    public SimplexOctaveGenerator getGenerator(long seed) {
        if (generator == null) {
            generator = new SimplexOctaveGenerator(new Random(seed), 3);
            generator.setScale(0.001);
        }
        return generator;
    }


    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        SimplexOctaveGenerator generator = getGenerator(worldInfo.getSeed());

        int dx;
        int dz;
        int floorY;
        Biome biome;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                dx = x + chunkX * 16;
                dz = z + chunkZ * 16;
                floorY = limitedRegion.getHighestBlockYAt(dx, dz, HeightMap.OCEAN_FLOOR_WG)-1;

                biome = limitedRegion.getBiome(dx, floorY, dz);

                if (!OCEAN_BIOMES.contains(biome)) continue;

                Material mat;
                for (int y = -30; y < (floorY > 35 ? floorY - 8 : floorY-5); y++) {
                    mat = limitedRegion.getType(dx, y, dz);
                    if (!mat.isSolid()) continue;
                    CaveType type = getCaveType(limitedRegion, dx, y, dz, generator);
                    if (type == CaveType.DEFAULT ||
                        type == CaveType.LUSH ||
                        type == CaveType.DRIPSTONE) continue;
                    changeType(limitedRegion, dx, y, dz, type, generator, random);
                }
            }
        }
    }

    public void changeType(LimitedRegion region, int x, int y, int z, CaveType type, SimplexOctaveGenerator generator, Random random) {
        Material material = region.getType(x, y, z);

        if (material == Material.PACKED_ICE || material == Material.BLUE_ICE) return;

        if (type == CaveType.CORAL) {
            generator.setScale(0.05);
            double noise = generator.noise(x, y, z, 2, 2, true);
            if (!material.name().contains("CORAL")) {
                material = noise < 0 ? Material.SANDSTONE : Material.SMOOTH_SANDSTONE;
                region.setType(x, y, z, material);
            }
        } else if (type == CaveType.GEODE) {
            generator.setScale(0.02);
            double noise = generator.noise(x, y, z, 2, 2, true);
            if (!material.name().contains("AMETHYST"))
                if (noise < 0) {
                    material = random.nextInt(25) == 0 ? Material.BUDDING_AMETHYST : Material.AMETHYST_BLOCK;
                    region.setType(x, y, z, material);
                }
        } else if (type == CaveType.SCULK) {

            if (!material.name().contains("SCULK")) {
                generator.setScale(0.02);
                double noise = generator.noise(x, y, z, 2, 2, true);
                if (noise < 0.15) material = Material.SCULK;
                else material = Material.DEEPSLATE;
                region.setType(x, y, z, material);

                if (noise >= 0.15 && noise < 0.25) {
                    Vector vec;
                    for (int i = 0; i < 6; i++) {
                        vec = switch (i) {
                            case 0 -> new Vector(1, 0, 0);
                            case 1 -> new Vector(-1, 0, 0);
                            case 2 -> new Vector(0, 1, 0);
                            case 3 -> new Vector(0, -1, 0);
                            case 4 -> new Vector(0, 0, 1);
                            default -> new Vector(0, 0, -1);
                        };
                        int dx = x + vec.getBlockX();
                        int dy = y + vec.getBlockY();
                        int dz = z + vec.getBlockZ();
                        Material mat = region.getType(dx, dy, dz);
                        if (mat != Material.WATER && mat != Material.SCULK_VEIN) continue;

                        SculkVein sculkVein;
                        if (mat == Material.SCULK_VEIN) sculkVein = (SculkVein) region.getBlockData(dx, dy, dz);
                        else sculkVein = (SculkVein) Material.SCULK_VEIN.createBlockData();

                        sculkVein.setWaterlogged(true);
                        sculkVein.setFace(getBlockFace(vec).getOppositeFace(), true);
                        region.setBlockData(dx, dy, dz, sculkVein);
                    }
                }
            }
        }

        switch (type) {
            case KELP -> {
                if (random.nextInt(6) != 0) return;
                if (region.getType(x, y+1, z) != Material.WATER) return;

                int i = 1;
                for (; i < random.nextInt(8, 16); i++) {
                    if (region.getType(x, y+i, z) != Material.WATER) break;
                    region.setType(x, y+i, z, Material.KELP_PLANT);
                }
                region.setType(x, y+i-1, z, Material.KELP);
            }
            case CORAL -> {
                if ((material == Material.SANDSTONE || material == Material.SMOOTH_SANDSTONE) && (region.getType(x, y+1, z) == Material.WATER || region.getType(x, y+1, z) == Material.SAND)) {
                    region.setType(x, y, z, Material.SAND);
                }

                if (random.nextInt(6) != 0) return;
                Material coralMat = coralBlocks[random.nextInt(coralBlocks.length)];
                Vector vec = new Vector(0, 1, 0);

                for (int i = 0; i < 7; i++) {
                    Vector offset = getRandomOffset(random);
                    vec.add(offset);
                    if (region.getType(x+vec.getBlockX(), y+vec.getBlockY(), z+vec.getBlockZ()).isSolid()
                            || region.getType(x+vec.getBlockX(), y+vec.getBlockY(), z+vec.getBlockZ()) == Material.AIR) {
                        vec.subtract(offset);
                        break;
                    }

                    region.setType(x+vec.getBlockX(), y+vec.getBlockY(), z+vec.getBlockZ(), coralMat);

                    for (int j = 0; j < 2; j++) {
                        Vector fanOffset = getRandomOffset(random);
                        if (fanOffset.getY() == -1) continue;

                        vec.add(fanOffset);

                        if (region.getType(x+vec.getBlockX(), y+vec.getBlockY(), z+vec.getBlockZ()) == Material.WATER) {
                            if (fanOffset.getY() == 1) {
                                Waterlogged waterlogged;
                                if (random.nextInt(3) == 0) {
                                    waterlogged = (Waterlogged) Material.SEA_PICKLE.createBlockData();
                                    ((SeaPickle)waterlogged).setPickles(random.nextInt(1, 5));
                                } else waterlogged = (Waterlogged) coralFans[random.nextInt(coralFans.length)].createBlockData();
                                waterlogged.setWaterlogged(true);
                                region.setBlockData(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ(), waterlogged);
                            } else {
                                CoralWallFan fan = (CoralWallFan) coralWallFans[random.nextInt(coralWallFans.length)].createBlockData();
                                fan.setWaterlogged(true);
                                fan.setFacing(getBlockFace(fanOffset));

                                region.setBlockData(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ(), fan);
                            }
                        }
                        vec.subtract(fanOffset);
                    }
                }
            }
            case GEODE -> {
                if (material == Material.GRANITE || material == Material.ANDESITE || material == Material.DIORITE || material == Material.GRAVEL  || material == Material.DIRT) {
                    material = random.nextInt(25) == 0 ? Material.BUDDING_AMETHYST : Material.AMETHYST_BLOCK;
                    region.setType(x, y, z, material);

                }

                if (random.nextInt(6) != 0) return;


                if ((material == Material.DEEPSLATE || material == Material.STONE) && random.nextBoolean()) {
                    region.setType(x, y, z, material == Material.DEEPSLATE ? deepslateOres[random.nextInt(deepslateOres.length)] : ores[random.nextInt(ores.length)]);
                    return;
                }
                if (material != Material.AMETHYST_BLOCK) return;

                Vector vec = new Vector(0, 0, 0);


                for (int i = 0; i < 7; i++) {
                    Vector offset = getRandomOffset(random);
                    vec.add(offset);
                    if (region.getType(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ()).isSolid()
                            || region.getType(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ()) == Material.AIR
                            || vec.lengthSquared() > 4*4) {
                        vec.subtract(offset);
                        continue;
                    }

                    region.setType(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ(), Material.AMETHYST_BLOCK);

                    for (int j = 0; j < 2; j++) {
                        Vector clusterOffset = getRandomOffset(random);

                        vec.add(clusterOffset);

                        if (region.getType(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ()) == Material.WATER) {
                            AmethystCluster cluster = (AmethystCluster) amethystBuds[random.nextInt(amethystBuds.length)].createBlockData();

                            cluster.setWaterlogged(true);
                            cluster.setFacing(getBlockFace(clusterOffset));

                            region.setBlockData(x + vec.getBlockX(), y + vec.getBlockY(), z + vec.getBlockZ(), cluster);
                        }
                        vec.subtract(clusterOffset);
                    }
                }
            }
            case SCULK -> {
                if (material != Material.SCULK || region.getType(x, y+1, z) != Material.WATER) return;
                if (random.nextInt(18) != 0) return;
                material = random.nextInt(10) == 0 ? Material.SCULK_SHRIEKER : Material.SCULK_SENSOR;
                if (material == Material.SCULK_SHRIEKER) {
                    SculkShrieker shrieker = (SculkShrieker) material.createBlockData();
                    shrieker.setWaterlogged(true);
                    shrieker.setCanSummon(true);
                    region.setBlockData(x, y+1, z, shrieker);
                    return;
                }
                Waterlogged waterlogged = (Waterlogged) material.createBlockData();
                waterlogged.setWaterlogged(true);
                region.setBlockData(x, y+1, z, waterlogged);
            }
        }
    }

    public Vector getRandomOffset(Random random) {
        return switch (random.nextInt(6)) {
            default -> new Vector(1, 0 ,0);
            case 1 -> new Vector(-1, 0 ,0);
            case 2 -> new Vector(0, 1 ,0);
            case 3 -> new Vector(0, -1 ,0);
            case 4 -> new Vector(0, 0 ,1);
            case 5 -> new Vector(0, 0 ,-1);
        };
    }
    public BlockFace getBlockFace(Vector offset) {
        if (offset.getX() == 1) return BlockFace.EAST;
        if (offset.getX() == -1) return BlockFace.WEST;
        if (offset.getZ() == 1) return BlockFace.SOUTH;
        if (offset.getZ() == -1) return BlockFace.NORTH;
        if (offset.getY() == 1) return BlockFace.UP;
        return BlockFace.DOWN;
    }

    public CaveType getCaveType(LimitedRegion region, int x, int y, int z, SimplexOctaveGenerator generator) {
        if (region.getBiome(x, y, z) == Biome.LUSH_CAVES) return CaveType.LUSH;
        if (region.getBiome(x, y, z) == Biome.DRIPSTONE_CAVES) return CaveType.DRIPSTONE;
        if (region.getType(x+1, y, z) != Material.WATER
                && region.getType(x-1, y, z) != Material.WATER
                && region.getType(x, y, z+1) != Material.WATER
                && region.getType(x, y, z-1) != Material.WATER
                && region.getType(x, y+1, z) != Material.WATER
                && region.getType(x, y-1, z) != Material.WATER) return CaveType.DEFAULT;

        generator.setScale(0.0005);
        double noise = generator.noise(x, y, z, 1, 0.5, true);
        boolean negative = noise < 0;
        noise = Math.ceil(Math.abs(noise*10))/10;
        if (negative) noise *= -1;

        return CaveType.SCULK;
//        if (noise < -0.5) return CaveType.CORAL;
//        // Default Cave buffer -0.5 to -0.4
//        if (noise > -0.4 && noise < -0.2) return CaveType.GEODE;
//        // Large Default Cave buffer -0.2 to 0.2
//        if (noise > 0.2 && noise < 0.4) return CaveType.KELP;
//        // Default Cave buffer 0.4 to 0.5
//        if (noise > 0.5) return CaveType.SCULK;
//        return CaveType.DEFAULT;
    }
    public Material getMaterial(double noise) {
        noise = (noise+1)/2;
        if (noise < 0.1) return Material.BLACK_CONCRETE;
        if (noise < 0.2) return Material.POLISHED_BLACKSTONE;
        if (noise < 0.3) return Material.SMOOTH_BASALT;
        if (noise < 0.4) return Material.POLISHED_TUFF;
        if (noise < 0.5) return Material.PALE_MOSS_BLOCK;
        if (noise < 0.6) return Material.LIGHT_GRAY_CONCRETE;
        if (noise < 0.7) return Material.POLISHED_ANDESITE;
        if (noise < 0.8) return Material.SMOOTH_STONE;
        if (noise < 0.9) return Material.POLISHED_DIORITE;
        return Material.WHITE_CONCRETE;
    }
}

