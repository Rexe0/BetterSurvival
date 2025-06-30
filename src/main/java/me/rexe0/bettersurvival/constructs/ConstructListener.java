package me.rexe0.bettersurvival.constructs;

import me.rexe0.bettersurvival.BetterSurvival;
import org.bukkit.*;
import org.bukkit.entity.HappyGhast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ConstructListener implements Listener {
    @EventHandler
    public void onGhastDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) return;
        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());

        int power = construct.getEngine().useFuel(construct.getFuel());
        if (power > 0) {
            // Explode on death if it has fuel
            power *= construct.getFuel().getAmount();
            boolean hasFire = construct.getEngine() == Engine.MOLTEN;

            float explosionPower = Math.min(20, power/750f);
            double d = explosionPower/10;


            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
                ghast.remove();

                ghast.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, ghast.getLocation(), (int) Math.ceil(explosionPower/4), d, d, d, 0);
                ghast.getWorld().spawnParticle(Particle.LARGE_SMOKE, ghast.getLocation(), (int) (explosionPower*10), d*0.75f, d*0.75f, d*0.75f, 0);
                ghast.getWorld().spawnParticle(Particle.LARGE_SMOKE, ghast.getLocation(), (int) (explosionPower*20), 0, 0, 0, 0.4);
                ghast.getWorld().spawnParticle(Particle.LAVA, ghast.getLocation(), (int) (explosionPower*5), d, d, d, 0);

                ghast.getWorld().playSound(ghast.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, explosionPower/2, 0.6f);
                ghast.getWorld().createExplosion(ghast.getLocation(), Math.min(15, explosionPower), hasFire, ghast.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING));
            }, 5);
        } else
            ghast.getWorld().dropItemNaturally(ghast.getLocation(), construct.getFuel());


        GhastConstruct.removeConstruct(ghast.getUniqueId());
    }
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof HappyGhast ghast)) return;
        if (e.isCancelled()) return;

        GhastConstruct construct = GhastConstruct.getConstruct(ghast.getUniqueId());
        if (construct == null) return;

        ItemStack item = e.getPlayer().getEquipment().getItem(e.getHand());
        Material type = item.getType();
        if (type.isAir()) return;
        if (type == Material.SHEARS) {
            construct.setHarness(Harness.NONE);
            construct.update();
            return;
        }

        if (Tag.ITEMS_HARNESSES.isTagged(type))
            for (Harness harness : Harness.getAllHarnesses())
                if (harness.getHarness().getType() == type) {
                    construct.setHarness(harness);
                    construct.update();
                    return;
                }

    }

    public static void run() {
//        for (World world : Bukkit.getWorlds()) {
//            ServerLevel level = ((CraftWorld)world).getHandle();
//            for (HappyGhast ghast : world.getEntitiesByClass(HappyGhast.class)) {
//                // Weird cast because for some reason CraftHappyGhast.getHandle() returns NoSuchMethodException
//                net.minecraft.world.entity.animal.Animal nmsGhast = ((CraftAnimals) ghast).getHandle();
//                if (!(nmsGhast instanceof GhastConstructEntity)) {
//                    Location loc = ghast.getLocation();
//
//                    GhastConstructEntity construct = new GhastConstructEntity(level);
//                    construct.setPos(loc.getX(), loc.getY(), loc.getZ());
//                    construct.setBodyArmorItem(CraftItemStack.asNMSCopy(ghast.getEquipment().getItem(EquipmentSlot.BODY)));
//                    level.addFreshEntity(construct);
//
//                    ghast.remove();
//                }
//            }
//        }
    }
}
