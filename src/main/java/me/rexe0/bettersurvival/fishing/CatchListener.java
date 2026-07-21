package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.fishing.*;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.Season;
import me.rexe0.bettersurvival.weather.SeasonListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFishHook;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class CatchListener implements Listener {
    public static final Map<UUID, FishingMinigame> minigameMap = new HashMap<>();

    private static final ChatColor[] fishColors = new ChatColor[]{
            ChatColor.GREEN, ChatColor.BLUE, ChatColor.DARK_PURPLE, ChatColor.GOLD
    };
    @EventHandler
    public void onLure(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.LURED) return;
        // Prevents another fish biting the hook when in minigame
        if (minigameMap.containsKey(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.FISHING) return;
        Player player = e.getPlayer();
        ItemStack fishingRod = e.getHand() == EquipmentSlot.HAND
                ? player.getEquipment().getItemInMainHand() : player.getEquipment().getItemInOffHand();
        ItemType rodType = ItemDataUtil.getItemType(fishingRod);

        FishingHook hook = ((CraftFishHook)e.getHook()).getHandle();
        boolean isLavaFishing = false;
        if (rodType != null && rodType.canFishInLava()) {
            hook.remove(Entity.RemovalReason.DISCARDED);
            hook = new LavaHook(((CraftPlayer)player).getHandle(), hook.level(), 0, 0);

            Projectile.spawnProjectile(hook, ((CraftWorld)player.getWorld()).getHandle(), CraftItemStack.asNMSCopy(fishingRod));
            ((CraftPlayer)player).getHandle().fishing = hook;
            hook.addTag("lavaHook");
            isLavaFishing = true;
        }
        boolean isNether = isLavaFishing && player.getWorld().getEnvironment() == World.Environment.NETHER;
        FishHook bukkitHook = (FishHook) hook.getBukkitEntity();


        int min = 100;
        int max = 600;
        if (rodType != null) {
            min = switch (rodType) {
                case OBSIDIAN_FISHING_ROD -> 120;
                case TUNGSTEN_FISHING_ROD -> 110;
                case NETHERITE_FISHING_ROD -> 90;
                case COPPER_FISHING_ROD -> 75;
                case PLATINUM_FISHING_ROD -> 50;
                case RESONANT_FISHING_ROD -> 25;
                default -> 100;
            };

            max = switch (rodType) {
                case COPPER_FISHING_ROD,TUNGSTEN_FISHING_ROD -> 550;
                case PLATINUM_FISHING_ROD,NETHERITE_FISHING_ROD -> 500;
                case RESONANT_FISHING_ROD -> 400;
                default -> 600;
            };

            ItemType tackle = null;
            ItemType bait = null;

            // Check for tackle in the player's inventory
            if (rodType.canUseTackle())
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    ItemType type = ItemDataUtil.getItemType(itemStack);
                    if (type != null && type.isTackle()) {
                        tackle = type;
                        if (type.isBait()) // If it's a lure, set the bait as the tackle as well
                            bait = type;
                        break;
                    }
                }

            // Check for bait in the player's inventory and reduce its amount by 1
            if (bait == null) {
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    ItemType type = ItemDataUtil.getItemType(itemStack);
                    if (type != null && type.isBait()) {
                        if (type.isTackle()) continue; // Ignore lures because their logic is calculated beforehand
                        bait = type;
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        break;
                    }
                }
            }

            if (bait != null && rodType.canUseBait()) {
                double multiplier = bait.getBaitMultiplier();
                min *= multiplier;
                max *= multiplier;

                EntityDataUtil.setStringValue(bukkitHook, "baitType", bait.name());
            }

            if (tackle != null) {
                if (tackle == ItemType.LEAD_SINKER) {
                    min += 150;
                    max += 150;
                }

                EntityDataUtil.setStringValue(bukkitHook, "tackleType", tackle.name());
            }



            String actionBar = ChatColor.BLUE+" | ";
            if (bait != null) actionBar = bait.getItem().getName() + actionBar;
            if (tackle != null) actionBar = actionBar + tackle.getItem().getName();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
        }

        // If its raining, reduce fishing time by 10%
        SeasonListener.Weather weather = SeasonListener.getCurrentWeather();
        if (weather == SeasonListener.Weather.RAIN
                || weather == SeasonListener.Weather.STORM
                || weather == SeasonListener.Weather.TEMPEST || (isNether && (weather == SeasonListener.Weather.SNOW || weather == SeasonListener.Weather.BLIZZARD))) {
            min *= 0.9;
            max *= 0.9;
        }

        if (fishingRod.containsEnchantment(Enchantment.LURE)) {
            min *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
            max *= 1 - (fishingRod.getEnchantmentLevel(Enchantment.LURE) * 0.1);
        }

        bukkitHook.setMinWaitTime(Math.max(0, min));
        bukkitHook.setMaxWaitTime(max);

        EntityDataUtil.setIntegerValue(bukkitHook, "luckLevel", fishingRod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA));

        hook.applyLure = false;
        hook.rainInfluenced = false;
    }


    @EventHandler
    public void onCatch(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (minigameMap.containsKey(e.getPlayer().getUniqueId())) return;
        if (!(e.getCaught() instanceof Item item)) return;
        Player player = e.getPlayer();
        FishHook hook = e.getHook();

        boolean isLavaFishing = hook.getScoreboardTags().contains("lavaHook");
        boolean isNether = player.getWorld().getEnvironment() == World.Environment.NETHER && isLavaFishing;

        ItemStack fishingRod = e.getHand() == EquipmentSlot.HAND
                ? player.getEquipment().getItemInMainHand() : player.getEquipment().getItemInOffHand();
        ItemType rodType = ItemDataUtil.getItemType(fishingRod);

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

        if (hook.getLocation().getY() < 20 && !isNether) biomes.add(BiomeGroup.CAVERNS);
        else {
            for (BiomeGroup group : BiomeGroup.values()) {
                for (Biome bio : group.getBiomes())
                    if (bio == hook.getLocation().getBlock().getBiome())
                        biomes.add(group);
            }
            if (biomes.isEmpty()) biomes.add(isLavaFishing ? BiomeGroup.LAVA : BiomeGroup.FOREST);
        }

        // Treasure
        double treasureChance = bait == ItemType.MAGNET ? 0.2 : 0.1;
        treasureChance += EntityDataUtil.getIntegerValue(hook, "luckLevel")*0.033;
        if (tackle == ItemType.VIBRANT_BOBBER) treasureChance *= 0.5;
        if (tackle == ItemType.GOLD_BOBBER) treasureChance += 0.05;

        ItemStack treasureItem = getTreasure(rodType, isNether);
        boolean caughtTreasure = Math.random() < treasureChance;

        int fishingLevel = EntityDataUtil.getIntegerValue(player, "upgradeLevel.FISHING");
        boolean isDoubleTreasure = Math.random() < fishingLevel*0.08;

        // Fish
        Fish.FishType fishType = getCatch(biomes, bait, tackle);

        FishingMinigame.Difficulty difficulty = null;
        if (fishType.getName().startsWith(ChatColor.BLUE+"")) difficulty = FishingMinigame.Difficulty.EASY;
        else if (fishType.getName().startsWith(ChatColor.DARK_PURPLE+"")) difficulty = FishingMinigame.Difficulty.MEDIUM;
        else if (fishType.getName().startsWith(ChatColor.GOLD+"")) difficulty = FishingMinigame.Difficulty.HARD;

        Fish fish = new Fish(fishType);

        if (tackle == ItemType.LEAD_SINKER || tackle == ItemType.STEEL_SINKER) {
            double amount = 3;

            if (tackle == ItemType.STEEL_SINKER) {
                Material fluid = isLavaFishing ? Material.LAVA : Material.WATER;
                int depth = 0;
                Location loc = hook.getLocation();
                for (int y = loc.getBlockY()-1; y > -64; y--) {
                    if (hook.getWorld().getType(loc.getBlockX(), y, loc.getBlockZ()) != fluid) {
                        if (hook.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getBlockData() instanceof Waterlogged waterlogged)
                            if (!waterlogged.isWaterlogged()) break;
                        else break;
                    }
                    depth++;
                }
                amount = Math.min(6, depth/7d); // Max increase of 6 lbs at 42 blocks depth
                if (amount > fishType.getMaximumWeight()) amount = fishType.getMaximumWeight(); // Cap fish weight increase at double
            }
            fish.addWeight(amount);
        }

        if (difficulty != null) {
            List<ItemStack> drops = new ArrayList<>();
            drops.add(fish.getItem());
            if (caughtTreasure) {
                for (int i = 0; i < (isDoubleTreasure ? 2 : 1); i++) {
                    drops.add(treasureItem.clone());
                }
            }
            FishingMinigame minigame = new FishingMinigame(player, hook, fishType, drops, difficulty, caughtTreasure, isNether);
            minigame.setTackle(tackle);
            minigame.getRunnable().runTaskTimer(BetterSurvival.getInstance(), 0, 1);
            minigameMap.put(player.getUniqueId(), minigame);

            e.setCancelled(true);
            return;
        }
        item.setItemStack(fish.getItem());
        item.setHealth(50);
        ((FishCodex)ItemType.FISH_CODEX.getItem()).onCatch(player, fishType);
        applyGlow(item);

        if (caughtTreasure)
            // Run it a tick later so that the item spawned has the same velocity as the caught fish
            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                for (int i = 0; i < (isDoubleTreasure ? 2 : 1); i++) {
                    Item treasure = player.getWorld().dropItem(item.getLocation(), treasureItem);
                    treasure.setVelocity(item.getVelocity());
                    treasure.setOwner(player.getUniqueId());
                    treasure.setPickupDelay(0);
                    treasure.setHealth(50);
                }

                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                String message = ChatColor.GREEN+"You managed to pull up some additional treasure.";
                if (isDoubleTreasure)
                    message = ChatColor.GOLD+""+ChatColor.MAGIC+"I "+message+ChatColor.GOLD+ChatColor.MAGIC+" I";
                player.sendMessage(message);
            }, 1);


        // Nether Mob Spawning - Chance to spawn hostile mob when fishing in nether
        if (isNether) {
            double chance = 0.2;
            if (tackle == ItemType.BATTLE_BOBBER) chance = 0.5;
            else if (tackle == ItemType.CALMING_BOBBER) chance = 0.1;

            if (Math.random() < chance) {
                e.setExpToDrop(e.getExpToDrop()+15);
                // Run it a tick later so that the mobs spawned has the same velocity as the caught fish
                Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                    spawnNetherMobs(item.getLocation(), rodType, item.getVelocity().clone().multiply(2f));

                    player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 0.3f, 1.1f);
                    player.playSound(player.getLocation(), Sound.BLOCK_VAULT_ACTIVATE, 0.3f, 1f);
                    String message = ChatColor.RED+"Uh oh. You accidentally pulled up some hostile mobs!";
                    player.sendMessage(message);
                }, 1);
            }
        }
    }

    @EventHandler
    public void onReel(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH && e.getState() != PlayerFishEvent.State.REEL_IN) return;
        if (!minigameMap.containsKey(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        minigameMap.get(e.getPlayer().getUniqueId()).onReel();
    }

    private void spawnNetherMobs(Location location, ItemType rodType, Vector velocity) {
        NetherMob mob = NetherMob.getNetherMob(rodType);
        int amount = RandomUtil.getRandom().nextInt(mob.getMinAmount(), mob.getMaxAmount()+1);

        World world = location.getWorld();
        List<LivingEntity> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            LivingEntity entity = (LivingEntity) world.spawnEntity(location, mob.getType());
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, true, true));
            entity.setVelocity(mob.isReelsUp() ? new Vector(0, 1f, 0) : velocity);
            entities.add(entity);
        }
    }

    private ItemStack getTreasure(ItemType fishingRod, boolean isNether) {
        // Treasure Chest
        if (Math.random() < 0.2)
            return isNether ? new TreasureBundle(fishingRod).getItem() : new TreasureChest(fishingRod).getItem();
        return isNether ? new TreasurePot(fishingRod).getItem() : new TreasureSand(fishingRod).getItem();
    }

    private Fish.FishType getCatch(List<BiomeGroup> biomes, ItemType bait, ItemType tackle) {
        Fish.FishType[] possibleFish = Arrays.stream(Fish.FishType.values())
                .filter(f -> biomes.contains(f.getBiome())
                        && Arrays.stream(f.getSeason()).toList().contains(Season.getSeason())
                        && f.getTime().isValid(BetterSurvival.getInstance().getDefaultWorld().getTime()))
                .toArray(Fish.FishType[]::new);

        int totalWeight = 0;
        for (Fish.FishType type : possibleFish) {
            int amount = getWeight(tackle, type);
            totalWeight += amount;
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < possibleFish.length - 1; ++idx) {
            int amount = getWeight(tackle, possibleFish[idx]);
            r -= amount;
            if (r <= 0.0) break;
        }
        return possibleFish[idx];
    }

    private static int getWeight(ItemType tackle, Fish.FishType type) {
        int amount = type.getWeight();
        if (tackle == ItemType.VIBRANT_BOBBER) {
            if (amount <= 5) {
                amount *= 5;
            } else if (amount <= 15)
                amount = 25;
        }
        if (amount <= 30 && tackle == ItemType.GOLD_BOBBER) amount = (int) (amount * 0.5f);
        return amount;
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
