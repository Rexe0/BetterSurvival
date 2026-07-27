package me.rexe0.bettersurvival.enchanting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;

public class AnvilChanges implements Listener {
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        AnvilView view = e.getView();
        view.setMaximumRepairCost(100);
        if (view.getRepairCost() > 15) view.setRepairCost(15);
    }
}
