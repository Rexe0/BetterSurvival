package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.fishing.Fish;
import me.rexe0.bettersurvival.item.fishing.FishCodex;
import me.rexe0.bettersurvival.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FishingMinigame {
    private final Player player;
    private final FishHook hook;
    private final Fish.FishType fishType;
    private final List<ItemStack> items;
    private final Difficulty difficulty;
    private final boolean hasTreasure;
    private final boolean isNether;
    private boolean isFirstCatch;
    private boolean isFirstNetherCatch;

    private ItemType tackle;

    private boolean isFinished;
    private int i;
    private double progress;
    private double location;
    private double fishLocation;

    private double locationVelocity;
    private int burnCooldown;
    private double fishStrength;

    public FishingMinigame(Player player, FishHook hook, Fish.FishType fishType, List<ItemStack> items, Difficulty difficulty, boolean hasTreasure, boolean isNether) {
        this.player = player;
        this.hook = hook;
        this.fishType = fishType;
        this.items = items;
        this.difficulty = difficulty;
        this.isNether = isNether;
        this.progress = 0.125;
        this.location = 0.5;
        this.fishLocation = RandomUtil.getRandom().nextDouble(0.3, 0.7);
        this.hasTreasure = hasTreasure;
        this.isFirstCatch = !FishFile.getPlayerData(player).hasCaughtRareFish();
        this.isFirstNetherCatch = !FishFile.getPlayerData(player).hasCaughtRareNetherFish();
        this.fishStrength = 1;
    }

    public void setTackle(ItemType tackle) {
        this.tackle = tackle;
    }

    private void run() {
        if (i % 10 == 0)
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);

        if (player == null || player.isDead() || hook == null || hook.isDead() || progress <= 0) {
            isFinished = true;
            return;
        }

        // UI display
        boolean hasWon = false;
        String UI = "";
        List<ChatColor> colors = new ArrayList<>();
        for (int i = 0; i < 41; i++) {
            float currentPercent = i / 40f;
            ChatColor color;

            if (isNether && (currentPercent >= location - 0.03 && currentPercent <= location + 0.03)) {
                // Nether hook
                color = ChatColor.DARK_GREEN;
                if (i == 40 && colors.getFirst().equals(ChatColor.GREEN))
                    hasWon = true;
            } else if (currentPercent >= location - (progress / 2) && currentPercent <= location + (progress / 2)) {
                color = ChatColor.GREEN;
                if (i == 40 && (colors.getFirst().equals(ChatColor.GREEN) || colors.getFirst().equals(ChatColor.DARK_GREEN)))
                    hasWon = true;
            } else if (currentPercent >= location - (progress / 2) - 0.1 && currentPercent <= location + (progress / 2) + 0.1)
                color = ChatColor.YELLOW;
            else color = ChatColor.RED;
            colors.add(color);
        }
        if (isNether) {
            // Add 20% lava to left and right
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 8; j++)
                    colors.add(i == 0 ? 0 : colors.size(), ChatColor.GOLD);
        }
        int fishLoc = (int) Math.round(fishLocation * 40);
        if (isNether) fishLoc += 8;
        colors.set(fishLoc, ChatColor.DARK_BLUE);

        for (int i = 0; i < colors.size(); i++) {
            UI += colors.get(i) + "|";
        }
        String subtitle = "";
        if (isFirstCatch) {
            if (i < 80)
                subtitle = ChatColor.GOLD + "Right Click rapidly to move the " + ChatColor.GREEN + "green bar" + ChatColor.GOLD + " to the right!";
            else if (i < 160)
                subtitle = ChatColor.GOLD + "Don't Click to move the " + ChatColor.GREEN + "green bar" + ChatColor.GOLD + " to the left!";
            else if (i < 220)
                subtitle = ChatColor.GOLD + "Keep the " + ChatColor.BLUE + "Fish" + ChatColor.GOLD + " in the " + ChatColor.GREEN + "green area" + ChatColor.GOLD + "!";
            else
                subtitle = ChatColor.GOLD + "Keep going until the whole bar turns " + ChatColor.GREEN + "green" + ChatColor.GOLD + "!";
        } else if (isFirstNetherCatch) {
            if (i < 80)
                subtitle = ChatColor.GOLD + "Your " + ChatColor.DARK_GREEN + "Hook" + ChatColor.GOLD + " is now flanked by Lava";
            else if (i < 160)
                subtitle = ChatColor.GOLD + "If it touches the Lava, you lose progress";
            else if (i < 220)
                subtitle = ChatColor.GOLD + "Keep the " + ChatColor.BLUE + "Fish" + ChatColor.GOLD + " in the " + ChatColor.GREEN + "green area" + ChatColor.GOLD + "!";
            else
                subtitle = ChatColor.GOLD + "Keep going until the whole bar turns " + ChatColor.GREEN + "green" + ChatColor.GOLD + "!";
        }
        player.sendTitle(UI, subtitle, 0, 2, 10);

        if (hasWon) {
            win();
            return;
        }

        // Increase or Decrease Progress
        if (i % 5 == 0) {
            if (getFishLocation() == 2) // If in green zone, increase progress
                progress += 0.05;
            else if (getFishLocation() == 0) {// If in red, reduce progress. If in yellow then don't do anything
                double amount = Math.min(0.075, Math.max(0.005, Math.pow(progress, 2)));
                if (tackle == ItemType.JUMBO_HOOK) amount /= 2;
                if (isFirstCatch || isFirstNetherCatch && i < 220) amount = 0;
                progress -= amount;
            }
        }

        // Player bobber
        location = Math.min(1, Math.max(0, location + locationVelocity));
        if (location == 0) {
            locationVelocity = 0;
        } else if (i % 4 == 0)
            locationVelocity -= 0.01;

        if (isNether)  {
            if (burnCooldown > 0) burnCooldown--;
            // Hook is burned, lose progress
            else if (location+0.03 >= 1 || location-0.03 <= 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.8f, 1);
                burnCooldown = 20;

                if (!isFirstNetherCatch || i >= 220)
                    progress *= 0.5;

                // Bounce away from lava
                if (location-0.03 <= 0)
                    locationVelocity = 0.05;
                else locationVelocity = -0.035;
            }
        }

        double erraticMultiplier = 2.5f;
        if (getFishLocation() == 2 && tackle == ItemType.BARBED_HOOK) erraticMultiplier = 1.5f;

        // Fish Location
        if (isNether) {
            // Nether fish like to hide in lava flanks
            if (difficulty == Difficulty.EASY) {
                double amount = RandomUtil.getRandom().nextDouble(0.006, 0.01);
                if (hasTreasure) amount *= -1;
                fishLocation = Math.min(1.15, Math.max(-0.15, fishLocation + (i % 150 < 75 ? amount : -amount)));
            } else if (difficulty == Difficulty.MEDIUM) {
                // Stage 0 is hovering, Stage 1 is shooting, Stage 2 is sinking slowly
                int stage = i % 130 < 40 ? 0 : (i % 130 < 70 ? 1 : 2);

                double amount = RandomUtil.getRandom().nextDouble(0.005, 0.008);
                double movement = switch (stage) {
                    case 2 -> amount * -1.5;
                    case 1 -> amount * (erraticMultiplier+0.5);
                    default -> (RandomUtil.getRandom().nextBoolean() ? -amount : amount);
                };

                if (hasTreasure) movement *= -1;

                fishLocation = Math.min(1.15, Math.max(-0.15, fishLocation + movement));
            } else if (difficulty == Difficulty.HARD) {
                // Stage 0 is hovering, Stage 1 is shooting up, Stage 2 is shooting down
                int stage;
                if (i % 240 < 40) stage = 0;
                else if (i % 240 < 60) stage = 1;
                else if (i % 240 < 100) stage = 0;
                else if (i % 240 < 120) stage = 2; // 100
                else if (i % 240 < 160) stage = 0;
                else if (i % 240 < 180) stage = 2;
                else if (i % 240 < 220) stage = 0;
                else stage = 1; // 220

                if (i < 540) {
                    if (i % 240 == 100 || i % 240 == 220)
                        fishStrength = RandomUtil.getRandom().nextDouble(0.5, 1.5);
                    if (i % 240 == 160 || i % 240 == 40)
                        fishStrength = 2-fishStrength;

                } else if (i == 540) fishStrength = 1;
                else if (fishStrength > 0.5) fishStrength -= 0.001;

                double amount = 0.008 * fishStrength;
                double movement = switch (stage) {
                    case 2 -> amount * -(erraticMultiplier+1.57);
                    case 1 -> amount * (erraticMultiplier+1.57);
                    default -> (RandomUtil.getRandom().nextBoolean() ? -amount : amount);
                };

                if (hasTreasure) movement *= -1;

                fishLocation = Math.min(1.15, Math.max(-0.15, fishLocation + movement));
            }

        } else {
            if (difficulty == Difficulty.EASY) {
                double amount = RandomUtil.getRandom().nextDouble(0.004, 0.008);
                if (hasTreasure) amount *= -1;
                fishLocation = Math.min(1, Math.max(0, fishLocation + (i % 150 < 75 ? amount : -amount)));
            } else if (difficulty == Difficulty.MEDIUM) {
                int stage = i % 100 < 40 ? 0 : (i % 100 < 70 ? 1 : 2); // Stage 0 is floating, Stage 1 is hovering, Stage 2 is sinking
                double amount = RandomUtil.getRandom().nextDouble(0.005, 0.008);
                fishLocation = Math.min(0.9, Math.max(0.1, fishLocation + (stage == 0 ? amount : stage == 1 ? (RandomUtil.getRandom().nextBoolean() ? -amount : amount) : -amount * erraticMultiplier)));
            } else if (difficulty == Difficulty.HARD) {
                int stage = i % 80 < 20 ? 0 : (i % 80 < 40 ? 1 : (i % 80 < 60 ? 2 : 1)); // Stage 0 is floating, Stage 1 is hovering, Stage 2 is sinking
                double amount = 0.01;
                fishLocation = Math.min(0.9, Math.max(0.1, fishLocation + (stage == 0 ? amount * erraticMultiplier : stage == 1 ? (RandomUtil.getRandom().nextBoolean() ? -amount : amount) : -amount * erraticMultiplier)));
            }
        }

        i++;
    }

    // Returns 0 if the fish is in the red, 1 if it is in the yellow and 2 if it is in the green
    private int getFishLocation() {
        // If in lava (outside of normal bar), progress decreases
        if (fishLocation < 0 || fishLocation > 1) return 0;
        if (fishLocation >= location - (progress / 2) && fishLocation <= location + (progress / 2)) return 2;
        if (fishLocation < location - (progress / 2) - 0.1 || fishLocation > location + (progress / 2) + 0.1) return 0;
        return 1;
    }

    private void win() {
        for (ItemStack drop : items) {
            Item item = hook.getWorld().dropItem(hook.getLocation(), drop);
            double d0 = player.getEyeLocation().getX() - hook.getLocation().getX();
            double d1 = player.getEyeLocation().getY() - hook.getLocation().getY();
            double d2 = player.getEyeLocation().getZ() - hook.getLocation().getZ();
            item.setVelocity(new Vector(d0 * 0.1, d1 * 0.1 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08, d2 * 0.1));
            CatchListener.applyGlow(item);

            item.setOwner(player.getUniqueId());
            item.setPickupDelay(0);
            item.setHealth(50);
        }
        int extraXp = 5;
        if (fishType.getName().startsWith(ChatColor.DARK_PURPLE+"")) extraXp = 10;
        if (fishType.getName().startsWith(ChatColor.GOLD+"")) extraXp = 50;

        player.giveExp(extraXp);

        ((FishCodex) ItemType.FISH_CODEX.getItem()).onCatch(player, fishType);

        if (fishType.getName().startsWith(ChatColor.GOLD+"")) {
            String UI = ChatColor.GOLD+"";
            for (int i = 0; i < (isNether ? 57 : 41); i++) UI += "|";
            player.sendTitle(UI, "", 0, 10, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        }
        if (hasTreasure) {
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);

            boolean isDoubleTreasure = items.size() == 3; // Fish + 2 treasure
            String message = ChatColor.GREEN+"You managed to pull up some additional treasure.";
            if (isDoubleTreasure)
                message = ChatColor.GOLD+""+ChatColor.MAGIC+"I "+message+ChatColor.GOLD+ChatColor.MAGIC+" I";
            player.sendMessage(message);
        }

        isFinished = true;
        if (isFirstCatch) FishFile.getPlayerData(player).setHasCaughtRareFish(true);
        else if (isFirstNetherCatch) FishFile.getPlayerData(player).setHasCaughtRareNetherFish(true);
    }

    public void onReel() {
        locationVelocity = Math.min(0.04, locationVelocity+0.02);
    }

    public BukkitRunnable getRunnable() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (isFinished) {
                    hook.remove();
                    CatchListener.minigameMap.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                FishingMinigame.this.run();
            }
        };
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
