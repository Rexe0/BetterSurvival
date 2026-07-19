package me.rexe0.bettersurvival.mobs;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;

public class LibrarianChanges implements Listener {
    @EventHandler
    public void onAcquireCareer(VillagerCareerChangeEvent e) {
        if (e.getProfession() != Villager.Profession.LIBRARIAN) return;
        EntityDataUtil.setStringValue(e.getEntity(), "libSubProfession", SubProfession.values()[(int) (Math.random() * SubProfession.values().length)].name());
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (!(e.getEntity() instanceof Villager villager)) return;
        if (villager.getProfession() != Villager.Profession.LIBRARIAN) return;
        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {

            // Reorder any vanilla enchanted books to be in 'first' slot out of the two for each tier
            List<MerchantRecipe> trades = villager.getRecipes();
            List<MerchantRecipe> newTrades = new ArrayList<>();

            for (int i = 0; i < trades.size(); i++) {
                MerchantRecipe recipe = trades.get(i);
                if (recipe.getResult().getType() != Material.ENCHANTED_BOOK || i % 2 == 0) {
                    newTrades.add(recipe);
                    continue;
                }
                // If enchanted book is in 'second' slot out of the two
                newTrades.add(i-1, recipe);
            }
            String id = EntityDataUtil.getStringValue(e.getEntity(), "libSubProfession");
            if (id.isEmpty()) return;


            SubProfession subProfession = null;
            try {
                subProfession = SubProfession.valueOf(id);
            } catch (IllegalArgumentException ex) {
                BetterSurvival.getInstance().getLogger().warning("Librarian SubProfession does not exist: "+id);
            }

            for (int i = 0; i < villager.getVillagerLevel(); i++) {
                BookEnchantment ench = subProfession.getEnchantment(i);
                if (ench == null) continue;

                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

                meta.addStoredEnchant(ench.enchantment(), ench.level(), true);
                book.setItemMeta(meta);

                int cost = (int) Math.pow(2, i+1);
                MerchantRecipe recipe = new MerchantRecipe(book, 0, 8, true, 5+cost, 0.2f);
                recipe.addIngredient(new ItemStack(Material.EMERALD, cost));
                recipe.addIngredient(new ItemStack(Material.BOOK));
                newTrades.set(i*2, recipe);
            }

            villager.setRecipes(newTrades);
        }, 1);

    }

    record BookEnchantment(Enchantment enchantment, int level) {
    }

    enum SubProfession {
        SHARPNESS(new BookEnchantment(Enchantment.SHARPNESS, 1), new BookEnchantment(Enchantment.SHARPNESS, 2), new BookEnchantment(Enchantment.SHARPNESS, 3), new BookEnchantment(Enchantment.SHARPNESS, 4), new BookEnchantment(Enchantment.SHARPNESS, 5)),
        SMITE(new BookEnchantment(Enchantment.SMITE, 1), new BookEnchantment(Enchantment.SMITE, 2), new BookEnchantment(Enchantment.SMITE, 3), new BookEnchantment(Enchantment.SMITE, 4), new BookEnchantment(Enchantment.SMITE, 5)),
        BANE_OF_ARTHROPODS(new BookEnchantment(Enchantment.BANE_OF_ARTHROPODS, 1), new BookEnchantment(Enchantment.BANE_OF_ARTHROPODS, 2), new BookEnchantment(Enchantment.BANE_OF_ARTHROPODS, 3), new BookEnchantment(Enchantment.BANE_OF_ARTHROPODS, 4), new BookEnchantment(Enchantment.BANE_OF_ARTHROPODS, 5)),
        POWER(new BookEnchantment(Enchantment.POWER, 1), new BookEnchantment(Enchantment.POWER, 2), new BookEnchantment(Enchantment.POWER, 3), new BookEnchantment(Enchantment.POWER, 4), new BookEnchantment(Enchantment.POWER, 5)),
        DENSITY(new BookEnchantment(Enchantment.DENSITY, 1), new BookEnchantment(Enchantment.DENSITY, 2), new BookEnchantment(Enchantment.DENSITY, 3), new BookEnchantment(Enchantment.DENSITY, 4), new BookEnchantment(Enchantment.DENSITY, 5)),
        IMPALING(new BookEnchantment(Enchantment.IMPALING, 1), new BookEnchantment(Enchantment.IMPALING, 2), new BookEnchantment(Enchantment.IMPALING, 3), new BookEnchantment(Enchantment.IMPALING, 4), new BookEnchantment(Enchantment.IMPALING, 5)),
        EFFICIENCY(new BookEnchantment(Enchantment.EFFICIENCY, 1), new BookEnchantment(Enchantment.EFFICIENCY, 2), new BookEnchantment(Enchantment.EFFICIENCY, 3), new BookEnchantment(Enchantment.EFFICIENCY, 4), new BookEnchantment(Enchantment.EFFICIENCY, 5)),

        BLAST_PROTECTION(new BookEnchantment(Enchantment.BLAST_PROTECTION, 1), new BookEnchantment(Enchantment.BLAST_PROTECTION, 2), new BookEnchantment(Enchantment.BLAST_PROTECTION, 3), new BookEnchantment(Enchantment.BLAST_PROTECTION, 4), null),
        BREACH(new BookEnchantment(Enchantment.BREACH, 1), new BookEnchantment(Enchantment.BREACH, 2), new BookEnchantment(Enchantment.BREACH, 3), new BookEnchantment(Enchantment.BREACH, 4), null),
        FEATHER_FALLING(new BookEnchantment(Enchantment.FEATHER_FALLING, 1), new BookEnchantment(Enchantment.FEATHER_FALLING, 2), new BookEnchantment(Enchantment.FEATHER_FALLING, 3), new BookEnchantment(Enchantment.FEATHER_FALLING, 4), null),
        FIRE_PROTECTION(new BookEnchantment(Enchantment.FIRE_PROTECTION, 1), new BookEnchantment(Enchantment.FIRE_PROTECTION, 2), new BookEnchantment(Enchantment.FIRE_PROTECTION, 3), new BookEnchantment(Enchantment.FIRE_PROTECTION, 4), null),
        PIERCING(new BookEnchantment(Enchantment.PIERCING, 1), new BookEnchantment(Enchantment.PIERCING, 2), new BookEnchantment(Enchantment.PIERCING, 3), new BookEnchantment(Enchantment.PIERCING, 4), null),
        PROJECTILE_PROTECTION(new BookEnchantment(Enchantment.PROJECTILE_PROTECTION, 1), new BookEnchantment(Enchantment.PROJECTILE_PROTECTION, 2), new BookEnchantment(Enchantment.PROJECTILE_PROTECTION, 3), new BookEnchantment(Enchantment.PROJECTILE_PROTECTION, 4), null),
        PROTECTION(new BookEnchantment(Enchantment.PROTECTION, 1), new BookEnchantment(Enchantment.PROTECTION, 2), new BookEnchantment(Enchantment.PROTECTION, 3), new BookEnchantment(Enchantment.PROTECTION, 4), null),

        THORNS(new BookEnchantment(Enchantment.THORNS, 1), new BookEnchantment(Enchantment.THORNS, 2), new BookEnchantment(Enchantment.THORNS, 3), null, null),
        LOYALTY(new BookEnchantment(Enchantment.LOYALTY, 1), new BookEnchantment(Enchantment.LOYALTY, 2), new BookEnchantment(Enchantment.LOYALTY, 3), null, null),

        ANY(new BookEnchantment(Enchantment.UNBREAKING, 1), new BookEnchantment(Enchantment.UNBREAKING, 2), new BookEnchantment(Enchantment.UNBREAKING, 3), new BookEnchantment(Enchantment.MENDING, 1), null),
        WATER_HEAD(new BookEnchantment(Enchantment.RESPIRATION, 1), new BookEnchantment(Enchantment.RESPIRATION, 2), new BookEnchantment(Enchantment.RESPIRATION, 3), new BookEnchantment(Enchantment.AQUA_AFFINITY, 1), null),
        WATER_BOOTS(new BookEnchantment(Enchantment.DEPTH_STRIDER, 1), new BookEnchantment(Enchantment.DEPTH_STRIDER, 2), new BookEnchantment(Enchantment.DEPTH_STRIDER, 3), new BookEnchantment(Enchantment.FROST_WALKER, 1), new BookEnchantment(Enchantment.FROST_WALKER, 2)),
        SWORD_ATTACK(new BookEnchantment(Enchantment.KNOCKBACK, 1), new BookEnchantment(Enchantment.KNOCKBACK, 2), new BookEnchantment(Enchantment.SWEEPING_EDGE, 1), new BookEnchantment(Enchantment.SWEEPING_EDGE, 2), new BookEnchantment(Enchantment.SWEEPING_EDGE, 3)),
        SWORD_EFFECT(new BookEnchantment(Enchantment.FIRE_ASPECT, 1), new BookEnchantment(Enchantment.FIRE_ASPECT, 2), new BookEnchantment(Enchantment.LOOTING, 1), new BookEnchantment(Enchantment.LOOTING, 2), new BookEnchantment(Enchantment.LOOTING, 3)),
        TRIDENT_EFFECT(new BookEnchantment(Enchantment.RIPTIDE, 1), new BookEnchantment(Enchantment.RIPTIDE, 2), new BookEnchantment(Enchantment.RIPTIDE, 3), new BookEnchantment(Enchantment.CHANNELING, 1), null),
        BOW_EFFECT(new BookEnchantment(Enchantment.PUNCH, 1), new BookEnchantment(Enchantment.PUNCH, 2), new BookEnchantment(Enchantment.FLAME, 1), new BookEnchantment(Enchantment.INFINITY, 1), null),
        CROSSBOW_EFFECT(new BookEnchantment(Enchantment.QUICK_CHARGE, 1), new BookEnchantment(Enchantment.QUICK_CHARGE, 2), new BookEnchantment(Enchantment.QUICK_CHARGE, 3), new BookEnchantment(Enchantment.MULTISHOT, 1), null),
        PICKAXE_EFFECT(new BookEnchantment(Enchantment.FORTUNE, 1), new BookEnchantment(Enchantment.FORTUNE, 2), new BookEnchantment(Enchantment.FORTUNE, 3), new BookEnchantment(Enchantment.SILK_TOUCH, 1), null),

        CURSES(new BookEnchantment(Enchantment.VANISHING_CURSE, 1), new BookEnchantment(Enchantment.BINDING_CURSE, 1), null, null, null),

        ;

        private final BookEnchantment novice;
        private final BookEnchantment apprentice;
        private final BookEnchantment journeyman;
        private final BookEnchantment expert;
        private final BookEnchantment master;

        SubProfession(BookEnchantment novice, BookEnchantment apprentice, BookEnchantment journeyman, BookEnchantment expert, BookEnchantment master) {
            this.novice = novice;
            this.apprentice = apprentice;
            this.journeyman = journeyman;
            this.expert = expert;
            this.master = master;
        }

        public BookEnchantment getEnchantment(int i) {
            return switch (i) {
                default -> novice;
                case 1 -> apprentice;
                case 2 -> journeyman;
                case 3 -> expert;
                case 4 -> master;
            };
        }
    }
}
