package me.rexe0.bettersurvival;

import com.fren_gor.ultimateAdvancementAPI.AdvancementMain;
import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.database.impl.SQLite;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.util.CoordAdapter;
import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.advs.AdvancementTabNamespaces;
import me.rexe0.bettersurvival.advs.fishing.*;
import me.rexe0.bettersurvival.config.ConfigLoader;
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
import me.rexe0.bettersurvival.item.ItemListener;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.golf.GolfTee;
import me.rexe0.bettersurvival.minecarts.ChainedMinecart;
import me.rexe0.bettersurvival.minecarts.MinecartChanges;
import me.rexe0.bettersurvival.minecarts.RailRecipes;
import me.rexe0.bettersurvival.mining.DeepDarkChanges;
import me.rexe0.bettersurvival.mining.MiningListener;
import me.rexe0.bettersurvival.mobs.*;
import me.rexe0.bettersurvival.smithing.PouringListener;
import me.rexe0.bettersurvival.smithing.SmeltingListener;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

public final class BetterSurvival extends JavaPlugin implements Listener {
    private static BetterSurvival instance;
    private static final String defaultWorld = "world";

    private static ConfigLoader configLoader;

    private Map<NamespacedKey, Recipe> recipes;
    private BukkitRunnable structureOrderManagerRunnable;

    private AdvancementMain advancementMain;
    private UltimateAdvancementAPI advancementAPI;
    public AdvancementTab fishing;

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


    public void grantCustomAdvancement(Player player, AdvancementKey key) {
        Advancement advancement = advancementAPI.getAdvancement(key);
        if (advancement == null) return;
        advancement.grant(player);
    }

    @Override
    public void onLoad() {
        advancementMain = new AdvancementMain(this);
        advancementMain.load();
    }

