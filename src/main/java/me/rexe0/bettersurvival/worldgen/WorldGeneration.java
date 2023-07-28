package me.rexe0.bettersurvival.worldgen;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldGeneration implements Listener {
    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        World world = e.getWorld();
        if (world.equals(BetterSurvival.getInstance().getDefaultNether())) {
            world.getPopulators().add(new ReduceAncientDebris());
            return;
        }
        if (!world.equals(BetterSurvival.getInstance().getDefaultWorld())) return;
        world.getPopulators().add(new ReduceDiamonds());
        world.getPopulators().add(new ReduceGold());
        world.getPopulators().add(new ReduceIron());

        world.getPopulators().add(new RiverGeneration());
        world.getPopulators().add(new DesertGeneration());
        world.getPopulators().add(new FossilSandGeneration());

        world.getPopulators().add(new CoalDeposit());
        world.getPopulators().add(new IronDeposit());
        world.getPopulators().add(new CopperDeposit());
    }
}
