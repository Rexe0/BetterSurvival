package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.alcohol.customers.CustomerSpawner;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.Cannabis;
import me.rexe0.bettersurvival.item.drugs.CocaLeaves;
import me.rexe0.bettersurvival.item.drugs.Yeast;
import me.rexe0.bettersurvival.util.RandomUtil;
import me.rexe0.bettersurvival.weather.SeasonListener;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WanderingTrader implements Listener {
    private static final CustomerSpawner customerSpawner = new CustomerSpawner();

    private final Map<ItemStack, Integer> sellTrades;

    public WanderingTrader() {
        sellTrades = new HashMap<>();
        sellTrades.put(new ItemStack(Material.ECHO_SHARD), 3);
        sellTrades.put(new ItemStack(Material.TURTLE_SCUTE), 3);
        sellTrades.put(new ItemStack(Material.ARMADILLO_SCUTE), 2);
        sellTrades.put(new ItemStack(Material.RABBIT_FOOT), 2);
        sellTrades.put(new ItemStack(Material.AMETHYST_SHARD), 2);
        sellTrades.put(new ItemStack(Material.SPONGE), 4);
        sellTrades.put(new ItemStack(Material.WITHER_ROSE), 3);
        sellTrades.put(new ItemStack(Material.HEART_OF_THE_SEA), 4);
        sellTrades.put(new ItemStack(Material.SADDLE), 4);
        sellTrades.put(new ItemStack(Material.GRASS_BLOCK), 3);
        sellTrades.put(new ItemStack(Material.DRAGON_BREATH), 5);
        sellTrades.put(new ItemStack(Material.CAKE), 4);
        sellTrades.put(new ItemStack(Material.BUBBLE_CORAL_BLOCK), 2);
        sellTrades.put(new ItemStack(Material.FIRE_CORAL_BLOCK), 2);
    }
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.WanderingTrader trader)) return;

        List<MerchantRecipe> recipes = new ArrayList<>(trader.getRecipes());

        for (int i = 0; i < 2; i++) {
            ItemStack item;
            if (i == 0) item = switch (RandomUtil.getRandom().nextInt(3)) {
                default -> ItemType.STOPWATCH.getItem().getItem();
                case 1 -> ItemType.METAL_DETECTOR.getItem().getItem();
                case 2 -> ItemType.FISH_CODEX.getItem().getItem();
            };
            else {
                int potency = RandomUtil.getRandom().nextInt(0, 15);
                item = switch (RandomUtil.getRandom().nextInt(3)) {
                    default -> new Cannabis(potency).getItem();
                    case 1 -> new CocaLeaves(potency).getItem();
                    case 2 -> new Yeast().getItem();
                };
            }

            MerchantRecipe trade = new MerchantRecipe(item, 1);
            trade.addIngredient(new ItemStack(Material.EMERALD, 20));
            trade.addIngredient(new ItemStack(trade.getResult().getType() == Material.FROGSPAWN ? Material.PUMPKIN_SEEDS : trade.getResult().getType(), 1));
            recipes.add(0, trade);
        }
        for (int i = 0; i < RandomUtil.getRandom().nextInt(2, 4); i++) {
            ItemStack item = (ItemStack) sellTrades.keySet().toArray()[RandomUtil.getRandom().nextInt(sellTrades.size())];
            MerchantRecipe trade = new MerchantRecipe(new ItemStack(Material.EMERALD, sellTrades.get(item)), 1);
            trade.addIngredient(item);
            recipes.add(0, trade);
        }

        trader.setRecipes(recipes);
    }

    public static void run() {
        if (SeasonListener.getDays() < 30) return;
        World world = BetterSurvival.getInstance().getDefaultWorld();
        if (world.getTime() % 100 == 0)
            customerSpawner.despawn(world);
        if (world.getTime() != 1) return;
        ServerLevel level = ((CraftWorld) world).getHandle();

        customerSpawner.spawn(level);

        if (SeasonListener.getDays() % 10 == 9) {
            // Spawn wandering trader at the start of the day, every 10 days after the first season
            WanderingTraderSpawner spawner = new WanderingTraderSpawner(level.L);
            spawner.spawn(level);
        }
    }
}
