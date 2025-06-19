package me.rexe0.bettersurvival.fishing;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.fishing.Fish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishFile {

    private static final File file = new File(BetterSurvival.getInstance().getDataFolder(), File.separator + "playerdata" + File.separator + "fish.yml");
    private static Map<UUID, FishData> playerData = new HashMap<>();


    public static void fileCheck() {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                configuration.createSection("Data");
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData() {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if (file.exists()) {
            try {
                for (Map.Entry<UUID, FishData> entry : playerData.entrySet()) {
                    for (Map.Entry<Fish.FishType, Integer> map : entry.getValue().getFishes().entrySet())
                        configuration.set("Data." + entry.getKey().toString() + "." + map.getKey().name(), map.getValue());
                    configuration.set("Data." + entry.getKey().toString()+".HasCaughtRareFish", entry.getValue().hasCaughtRareFish());
                }
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadData() {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        for (String str : configuration.getConfigurationSection("Data").getKeys(false)) {
            UUID uuid = UUID.fromString(str);
            Map<Fish.FishType, Integer> map = new HashMap<>();
            for (String fish : configuration.getConfigurationSection("Data."+uuid).getKeys(false)) {
                if (fish.equals("HasCaughtRareFish")) continue;
                map.put(Fish.FishType.valueOf(fish), configuration.getInt("Data." + uuid + "." + fish));
            }
            playerData.put(uuid, new FishData(map, configuration.getBoolean("Data."+uuid+".HasCaughtRareFish", false)));
        }
    }

    public static FishData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        playerData.putIfAbsent(uuid, new FishData(new HashMap<>(), false));
        return playerData.get(player.getUniqueId());
    }
}
