package me.rexe0.bettersurvival.worldgen;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.worldgen.structures.StructureOrder;
import me.rexe0.bettersurvival.worldgen.structures.StructureOrderManager;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HydrothermalVent extends BlockPopulator {
    private static final List<Biome> DEEP_OCEAN_BIOMES = List.of(
            Biome.DEEP_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN
    );

    private List<Structure> structures;
    private List<Structure> warmStructures;
    private StructureOrderManager orderManager;

    private SimplexOctaveGenerator generator;
    public SimplexOctaveGenerator getGenerator(long seed) {
        if (generator == null) {
            generator = new SimplexOctaveGenerator(new Random(seed), 2);
            generator.setScale(0.02);
        }
        return generator;
    }

    public Structure getStructure(boolean isWarm) {
        if (isWarm)
            return warmStructures.get(new Random().nextInt(warmStructures.size()));
        return structures.get(new Random().nextInt(structures.size()));
    }

    public HydrothermalVent() {
        StructureManager manager = Bukkit.getStructureManager();
        structures = new ArrayList<>();
        warmStructures = new ArrayList<>();

        File structureFolder = new File(BetterSurvival.getInstance().getDataFolder(), "structures");
        try {
            for (int i = 0; i < 3; i++)
                structures.add(manager.loadStructure(new File(structureFolder, "deepseavent" + i + ".nbt")));

            for (int i = 0; i < 6; i++)
                warmStructures.add(manager.loadStructure(new File(structureFolder, "deepseaventwarm" + i + ".nbt")));
        } catch (IOException ex) {
            Bukkit.getLogger().warning("Failed to load 'deepseavent' structure. Are they present in the structures folder for BetterSurvival?");
        }
        this.orderManager = StructureOrderManager.getInstance();
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        SimplexOctaveGenerator generator = getGenerator(worldInfo.getSeed());

        if (structures.isEmpty() || warmStructures.isEmpty()) return;

        int dx;
        int dz;
        int y;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                dx = x + chunkX * 16;
                dz = z + chunkZ * 16;

                y = limitedRegion.getHighestBlockYAt(dx, dz, HeightMap.OCEAN_FLOOR_WG)-1;

                if (y > 35) continue;

                Biome biome = limitedRegion.getBiome(dx, y, dz);

                if (!DEEP_OCEAN_BIOMES.contains(biome)) continue;

                double noise = generator.noise(dx, dz, 1, 64, true);
                if (Math.abs(noise) > 0.15) continue;

                boolean isWarm = biome == Biome.DEEP_LUKEWARM_OCEAN;

                // Creating fault lines
                if (limitedRegion.getType(dx, y, dz) == Material.STONE || limitedRegion.getType(dx, y, dz) == (isWarm ? Material.SAND : Material.GRAVEL)) {
                    double noise1 = generator.noise(dx, dz, 1, 4, true);
                    Material mat;
                    if (noise1 < -0.2)
                        mat = isWarm ? Material.BIRCH_PLANKS : Material.DEEPSLATE;
                    else
                        mat = isWarm ? Material.SANDSTONE : Material.COBBLED_DEEPSLATE;

                    limitedRegion.setType(dx, y, dz, mat);
                }

                if (Math.abs(noise) > 0.01) continue;
                Structure structure = getStructure(isWarm);

                StructureOrder order = new StructureOrder(structure, new Location(Bukkit.getWorld(worldInfo.getName()), dx, y, dz),
                        false, StructureRotation.NONE, Mirror.values()[random.nextInt(Mirror.values().length)], 0, 1);

                // Add treasure to suspicious sand
                order.addBlockTransformer((region, i, i1, i2, blockState, transformationState) -> {
                    if (!(blockState instanceof BrushableBlock block)) return blockState;
                    block.setItem(getSuspiciousItem(random));
                    return blockState;
                });
                order.setGrounded(true);

                orderManager.queueOrder(order);
            }
        }
    }

    private ItemStack getSuspiciousItem(Random random) {
        return switch(random.nextInt(6)) {
            case 0 -> new ItemStack(Material.DIAMOND, 1);
            case 1 -> new ItemStack(Material.RAW_GOLD, 2);
            case 2 -> new ItemStack(Material.RAW_IRON, 3);
            case 3 -> new ItemStack(Material.NAUTILUS_SHELL, 1);
            default -> new ItemStack(Material.COAL, 3);
        };
    }
}

