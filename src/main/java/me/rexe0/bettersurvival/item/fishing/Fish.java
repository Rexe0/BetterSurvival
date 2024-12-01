package me.rexe0.bettersurvival.item.fishing;

import me.rexe0.bettersurvival.fishing.BiomeGroup;
import me.rexe0.bettersurvival.item.Item;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.Time;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Fish extends Item {
    private final double weight;
    private final FishType type;
    public Fish(FishType type) {
        super(type.getMaterial(), type.getName(), type.getID());
        this.weight = RandomUtil.getRandom().nextDouble(type.getMinimumWeight(), type.getMaximumWeight());
        this.type = type;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setItemMeta(ItemDataUtil.setDoubleValue(item, "weight", weight));
        item.setItemMeta(ItemDataUtil.setStringValue(item, "fishType", type.getID()));
        return item;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Weight: "+ChatColor.GREEN+Math.round(weight*100)/100f+" lbs");
        return lore;
    }

    public enum FishType {
        SARDINE(Material.COD,ChatColor.GREEN+"Sardine", BiomeGroup.OCEAN, new Season[]{Season.AUTUMN, Season.SUMMER, Season.SPRING}, Time.ANY, 1, 4, 50),
        ANCHOVY(Material.COD,ChatColor.GREEN+"Anchovy", BiomeGroup.OCEAN, new Season[]{Season.SUMMER, Season.AUTUMN, Season.WINTER}, Time.ANY, 1, 5, 50),
        TUNA(Material.COD,ChatColor.BLUE+"Tuna", BiomeGroup.OCEAN, new Season[]{Season.SPRING, Season.SUMMER}, Time.DAY, 10, 18, 30),
        TROUT(Material.SALMON,ChatColor.BLUE+"Trout", BiomeGroup.OCEAN, new Season[]{Season.SPRING, Season.SUMMER, Season.AUTUMN}, Time.NIGHT, 8, 20, 30),
        RED_MULLET(Material.SALMON,ChatColor.BLUE+"Red Mullet", BiomeGroup.OCEAN, new Season[]{Season.AUTUMN, Season.WINTER}, Time.NIGHT, 10, 20, 30),
        SHADEHEAD_TROUT(Material.SALMON,ChatColor.DARK_PURPLE+"Shadehead Trout", BiomeGroup.OCEAN, new Season[]{Season.WINTER}, Time.DUSK, 16, 30, 10),

        CLOWNFISH(Material.TROPICAL_FISH,ChatColor.GREEN+"Clownfish", BiomeGroup.WARM_OCEAN, new Season[]{Season.SUMMER, Season.SPRING}, Time.ANY, 2, 7, 50),
        SUNFISH(Material.COD,ChatColor.GREEN+"Sunfish", BiomeGroup.WARM_OCEAN, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN}, Time.ANY, 3, 10, 50),
        POMFRET(Material.COD,ChatColor.GREEN+"Pomfret", BiomeGroup.WARM_OCEAN, new Season[]{Season.WINTER, Season.AUTUMN}, Time.ANY, 5, 8, 50),
        SEA_CUCUMBER(Material.KELP,ChatColor.BLUE+"Sea Cucumber", BiomeGroup.WARM_OCEAN, new Season[]{Season.WINTER, Season.SPRING, Season.AUTUMN}, Time.DAY, 6, 15, 30),
        PUFFERFISH(Material.PUFFERFISH,ChatColor.BLUE+"Pufferfish", BiomeGroup.WARM_OCEAN, new Season[]{Season.WINTER, Season.SPRING, Season.AUTUMN}, Time.NIGHT, 6, 15, 30),
        JELLYFISH(Material.COD,ChatColor.DARK_PURPLE+"Jellyfish", BiomeGroup.WARM_OCEAN, new Season[]{Season.WINTER, Season.AUTUMN}, Time.NIGHT, 10, 25, 10),

        HALIBUT(Material.COD,ChatColor.GREEN+"Halibut", BiomeGroup.FROZEN_OCEAN, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 5, 10, 50),
        SQUID(Material.COD,ChatColor.GREEN+"Squid", BiomeGroup.FROZEN_OCEAN, new Season[]{Season.SUMMER, Season.SPRING}, Time.ANY, 6, 12, 50),
        EEL(Material.KELP,ChatColor.BLUE+"Eel", BiomeGroup.FROZEN_OCEAN, new Season[]{Season.WINTER, Season.AUTUMN, Season.SPRING}, Time.NIGHT, 14, 21, 30),
        OCTOPUS(Material.COD,ChatColor.BLUE+"Octopus", BiomeGroup.FROZEN_OCEAN, new Season[]{Season.WINTER, Season.AUTUMN}, Time.NIGHT, 11, 24, 30),
        TWILIGHT_CARP(Material.COD,ChatColor.DARK_PURPLE+"Twilight Carp", BiomeGroup.FROZEN_OCEAN, new Season[]{Season.SPRING, Season.AUTUMN}, Time.DUSK, 19, 35, 10),

        SALMON(Material.SALMON,ChatColor.GREEN+"Salmon", BiomeGroup.RIVER, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN}, Time.ANY, 3, 7, 50),
        BREAM(Material.COD,ChatColor.GREEN+"Bream", BiomeGroup.RIVER, new Season[]{Season.SUMMER, Season.WINTER, Season.AUTUMN}, Time.ANY, 4, 6, 50),
        PIKE(Material.COD,ChatColor.GREEN+"Pike", BiomeGroup.RIVER, new Season[]{Season.AUTUMN, Season.WINTER, Season.SPRING}, Time.ANY, 2, 8, 50),
        SHAD(Material.COD,ChatColor.BLUE+"Shad", BiomeGroup.RIVER, new Season[]{Season.SUMMER, Season.WINTER, Season.SPRING}, Time.NIGHT, 5, 12, 30),
        RAINBOW_TROUT(Material.SALMON,ChatColor.DARK_PURPLE+"Rainbow Trout", BiomeGroup.RIVER, new Season[]{Season.SUMMER, Season.WINTER, Season.SPRING}, Time.DUSK, 10, 17, 30),

        NEON_TETRA(Material.COD,ChatColor.GREEN+"Neon Tetra", BiomeGroup.JUNGLE, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 2, 5, 50),
        COCOA_FISH(Material.COD,ChatColor.GREEN+"Cocoa Fish", BiomeGroup.JUNGLE, new Season[]{Season.SUMMER, Season.SPRING}, Time.DAY, 4, 7, 50),
        CAT_FISH(Material.COD,ChatColor.BLUE+"Cat Fish", BiomeGroup.JUNGLE, new Season[]{Season.WINTER, Season.AUTUMN}, Time.ANY, 7, 14, 30),
        ANGEL_FISH(Material.COD,ChatColor.BLUE+"Angel Fish", BiomeGroup.JUNGLE, new Season[]{Season.SPRING, Season.AUTUMN}, Time.DAY, 10, 12, 30),
        MURKFISH(Material.SALMON,ChatColor.DARK_PURPLE+"Murkfish", BiomeGroup.JUNGLE, new Season[]{Season.SUMMER}, Time.DAWN, 15, 25, 10),

        BASS(Material.COD,ChatColor.GREEN+"Bass", BiomeGroup.FOREST, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 5, 8, 50),
        CHUB(Material.COD,ChatColor.GREEN+"Chub", BiomeGroup.FOREST, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN}, Time.ANY, 7, 9, 50),
        CARP(Material.COD,ChatColor.BLUE+"Carp", BiomeGroup.FOREST, new Season[]{Season.SUMMER, Season.SPRING}, Time.DAY, 13, 19, 30),
        BLUE_DISCUS(Material.COD,ChatColor.BLUE+"Blue Discus", BiomeGroup.FOREST, new Season[]{Season.AUTUMN, Season.WINTER}, Time.NIGHT, 16, 17, 30),
        DAYBREAK(Material.TROPICAL_FISH,ChatColor.DARK_PURPLE+"Daybreak", BiomeGroup.FOREST, new Season[]{Season.SUMMER}, Time.DAWN, 22, 25, 10),

        STURGEON(Material.COD,ChatColor.GREEN+"Sturgeon", BiomeGroup.CAVERNS, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 2, 8, 50),
        STONE_FISH(Material.SALMON,ChatColor.GREEN+"Stone Fish", BiomeGroup.CAVERNS, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 6, 7, 50),
        OBSIDIFISH(Material.SALMON,ChatColor.BLUE+"Obsidifish", BiomeGroup.CAVERNS, new Season[]{Season.SUMMER, Season.SPRING, Season.AUTUMN, Season.WINTER}, Time.ANY, 5, 14, 30),
        DIAMOND_COD(Material.COD,ChatColor.DARK_PURPLE+"Diamond Cod", BiomeGroup.CAVERNS, new Season[]{Season.SUMMER, Season.AUTUMN, Season.WINTER}, Time.ANY, 13, 20, 10),


        GOLDEN_CARP(Material.COD, ChatColor.GOLD+"Golden Carp", BiomeGroup.ANY_OCEAN, new Season[]{Season.SPRING, Season.AUTUMN}, Time.DAWN, 37, 44, 1),
        ILLUMINATED_GLOOMFISH(Material.COD, ChatColor.GOLD+"Illuminated Gloomfish", BiomeGroup.ANY_OCEAN, new Season[]{Season.WINTER}, Time.WINTER_SOLSTICE_NIGHT, 40, 50, 2);

        private final Material material;
        private final String name;
        private final BiomeGroup biome;
        private final Season[] season;
        private final Time time;
        private final int minimumWeight;
        private final int maximumWeight;

        // Weight is the chance that the fish will be caught
        private final int weight;

        FishType(Material material, String name, BiomeGroup biome, Season[] season, Time time, int minimumWeight, int maximumWeight, int weight) {
            this.material = material;
            this.name = name;
            this.biome = biome;
            this.season = season;
            this.time = time;
            this.minimumWeight = minimumWeight;
            this.maximumWeight = maximumWeight;
            this.weight = weight;
        }

        public Material getMaterial() {
            return material;
        }

        public int getMinimumWeight() {
            return minimumWeight;
        }

        public int getMaximumWeight() {
            return maximumWeight;
        }

        public BiomeGroup getBiome() {
            return biome;
        }

        public Season[] getSeason() {
            return season;
        }

        public Time getTime() {
            return time;
        }

        public String getName() {
            return name;
        }

        public String getID() {
            return name();
        }


        public int getWeight() {
            return weight;
        }
    }
}
