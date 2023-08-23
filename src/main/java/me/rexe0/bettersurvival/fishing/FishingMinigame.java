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

import java.util.List;

public class FishingMinigame {
    private final Player player;
    private final FishHook hook;
    private final Fish.FishType fishType;
    private final List<ItemStack> items;
    private final Difficulty difficulty;
    private final boolean hasTreasure;

    private boolean isFinished;
    private int i;
    private double progress;
    private double location;
    private double fishLocation;

    private double locationVelocity;


    public FishingMinigame(Player player, FishHook hook, Fish.FishType fishType, List<ItemStack> items, Difficulty difficulty, boolean hasTreasure) {
        this.player = player;
        this.hook = hook;
        this.fishType = fishType;
        this.items = items;
        this.difficulty = difficulty;
        this.progress = 0.125;
        this.location = 0.5;
        this.fishLocation = RandomUtil.getRandom().nextDouble(0.3, 0.7);
        this.hasTreasure = hasTreasure;
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
        for (int i = 0; i < 41; i++) {
            float currentPercent = i / 40f;
            ChatColor color;
            if (currentPercent >= location - (progress / 2) && currentPercent <= location + (progress / 2)) {
                color = ChatColor.GREEN;
                if (i == 40 && UI.startsWith(ChatColor.GREEN+""))
                    hasWon = true;
            } else if (currentPercent >= location - (progress / 2) - 0.1 && currentPercent <= location + (progress / 2) + 0.1)
                color = ChatColor.YELLOW;
            else color = ChatColor.RED;
            if (i == Math.round(fishLocation * 40)) color = ChatColor.BLUE;

            UI += color + "|";
        }
        player.sendTitle(UI, "", 0, 2, 10);

        if (hasWon) {
            win();
            return;
        }

        // Increase or Decrease Progress
        if (i % 5 == 0) {
            if (fishLocation >= location - (progress / 2) && fishLocation <= location + (progress / 2)) // If in green zone, increase progress
                progress += 0.05;
            else if (fishLocation < location - (progress / 2) - 0.1 || fishLocation > location + (progress / 2) + 0.1) // If in red, reduce progress. If in yellow then don't do anything
                progress -= Math.min(0.075, Math.max(0.005, Math.pow(progress, 2)));
        }

        // Player bobber
        location = Math.min(1, Math.max(0, location+locationVelocity));
        if (location == 0) {
            locationVelocity = 0;
        } else if (i % 4 == 0)
                locationVelocity -= 0.01;

        // Fish Location
        if (difficulty == Difficulty.EASY) {
            double amount = RandomUtil.getRandom().nextDouble(0.004, 0.008);
            fishLocation = Math.min(1, Math.max(0, fishLocation+(i % 200 < 100 ? amount : -amount)));
        } else if (difficulty == Difficulty.MEDIUM) {
            int stage = i % 100 < 40 ? 0 : (i % 100 < 70 ? 1 : 2); // Stage 0 is floating, Stage 1 is hovering, Stage 2 is sinking
            double amount = RandomUtil.getRandom().nextDouble(0.005, 0.008);
            fishLocation = Math.min(0.9, Math.max(0.1, fishLocation+(stage == 0 ? amount : stage == 1 ? (RandomUtil.getRandom().nextBoolean() ? -amount : amount) : -amount*2.5)));
        } else if (difficulty == Difficulty.HARD) {
            int stage = i % 80 < 20 ? 0 : (i % 80 < 40 ? 1 : (i % 80 < 60 ? 2 : 1)); // Stage 0 is floating, Stage 1 is hovering, Stage 2 is sinking
            double amount = 0.01;
            fishLocation = Math.min(0.9, Math.max(0.1, fishLocation+(stage == 0 ? amount*2.5 : stage == 1 ? (RandomUtil.getRandom().nextBoolean() ? -amount : amount) : -amount*2.5)));
        }

        i++;
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
        }

        ((FishCodex) ItemType.FISH_CODEX.getItem()).onCatch(player, fishType);
        if (hasTreasure) {
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
            player.sendMessage(ChatColor.GREEN+"You managed to pull up some additional treasure.");
        }

        isFinished = true;
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
