package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.Cannabis;
import me.rexe0.bettersurvival.item.CocaLeaves;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.RandomUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WanderingTrader implements Listener {
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.WanderingTrader trader)) return;
        ItemStack item = switch (RandomUtil.getRandom().nextInt(4)) {
            default -> ItemType.STOPWATCH.getItem().getItem();
            case 1 -> ItemType.METAL_DETECTOR.getItem().getItem();
            case 2 -> ItemType.FISH_CODEX.getItem().getItem();
            case 3 -> {
                int potency = RandomUtil.getRandom().nextInt(0, 15);
                ItemStack itemStack;
                if (RandomUtil.getRandom().nextBoolean())
                    itemStack = new CocaLeaves(potency).getItem();
                else
                    itemStack = new Cannabis(potency).getItem();

                itemStack.setAmount(1);
                yield itemStack;
            }
        };
        MerchantRecipe trade = new MerchantRecipe(item, 1);
        trade.addIngredient(new ItemStack(Material.EMERALD, 20));
        trade.addIngredient(new ItemStack(trade.getResult().getType(), 1));
        trader.setRecipe(trader.getRecipeCount()-2, trade);
    }

    public static void runTimer() {
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty() && i >= 2400/Bukkit.getOnlinePlayers().size()) {
                    i = 0;
                    // Increase wandering trader spawn rates when there are more players online
                    ServerLevel level = ((CraftWorld) BetterSurvival.getInstance().getDefaultWorld()).getHandle();
                    WanderingTraderSpawner spawner = new WanderingTraderSpawner(level.L);
                    try {
                        Method method = spawner.getClass().getDeclaredMethod("spawn", ServerLevel.class);
                        method.setAccessible(true);
                        method.invoke(spawner, level);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                i++;
            }
        }.runTaskTimer(BetterSurvival.getInstance(), 0, 20);
    }
}
