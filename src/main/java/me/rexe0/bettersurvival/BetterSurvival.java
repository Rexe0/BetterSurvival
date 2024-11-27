package me.rexe0.bettersurvival;

import me.rexe0.bettersurvival.config.ConfigLoader;
import me.rexe0.bettersurvival.farming.*;
import me.rexe0.bettersurvival.fishing.CatchListener;
import me.rexe0.bettersurvival.fishing.FishFile;
import me.rexe0.bettersurvival.fletchingtable.FletchingTableGUI;
import me.rexe0.bettersurvival.gear.AnvilRepair;
import me.rexe0.bettersurvival.gear.MendingChange;
import me.rexe0.bettersurvival.item.DrillEntity;
import me.rexe0.bettersurvival.item.ItemListener;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.minecarts.ChainedMinecart;
import me.rexe0.bettersurvival.minecarts.MinecartChanges;
import me.rexe0.bettersurvival.minecarts.RailRecipes;
import me.rexe0.bettersurvival.mobs.*;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.weather.HolidayListener;
import me.rexe0.bettersurvival.weather.LightningRodChanges;
import me.rexe0.bettersurvival.weather.SeasonListener;
import me.rexe0.bettersurvival.worldgen.WorldGeneration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class BetterSurvival extends JavaPlugin {
    private static BetterSurvival instance;
    private static final String defaultWorld = "world";

    private Map<NamespacedKey, Recipe> recipes;
    private static ConfigLoader configLoader;

    public static ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public World getDefaultWorld() {
        return Bukkit.getWorld(defaultWorld);
    }
    public World getDefaultNether() {
        return Bukkit.getWorld(defaultWorld+"_nether");
    }
    public World getDefaultEnd() {
        return Bukkit.getWorld(defaultWorld+"_the_end");
    }

    public static BetterSurvival getInstance() {
        return instance;
    }

    public Map<NamespacedKey, Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        ConfigLoader loader = new ConfigLoader(getConfig());
        getCommand("bettersurvivalreload").setExecutor(loader);
        configLoader = loader;

        FishFile.fileCheck();
        FishFile.loadData();

        getServer().getPluginManager().registerEvents(new WorldGeneration(), this);
        getServer().getPluginManager().registerEvents(new AnvilRepair(), this);
        getServer().getPluginManager().registerEvents(new ElderGuardianDrops(), this);
        getServer().getPluginManager().registerEvents(new MendingChange(), this);
        getServer().getPluginManager().registerEvents(new GrowthModifier(), this);
        getServer().getPluginManager().registerEvents(new HarvestModifier(), this);
        getServer().getPluginManager().registerEvents(new AnimalBreeding(), this);
        getServer().getPluginManager().registerEvents(new ComposterChanges(), this);
        getServer().getPluginManager().registerEvents(new FoodModifications(), this);
        getServer().getPluginManager().registerEvents(new PhantomChange(), this);
        getServer().getPluginManager().registerEvents(new PiglinChange(), this);
        getServer().getPluginManager().registerEvents(new VillagerChange(), this);
        getServer().getPluginManager().registerEvents(new FletchingTableGUI(), this);
        getServer().getPluginManager().registerEvents(new WanderingTrader(), this);
        getServer().getPluginManager().registerEvents(new SolsticeGlowSquid(), this);
        getServer().getPluginManager().registerEvents(new ChainedMinecart(), this);
        getServer().getPluginManager().registerEvents(new MinecartChanges(), this);
        getServer().getPluginManager().registerEvents(new HorseBreeding(), this);
        getServer().getPluginManager().registerEvents(new LightningRodChanges(), this);
        getServer().getPluginManager().registerEvents(new EnderDragonChanges(), this);
        getServer().getPluginManager().registerEvents(new CatchListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new CannabisListener(), this);
        getServer().getPluginManager().registerEvents(new CocaineListener(), this);

        recipes = new HashMap<>();
        recipes.put(RailRecipes.getRailRecipe().getKey(), RailRecipes.getRailRecipe());
        recipes.put(FoodModifications.getSuspiciousStewRecipe().getKey(), FoodModifications.getSuspiciousStewRecipe());
        for (ItemType type : ItemType.values()) {
            recipes.putAll(type.getItem().getRecipes());

            Recipe recipe = type.getItem().getRecipe();
            if (recipe != null) recipes.put(new NamespacedKey(this, type.getItem().getID()), recipe);

        }

        recipes.values().forEach(r -> getServer().addRecipe(r));

        DrillEntity.runTimer();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ChainedMinecart.run();
            SeasonListener.run();
            HolidayListener.run();
            CannabisListener.run();
            CocaineListener.run();
        }, 0, 1);
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().forEach((player) -> {
            for (ItemType type : ItemType.values()) {
                if (ItemDataUtil.isItem(player.getEquipment().getItemInMainHand(), type.getItem().getID())
                        || ItemDataUtil.isItem(player.getEquipment().getItemInOffHand(), type.getItem().getID()))
                    type.getItem().holdCheck(player);
                if (ItemDataUtil.isItem(player.getEquipment().getHelmet(), type.getItem().getID())
                        || ItemDataUtil.isItem(player.getEquipment().getChestplate(), type.getItem().getID())
                        || ItemDataUtil.isItem(player.getEquipment().getLeggings(), type.getItem().getID())
                        || ItemDataUtil.isItem(player.getEquipment().getBoots(), type.getItem().getID()))
                    type.getItem().armorEquipped(player);
            }
        }), 0, 5);
    }

    @Override
    public void onDisable() {
        recipes.keySet().forEach(r -> getServer().removeRecipe(r));

        FishFile.saveData();
    }
}