    @Override
    public void onEnable() {
        instance = this;

        advancementMain.enable(() -> new SQLite(advancementMain, new File(getDataFolder(), "database.db")));
        advancementAPI = UltimateAdvancementAPI.getInstance(this);
        initializeTabs();

        saveDefaultConfig();

        ConfigLoader loader = new ConfigLoader(getConfig());
        getCommand("bettersurvivalreload").setExecutor(loader);
        configLoader = loader;

        generateStructuresFiles();

        FishFile.fileCheck();
        FishFile.loadData();

        getServer().getPluginManager().registerEvents(this, this);
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
        getServer().getPluginManager().registerEvents(new SmeltingListener(), this);
        getServer().getPluginManager().registerEvents(new PouringListener(), this);
        getServer().getPluginManager().registerEvents(new SnifferChanges(), this);

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
        advancementMain.disable();

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

    public void initializeTabs() {
        fishing = advancementAPI.createAdvancementTab(AdvancementTabNamespaces.fishing_NAMESPACE);
        CoordAdapter adapterfishing = CoordAdapter.builder().add(First_rod.KEY, 0f, 0f).add(First_catch.KEY, 1f, 0f).add(Treasure_catch.KEY, 2f, -1f).add(Copper_rod.KEY, 2f, 0f).add(Platinum_ingot.KEY, 3f, 0f).add(Platinum_rod.KEY, 4f, 0f).add(Resonant_ingot.KEY, 5f, 0f).add(Resonant_rod.KEY, 6f, 0f).add(Gleaming_pearl.KEY, 7f, 0f).add(Double_treasure.KEY, 3f, -1f).add(Get_bait.KEY, 2f, 1f).add(Rare_catch.KEY, 2f, 2f).add(Get_premium_bait.KEY, 4f, 1f).add(Get_magnet.KEY, 3f, 1f).add(Quick_catch.KEY, 5f, 1f).add(Epic_catch.KEY, 3f, 2f).add(Legendary_catch.KEY, 4f, 2f).add(Monster_catch.KEY, 5f, 2f).add(Fish_stew.KEY, 2f, 3f).build();

        First_rod first_rod = new First_rod(adapterfishing.getX(First_rod.KEY), adapterfishing.getY(First_rod.KEY));

        First_catch first_catch = new First_catch(first_rod,adapterfishing.getX(First_catch.KEY), adapterfishing.getY(First_catch.KEY));
        Treasure_catch treasure_catch = new Treasure_catch(first_catch,adapterfishing.getX(Treasure_catch.KEY), adapterfishing.getY(Treasure_catch.KEY));
        Copper_rod copper_rod = new Copper_rod(first_catch,adapterfishing.getX(Copper_rod.KEY), adapterfishing.getY(Copper_rod.KEY));
        Platinum_ingot platinum_ingot = new Platinum_ingot(copper_rod,adapterfishing.getX(Platinum_ingot.KEY), adapterfishing.getY(Platinum_ingot.KEY));
        Platinum_rod platinum_rod = new Platinum_rod(platinum_ingot,adapterfishing.getX(Platinum_rod.KEY), adapterfishing.getY(Platinum_rod.KEY));
        Resonant_ingot resonant_ingot = new Resonant_ingot(platinum_rod,adapterfishing.getX(Resonant_ingot.KEY), adapterfishing.getY(Resonant_ingot.KEY));
        Resonant_rod resonant_rod = new Resonant_rod(resonant_ingot,adapterfishing.getX(Resonant_rod.KEY), adapterfishing.getY(Resonant_rod.KEY));
        Gleaming_pearl gleaming_pearl = new Gleaming_pearl(resonant_rod,adapterfishing.getX(Gleaming_pearl.KEY), adapterfishing.getY(Gleaming_pearl.KEY));
        Double_treasure double_treasure = new Double_treasure(treasure_catch,adapterfishing.getX(Double_treasure.KEY), adapterfishing.getY(Double_treasure.KEY));
        Get_bait get_bait = new Get_bait(first_catch,adapterfishing.getX(Get_bait.KEY), adapterfishing.getY(Get_bait.KEY));
        Rare_catch rare_catch = new Rare_catch(first_catch,adapterfishing.getX(Rare_catch.KEY), adapterfishing.getY(Rare_catch.KEY));
        Get_magnet get_magnet = new Get_magnet(get_bait,adapterfishing.getX(Get_magnet.KEY), adapterfishing.getY(Get_magnet.KEY));
        Get_premium_bait get_premium_bait = new Get_premium_bait(get_magnet,adapterfishing.getX(Get_premium_bait.KEY), adapterfishing.getY(Get_premium_bait.KEY));
        Quick_catch quick_catch = new Quick_catch(get_premium_bait,adapterfishing.getX(Quick_catch.KEY), adapterfishing.getY(Quick_catch.KEY));
        Epic_catch epic_catch = new Epic_catch(rare_catch,adapterfishing.getX(Epic_catch.KEY), adapterfishing.getY(Epic_catch.KEY));
        Legendary_catch legendary_catch = new Legendary_catch(epic_catch,adapterfishing.getX(Legendary_catch.KEY), adapterfishing.getY(Legendary_catch.KEY));
        Monster_catch monster_catch = new Monster_catch(legendary_catch,adapterfishing.getX(Monster_catch.KEY), adapterfishing.getY(Monster_catch.KEY));
        Fish_stew fish_stew = new Fish_stew(first_catch,adapterfishing.getX(Fish_stew.KEY), adapterfishing.getY(Fish_stew.KEY));
        fishing.registerAdvancements(first_rod ,first_catch ,treasure_catch ,copper_rod ,platinum_ingot ,platinum_rod ,resonant_ingot ,resonant_rod ,gleaming_pearl ,double_treasure ,get_bait ,rare_catch ,get_premium_bait ,get_magnet ,quick_catch ,epic_catch ,legendary_catch ,monster_catch ,fish_stew );
    }

    @EventHandler
    public void onJoin(PlayerLoadingCompletedEvent e) {
        fishing.showTab(e.getPlayer());
    }
}
