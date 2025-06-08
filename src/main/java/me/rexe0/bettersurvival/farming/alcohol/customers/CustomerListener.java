package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CustomerListener implements Listener {
    @EventHandler
    public void onNitwitSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) e.getEntity();
        if (villager.getProfession() != Villager.Profession.NITWIT) return;

        EntityDataUtil.setStringValue(villager, "request", Request.encodeAsString(generateRequest()));
    }
    @EventHandler
    public void onTalk(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) e.getRightClicked();
        if (villager.getProfession() != Villager.Profession.NITWIT) return;
        Player player = e.getPlayer();

        String requestString = EntityDataUtil.getStringValue(villager, "request");
        if (requestString.isEmpty()) return;
        Request request = Request.decodeString(requestString);
        sendMessage(player, request.getMessage());
    }
    public void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.YELLOW+"[Villager] "+ChatColor.WHITE+message);
    }
    public Request generateRequest() {
        AlcoholRequest request = new AlcoholRequest(WineType.SWEET_BERRY, WineType.BEER, BarrelType.CHERRY, null, 5);
        request.setMinimumConcentration(15);
        return request;
    }
}
