package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.Cannabis;
import me.rexe0.bettersurvival.item.drugs.CocaLeaves;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.SeasonListener;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

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

    public static void run() {
        World world = BetterSurvival.getInstance().getDefaultWorld();
        if (SeasonListener.getDays() < 30) return;
        if (world.getTime() != 1) return;
        if (SeasonListener.getDays() % 5 == 0) {
            // Spawn wandering trader at the start of the day, every 5 days after the first season
            ServerLevel level = ((CraftWorld) world).getHandle();
            WanderingTraderSpawner spawner = new WanderingTraderSpawner(level.L);
            spawner.spawn(level);
        }
    }
}
