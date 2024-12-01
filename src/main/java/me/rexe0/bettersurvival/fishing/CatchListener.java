package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.fishing.Fish;
import me.rexe0.bettersurvival.item.fishing.FishCodex;
import me.rexe0.bettersurvival.item.fishing.TreasureChest;
import me.rexe0.bettersurvival.item.fishing.TreasureSand;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import net.minecraft.world.entity.projectile.FishingHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftFishHook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class CatchListener implements Listener {
    public static final Map<UUID, FishingMinigame> minigameMap = new HashMap<>();

    private static final ChatColor[] fishColors = new ChatColor[]{
            ChatColor.GREEN, ChatColor.BLUE, ChatColor.DARK_PURPLE, ChatColor.GOLD
    };

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.FISHING) return;
        Player player = e.getPlayer();
        ItemStack fishingRod = e.getHand() == EquipmentSlot.HAND
                ? player.getEquipment().getItemInMainHand() : player.getEquipment().getItemInOffHand();
        ItemType itemType = ItemDataUtil.getItemType(fishingRod);

        int min = 100;
        int max = 600;
        if (itemType != null) {
            min = switch (itemType) {
                default -> 100;
                case COPPER_FISHING_ROD -> 75;
                case PLATINUM_FISHING_ROD -> 50;
                case RESONANT_FISHING_ROD -> 25;
            };

            max = switch (itemType) {
                default -> 600;
                case COPPER_FISHING_ROD -> 550;
                case PLATINUM_FISHING_ROD -> 500;
                case RESONANT_FISHING_ROD -> 400;
            };

            // Check for bait in the player's inventory and reduce its amount by 1
            ItemType bait = null;
            for (ItemStack itemStack : player.getInventory().getContents()) {
                ItemType type = ItemDataUtil.getItemType(itemStack);
                if (type != null && type.isBait()) {
                    bait = type;
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    break;
                }
            }
            if (bait != null && itemType.canUseBait()) {
                float multiplier = bait == ItemType.PREMIUM_BAIT ? 0.25f : 0.5f;
                min *= multiplier;
                max *= multiplier;

                EntityDataUtil.setStringValue(e.getHook(), "baitType", bait.name());
            }

            // Check for tackle in the player's inventory
            if (itemType.canUseTackle())
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    ItemType type = ItemDataUtil.getItemType(itemStack);
                    if (type != null && type.isTackle()) {
                        EntityDataUtil.setStringValue(e.getHook(), "tackleType", type.name());
                        break;
                    }
                }
        }

        // If its raining, reduce fishing time by 10%
        if (SeasonListener.getCurrentWeather() == SeasonListener.Weather.RAIN
                || SeasonListener.getCurrentWeather() == SeasonListener.Weather.STORM
                || SeasonListener.getCurrentWeather() == SeasonListener.Weather.TEMPEST) {
            min *= 0.9;
            max *= 0.9;
        }

        if (fishingRod.containsEnchantment(Enchantment.LURE)) {
            min *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
            max *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
        }

        e.getHook().setMinWaitTime(Math.max(0, min));
        e.getHook().setMaxWaitTime(max);

        EntityDataUtil.setIntegerValue(e.getHook(), "luckLevel", fishingRod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA));

        FishingHook hook = ((CraftFishHook)e.getHook()).getHandle();
        hook.applyLure = false;
    }


    @EventHandler
    public void onCatch(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (minigameMap.containsKey(e.getPlayer().getUniqueId())) return;
        if (!(e.getCaught() instanceof Item item)) return;
        Player player = e.getPlayer();
        FishHook hook = e.getHook();

        ItemType bait;
        try {
            bait = ItemType.valueOf(EntityDataUtil.getStringValue(hook, "baitType"));
        } catch (IllegalArgumentException ex) {
            bait = null;
        }

        ItemType tackle;
        try {
            tackle = ItemType.valueOf(EntityDataUtil.getStringValue(hook, "tackleType"));
        } catch (IllegalArgumentException ex) {
            tackle = null;
        }

        List<BiomeGroup> biomes = new ArrayList<>();

        if (hook.getLocation().getY() < 20) biomes.add(BiomeGroup.CAVERNS);
        else {
            for (BiomeGroup group : BiomeGroup.values()) {
                for (Biome bio : group.getBiomes())
                    if (bio == hook.getLocation().getBlock().getBiome())
                        biomes.add(group);
            }
            if (biomes.size() == 0) biomes.add(BiomeGroup.FOREST);
        }

        // Treasure
        double treasureChance = bait == ItemType.MAGNET ? 0.2 : 0.1;
        treasureChance += EntityDataUtil.getIntegerValue(hook, "luckLevel")*0.033;
        if (tackle == ItemType.VIBRANT_BOBBER) treasureChance *= 0.5;
        if (tackle == ItemType.GOLD_BOBBER) treasureChance += 0.05;

        ItemStack treasureItem = getTreasure();
        boolean caughtTreasure = Math.random() < treasureChance;

        // Fish
        Fish.FishType fish = getCatch(biomes, bait, tackle);

        FishingMinigame.Difficulty difficulty = null;
        if (fish.getName().startsWith(ChatColor.BLUE+"")) difficulty = FishingMinigame.Difficulty.EASY;
        else if (fish.getName().startsWith(ChatColor.DARK_PURPLE+"")) difficulty = FishingMinigame.Difficulty.MEDIUM;
        else if (fish.getName().startsWith(ChatColor.GOLD+"")) difficulty = FishingMinigame.Difficulty.HARD;

        if (difficulty != null) {
            List<ItemStack> drops = new ArrayList<>();
            drops.add(new Fish(fish).getItem());
            if (caughtTreasure)
                drops.add(treasureItem);

            FishingMinigame minigame = new FishingMinigame(player, hook, fish, drops, difficulty, caughtTreasure);
            minigame.setTackle(tackle);
            minigame.getRunnable().runTaskTimer(BetterSurvival.getInstance(), 0, 1);
            minigameMap.put(player.getUniqueId(), minigame);

            e.setCancelled(true);
            return;
        }
        item.setItemStack(new Fish(fish).getItem());
        ((FishCodex)ItemType.FISH_CODEX.getItem()).onCatch(player, fish);
        applyGlow(item);

        if (caughtTreasure)
            // Run it a tick later so that the item spawned has the same velocity as the caught fish
            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                Item treasure = player.getWorld().dropItem(item.getLocation(), treasureItem);
                treasure.setVelocity(item.getVelocity());
                treasure.setOwner(player.getUniqueId());
                treasure.setPickupDelay(0);

                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                player.sendMessage(ChatColor.GREEN+"You managed to pull up some additional treasure.");
            }, 1);
    }

    @EventHandler
    public void onPlaceTreasureChest(BlockPlaceEvent e) {
        ((TreasureChest)ItemType.TREASURE_CHEST.getItem()).onBlockPlace(e);
        ((TreasureSand)ItemType.TREASURE_SAND.getItem()).onBlockPlace(e);
    }

    @EventHandler
    public void onReel(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH && e.getState() != PlayerFishEvent.State.REEL_IN) return;
        if (!minigameMap.containsKey(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        minigameMap.get(e.getPlayer().getUniqueId()).onReel();
    }


    private ItemStack getTreasure() {
        // Treasure Chest
        if (Math.random() < 0.2)
            return ItemType.TREASURE_CHEST.getItem().getItem();
        return ItemType.TREASURE_SAND.getItem().getItem();
    }

    private Fish.FishType getCatch(List<BiomeGroup> biomes, ItemType bait, ItemType tackle) {
        Fish.FishType[] possibleFish = Arrays.stream(Fish.FishType.values())
                .filter(f -> biomes.contains(f.getBiome())
                        && Arrays.stream(f.getSeason()).toList().contains(Season.getSeason())
                        && f.getTime().isValid(BetterSurvival.getInstance().getDefaultWorld().getTime()))
                .toArray(Fish.FishType[]::new);

        int totalWeight = 0;
        for (Fish.FishType type : possibleFish) {
            int amount = type.getWeight();
            if (type.getWeight() <= 30) {
                if (tackle == ItemType.VIBRANT_BOBBER) amount *= 1.5;
                if (tackle == ItemType.GOLD_BOBBER) amount *= 0.5;
            }
            totalWeight += amount;
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < possibleFish.length - 1; ++idx) {
            int amount = possibleFish[idx].getWeight();
            if (possibleFish[idx].getWeight() <= 30) {
                if (tackle == ItemType.VIBRANT_BOBBER) amount *= 1.5;
                if (tackle == ItemType.GOLD_BOBBER) amount *= 0.5;
            }
            r -= amount;
            if (r <= 0.0) break;
        }
        return possibleFish[idx];
    }


    public static void applyGlow(Item item) {
        if (ItemDataUtil.getStringValue(item.getItemStack(), "fishType").equals("")) return;
        createTeams();

        // Add rarity glow to the caught fish
        for (ChatColor color : fishColors) {
            if (!item.getItemStack().getItemMeta().getDisplayName().startsWith(color+"")) continue;
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(color.name()).addEntry(item.getUniqueId()+"");
            item.setGlowing(true);
            break;
        }
    }

    private static void createTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        for (ChatColor color : fishColors) {
            if (board.getTeam(color.name()) == null)
                board.registerNewTeam(color.name());

            Team team = board.getTeam(color.name());
            team.setColor(color);
        }
    }
}
