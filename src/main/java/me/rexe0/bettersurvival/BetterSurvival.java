package me.rexe0.bettersurvival;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.config.ConfigLoader;
import me.rexe0.bettersurvival.constructs.ConstructListener;
import me.rexe0.bettersurvival.constructs.ConstructWorkshopGUI;
import me.rexe0.bettersurvival.constructs.GhastConstruct;
import me.rexe0.bettersurvival.farming.*;
import me.rexe0.bettersurvival.farming.alcohol.AgingListener;
import me.rexe0.bettersurvival.farming.alcohol.AlcoholListener;
import me.rexe0.bettersurvival.farming.alcohol.DistillListener;
import me.rexe0.bettersurvival.farming.alcohol.FermentListener;
import me.rexe0.bettersurvival.farming.alcohol.customers.CustomerListener;
import me.rexe0.bettersurvival.fishing.CatchListener;
import me.rexe0.bettersurvival.fishing.FishFile;
import me.rexe0.bettersurvival.fishing.PearlListener;
import me.rexe0.bettersurvival.fletchingtable.FletchingTableGUI;
import me.rexe0.bettersurvival.gear.AnvilRepair;
import me.rexe0.bettersurvival.gear.MendingChange;
import me.rexe0.bettersurvival.golf.GolfBallEntity;
import me.rexe0.bettersurvival.golf.GolfClubLogic;
import me.rexe0.bettersurvival.item.DrillEntity;
import me.rexe0.bettersurvival.item.ItemListener;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.golf.GolfTee;
import me.rexe0.bettersurvival.minecarts.ChainedMinecart;
import me.rexe0.bettersurvival.minecarts.MinecartChanges;
import me.rexe0.bettersurvival.minecarts.RailRecipes;
import me.rexe0.bettersurvival.mining.DeepDarkChanges;
import me.rexe0.bettersurvival.mining.MiningListener;
import me.rexe0.bettersurvival.mobs.*;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.weather.HolidayListener;
import me.rexe0.bettersurvival.weather.LightningRodChanges;
import me.rexe0.bettersurvival.weather.SeasonListener;
import me.rexe0.bettersurvival.worldgen.DeepOceanGenerator;
import me.rexe0.bettersurvival.worldgen.WorldGeneration;
import me.rexe0.bettersurvival.worldgen.structures.StructureOrderManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class BetterSurvival extends JavaPlugin {
    private static BetterSurvival instance;
    private static final String defaultWorld = "world";

    private static ConfigLoader configLoader;

    private Map<NamespacedKey, Recipe> recipes;
    private BukkitRunnable structureOrderManagerRunnable;

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

        generateStructuresFiles();

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
        getServer().getPluginManager().registerEvents(new DeepDarkChanges(), this);
        getServer().getPluginManager().registerEvents(new CriticalAttackListener(), this);
        getServer().getPluginManager().registerEvents(new DistillListener(), this);
        getServer().getPluginManager().registerEvents(new FermentListener(), this);
        getServer().getPluginManager().registerEvents(new AgingListener(), this);
        getServer().getPluginManager().registerEvents(new AlcoholListener(), this);
        getServer().getPluginManager().registerEvents(new CustomerListener(), this);
        getServer().getPluginManager().registerEvents(new MiningListener(), this);
        getServer().getPluginManager().registerEvents(new WolfChange(), this);
        getServer().getPluginManager().registerEvents(GolfClubLogic.getInstance(), this);
        getServer().getPluginManager().registerEvents(new PearlListener(), this);
        getServer().getPluginManager().registerEvents(WitherChanges.getInstance(), this);
        getServer().getPluginManager().registerEvents(new ConstructWorkshopGUI(), this);
        getServer().getPluginManager().registerEvents(new ConstructListener(), this);

        CustomBlockData.registerListener(this);

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
        WolfChange.startRunnable();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ChainedMinecart.run();
            SeasonListener.run();
            HolidayListener.run();
            CannabisListener.run();
            CocaineListener.run();
            DeepDarkChanges.run();
            AnimalBreeding.run();
            WanderingTrader.run();
            GolfTee.run();
            GolfClubLogic.getInstance().run();
            WitherChanges.getInstance().run();
            ConstructListener.run();
            GhastConstruct.tick();

            for (GolfBallEntity golfBall : GolfBallEntity.getGolfBalls().toArray(new GolfBallEntity[0]))
                golfBall.run();

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
        structureOrderManagerRunnable = StructureOrderManager.getInstance().start();

        Bukkit.getScheduler().runTaskTimer(this, CustomerListener::run, 0, 20);

        Bukkit.getScheduler().runTaskTimer(getInstance(), () -> Bukkit.getOnlinePlayers().forEach(AlcoholListener::alcoholTick), 0, 1200);
    }

    @Override
    public void onDisable() {
        // Clear all golf balls
        for (GolfBallEntity golfBall : GolfBallEntity.getGolfBalls().toArray(new GolfBallEntity[0]))
            golfBall.remove();

        recipes.keySet().forEach(r -> getServer().removeRecipe(r));

        FishFile.saveData();
        structureOrderManagerRunnable.cancel();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException ignored)
        {
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new DeepOceanGenerator();
    }

    public void generateStructuresFiles() {
        final String path = "structures";

        File dir = new File(getDataFolder(), path);
        if (!dir.exists())
            dir.mkdirs();


        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        // Source: https://stackoverflow.com/questions/11012819/how-can-i-access-a-folder-inside-of-a-resource-folder-from-inside-my-jar-file/20073154#20073154
        if(jarFile.isFile()) {  // Run with JAR file
            try (JarFile jar = new JarFile(jarFile)) {
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (!name.startsWith(path + "/") || name.equals(path+"/")) continue; //filter according to the path

                    File file = new File(getDataFolder(), name);
                    if (file.exists()) file.delete();
                    try (InputStream in = getResource(name)) {
                        Files.copy(in, file.toPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
