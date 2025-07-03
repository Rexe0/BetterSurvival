package me.rexe0.bettersurvival.constructs;

import com.jeff_media.customblockdata.CustomBlockData;
import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.item.constructs.ConstructWorkshop;
import me.rexe0.bettersurvival.util.InventoryUtil;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import me.rexe0.bettersurvival.util.SkullUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ConstructWorkshopGUI implements Listener {
    private static final NamespacedKey WORKSHOP_GHAST_UUID_KEY = new NamespacedKey(BetterSurvival.getInstance(), "WORKSHOP_GHAST_UUID");


    private final Map<Player, Block> workshopMap = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock().getType() != Material.RESPAWN_ANCHOR) return;
        Block block = e.getClickedBlock();

        if (getResearchData(block) == null) return;

        if (e.getPlayer().isSneaking()) {
            if (e.getItem().getType() == Material.GLOWSTONE) e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        Player player = e.getPlayer();
        Location location = block.getLocation().add(0.5, 1.5, 0.5);
        LeashHitch hitch = (LeashHitch) location.getWorld().getNearbyEntities(location, 1, 1, 1).stream()
                .filter(en -> en instanceof LeashHitch)
                .findFirst()
                .orElse(null);
        if (hitch == null) {
            player.sendMessage(ChatColor.RED+"Leash a Happy Ghast to a fence placed on top of the workshop.");
            return;
        }
        HappyGhast ghast = hitch.getWorld().getEntitiesByClass(HappyGhast.class).stream()
                .filter(g -> g.isLeashed() && g.getLeashHolder().equals(hitch))
                .findFirst()
                .orElse(null);

        if (ghast == null) {
            player.sendMessage(ChatColor.RED+"There is no Happy Ghast leashed to the fence pole.");
            return;
        }
        if (getGhastUUID(block) != null) {
            player.sendMessage(ChatColor.RED+"Another player is using that workshop.");
            return;
        }
        workshopMap.put(player, block);
        setGhastUUID(block, ghast.getUniqueId());

        player.openInventory(getInventory(GhastConstruct.getConstruct(ghast.getUniqueId())));
    }

    @EventHandler
    public void onGhastDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) return;
        String uuid = ghast.getUniqueId().toString();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().startsWith(ChatColor.DARK_GRAY+"Construct Workshop")) {
                Block block = workshopMap.get(player);
                if (getGhastUUID(block).toString().equals(uuid)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "The Ghast Construct you were working on has been destroyed.");
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.RESPAWN_ANCHOR) return;
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (!workshopMap.containsKey(player)) return;
        if (!workshopMap.get(player).equals(block)) return;

        if (player.getOpenInventory().getTitle().startsWith(ChatColor.DARK_GRAY+"Construct Workshop")) {
            removeGhastUUID(block);
            workshopMap.remove(player);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().startsWith(ChatColor.DARK_GRAY+"Construct Workshop")) return;
        Player player = (Player) e.getPlayer();
        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            if (!player.getOpenInventory().getTitle().startsWith(ChatColor.DARK_GRAY+"Construct Workshop"))
                onCloseMenu(player);
        }, 1);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        onCloseMenu(e.getPlayer());
    }

    @EventHandler
    public void onMainMenuClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY+"Construct Workshop")) return;
        Inventory inv = e.getClickedInventory();

        ItemStack item = e.getCurrentItem();
        if (inv.equals(e.getView().getTopInventory())) {
            if (e.getSlot() != 38) e.setCancelled(true);
        }

        Player player = (Player) e.getWhoClicked();

        Block block = workshopMap.get(player);
        UUID uuid = getGhastUUID(block);

        GhastConstruct construct = GhastConstruct.getConstruct(uuid);

        mainMenuClickLogic(construct, e.getView().getTopInventory());

        if (item == null) return;
        if (!inv.equals(e.getView().getTopInventory())) return;

        if (ItemDataUtil.isItemName(item, ChatColor.RED+"Exit")) {
            player.closeInventory();
            return;
        }
        String researchData = getResearchData(block);
        if (e.getSlot() == 11) {
            player.openInventory(getInventory(construct, ModificationType.HARNESS, researchData));
            return;
        }

        if (e.getSlot() == 29) {
            player.openInventory(getInventory(construct, ModificationType.ENGINE, researchData));
            return;
        }
        if (e.getSlot() == 15) {
            player.openInventory(getInventory(construct, ModificationType.MISCELLANEOUS, researchData));
            return;
        }
        if (e.getSlot() == 33) {
            player.openInventory(getInventory(construct, ModificationType.LOAD, researchData));
            return;
        }
    }

    private void mainMenuClickLogic(GhastConstruct construct, Inventory inventory) {
        ItemStack previousFuel = inventory.getItem(38);

        Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> {
            ItemStack fuel = inventory.getItem(38);

            if (fuel == null) fuel = new ItemStack(Material.AIR);
            if (fuel.equals(previousFuel)) return;

            construct.setFuel(fuel);
            construct.update();
        }, 1);
    }

    @EventHandler
    public void onSubMenuClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().startsWith(ChatColor.DARK_GRAY+"Construct Workshop: ")) return;
        e.setCancelled(true);
        Inventory inv = e.getClickedInventory();
        if (inv != e.getView().getTopInventory()) return;

        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        Player player = (Player) e.getWhoClicked();

        Block block = workshopMap.get(player);
        UUID uuid = getGhastUUID(block);

        GhastConstruct construct = GhastConstruct.getConstruct(uuid);

        if (ItemDataUtil.isItemName(item, ChatColor.GREEN+"Go Back")) {
            player.openInventory(getInventory(construct));
            return;
        }

        ModificationType type = ModificationType.getFromName(e.getView().getTitle().substring(ChatColor.DARK_GRAY.toString().length() + "Construct Workshop: ".length()));
        if (type == null) return;

        int i = InventoryUtil.convertInvIndexToArrayIndex(e.getSlot());
        if (i == -1) return;

        Modification modification = type.getModification(i);

        if (modification == null) return;

        String researchData = getResearchData(block);
        onClickModification(player, construct, modification, type, researchData);
    }

    private void onClickModification(Player player, GhastConstruct construct, Modification modification, ModificationType modificationType, String researchData) {
        Modification equipped = switch (modificationType) {
            case HARNESS -> construct.getHarness();
            case ENGINE -> construct.getEngine();
            case LOAD -> construct.getLoad();
            case MISCELLANEOUS -> construct.getMiscellaneous();
        };
        if (equipped.equals(modification)) {
            player.sendMessage(ChatColor.RED+"This is already equipped.");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
            return;
        }

        List<Modification> researched = ConstructWorkshop.decodeResearchData(researchData);
        if (!researched.contains(modification)) {
            Map<RecipeChoice, Integer> researchCost = modification.getResearchCost();
            if (!hasItems(researchCost, player)) {
                player.sendMessage(ChatColor.RED+"You do not have enough items to research this modification.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                return;
            }

            removeItems(researchCost, player);
            researched.add(modification);

            Block block = workshopMap.get(player);
            PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
            data.set(ConstructWorkshop.CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING, ConstructWorkshop.encodeResearchData(researched));

            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

            player.openInventory(getInventory(construct, modificationType, ConstructWorkshop.encodeResearchData(researched)));
            return;
        }
        Map<RecipeChoice, Integer> craftCost = modification.getCraftCost();
        if (!hasItems(craftCost, player)) {
            player.sendMessage(ChatColor.RED+"You do not have enough items to craft and equip this modification.");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
        removeItems(craftCost, player);

        switch (modificationType) {
            case HARNESS -> construct.setHarness((Harness) modification);
            case ENGINE -> construct.setEngine((Engine) modification);
            case LOAD -> construct.setLoad((Load) modification);
            case MISCELLANEOUS -> construct.setMiscellaneous((Miscellaneous) modification);
        }
        construct.update();

        player.openInventory(getInventory(construct));
    }

    private Inventory getInventory(GhastConstruct construct) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY+"Construct Workshop");

        if (construct.getMiscellaneous().getUses() > 0 && construct.getMiscellaneousAmmo() <= 0)
            construct.setMiscellaneous(Miscellaneous.NONE);
        if (construct.getLoad().getUses() > 0 && construct.getLoadAmmo() <= 0)
            construct.setLoad(Load.NONE);


        for (int i = 0; i < 54; i++) {

            if (i == 11) {
                Harness harness = construct.getHarness();
                ItemStack item = new ItemStack(harness.getIcon());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(harness.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY+"Harness");
                lore.addAll(harness.getDescription());
                lore.add(" ");

                List<String> statDescription = getStatDescription(harness);
                if (!statDescription.isEmpty()) {
                    lore.addAll(statDescription);
                    lore.add(" ");
                }
                lore.add(ChatColor.YELLOW+"Click to change.");

                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                continue;
            }
            if (i == 15) {
                Miscellaneous miscellaneous = construct.getMiscellaneous();
                ItemStack item = new ItemStack(miscellaneous.getIcon());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(miscellaneous.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY+"Miscellaneous");
                lore.addAll(miscellaneous.getDescription());
                lore.add(" ");
                if (miscellaneous.getUses() > 0) {
                    int ammo = construct.getMiscellaneousAmmo();
                    lore.add(ChatColor.GRAY + "Remaining: " + (ammo == 0 ? ChatColor.RED+"None" : ChatColor.GREEN+""+ammo));
                    lore.add(" ");
                }

                List<String> statDescription = getStatDescription(miscellaneous);
                if (!statDescription.isEmpty()) {
                    lore.addAll(statDescription);
                    lore.add(" ");
                }
                lore.add(ChatColor.YELLOW+"Click to change.");

                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                continue;
            }
            if (i == 29) {
                Engine engine = construct.getEngine();
                ItemStack item = new ItemStack(engine.getIcon());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(engine.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY+"Engine");
                lore.addAll(engine.getDescription());
                lore.add(" ");

                List<String> statDescription = getStatDescription(engine);
                if (!statDescription.isEmpty()) {
                    lore.addAll(statDescription);
                    lore.add(" ");
                }
                lore.add(ChatColor.YELLOW+"Click to change.");

                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                continue;
            }
            if (i == 33) {
                Load load = construct.getLoad();
                ItemStack item = new ItemStack(load.getIcon());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(load.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY+"Load");
                lore.addAll(load.getDescription());
                lore.add(" ");

                if (load.getUses() > 0) {
                    int ammo = construct.getLoadAmmo();
                    lore.add(ChatColor.GRAY + "Remaining: " + (ammo == 0 ? ChatColor.RED+"None" : ChatColor.GREEN+""+ammo));
                    lore.add(" ");
                }

                List<String> statDescription = getStatDescription(load);
                if (!statDescription.isEmpty()) {
                    lore.addAll(statDescription);
                    lore.add(" ");
                }
                lore.add(ChatColor.YELLOW+"Click to change.");

                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                continue;
            }
            if (i == 22) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN+construct.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY+"Health: "+ChatColor.GREEN+construct.getHealth());
                lore.add(ChatColor.GRAY+"Armor: "+ChatColor.GREEN+construct.getArmor());
                lore.add(ChatColor.GRAY+"Top Speed: "+ChatColor.GREEN+(Math.round(construct.getSpeed()*26000)/100d)+" km/h");
                lore.add(ChatColor.GRAY+"Acceleration: "+ChatColor.GREEN+(Math.round(construct.getAcceleration()*26000)/100d)+" km/h^2");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i, SkullUtil.getCustomSkull(item, "http://textures.minecraft.net/texture/504843421c218d0634455fdb1a6c5f7ae5b85098a50b12b9ed9d9310c84dc61b"));
                continue;
            }
            if (i == 38) {
                ItemStack fuel = construct.getFuel();
                inv.setItem(i, fuel);
                continue;
            }
            if (i == 49) {
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED+"Exit");
                item.setItemMeta(meta);
                inv.setItem(i, item);
                continue;
            }
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        boolean left = construct.getHarness() != Harness.NONE && construct.getEngine() != Engine.NONE;
        boolean right = construct.getLoad() != Load.NONE && construct.getMiscellaneous() != Miscellaneous.NONE;
        InventoryUtil.getLeftSide().forEach(i -> inv.setItem(i, new ItemStack(left ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)));
        InventoryUtil.getRightSide().forEach(i -> inv.setItem(i, new ItemStack(right ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)));


        return inv;
    }

    private Inventory getInventory(GhastConstruct construct, ModificationType modificationType, String researchData) {
        List<Modification> researched = ConstructWorkshop.decodeResearchData(researchData);

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY+"Construct Workshop: "+modificationType.getName());

        for (int i : InventoryUtil.getBorder())
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        List<? extends Modification> modifications = modificationType.getAllModifications();
        Modification equipped = switch (modificationType) {
            case HARNESS -> construct.getHarness();
            case ENGINE -> construct.getEngine();
            case LOAD -> construct.getLoad();
            case MISCELLANEOUS -> construct.getMiscellaneous();
        };

        int i = 0;
        for (Modification modification : modifications) {
            boolean isResearched = researched.contains(modification);
            ItemStack item = new ItemStack(isResearched ? modification.getIcon() : Material.PLAYER_HEAD);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(modification.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY+modificationType.getName());
            lore.addAll(modification.getDescription());
            lore.add(" ");
            List<String> statDescription = getStatDescription(modification);
            if (!statDescription.isEmpty()) {
                lore.addAll(statDescription);
                lore.add(" ");
            }

            if (equipped.equals(modification)) {
                lore.add(ChatColor.RED+"Already Equipped");
            } else if (isResearched) {
                lore.add(ChatColor.GRAY+"Cost:");
                for (Map.Entry<RecipeChoice, Integer> entry : modification.getCraftCost().entrySet()) {
                    RecipeChoice choice = entry.getKey();
                    int amount = entry.getValue();

                    String name = choice instanceof RecipeChoice.ExactChoice exact ? exact.getItemStack().getItemMeta().getDisplayName() : getMaterialName(((RecipeChoice.MaterialChoice)choice).getItemStack().getType());
                    lore.add(ChatColor.GRAY+"- "+ChatColor.YELLOW+amount+"x "+ChatColor.WHITE+name);
                }
                lore.add(" ");
                lore.add(ChatColor.GREEN+"Click to equip!");
            } else {
                lore.add(ChatColor.GRAY+"Research Cost:");
                for (Map.Entry<RecipeChoice, Integer> entry : modification.getResearchCost().entrySet()) {
                    RecipeChoice choice = entry.getKey();
                    int amount = entry.getValue();

                    String name = choice instanceof RecipeChoice.ExactChoice exact ? exact.getItemStack().getItemMeta().getDisplayName() : getMaterialName(((RecipeChoice.MaterialChoice)choice).getItemStack().getType());
                    lore.add(ChatColor.GRAY+"- "+ChatColor.YELLOW+amount+"x "+ChatColor.WHITE+name);
                }
                lore.add(" ");
                lore.add(ChatColor.GREEN+"Click to research!");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(InventoryUtil.convertArrayIndexToInvIndex(i), isResearched ? item : SkullUtil.getCustomSkull(item, "http://textures.minecraft.net/texture/46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82"));
            i++;
        }


        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Go Back");
        item.setItemMeta(meta);
        inv.setItem(49, item);

        return inv;
    }



    private boolean hasItems(Map<RecipeChoice, Integer> items, Player player) {
        int hasMaterials = 0;
        for (Map.Entry<RecipeChoice, Integer> entry : items.entrySet()) {
            int materialsAmount = 0;

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null) continue;
                if (entry.getKey().test(itemStack))
                    materialsAmount += itemStack.getAmount();
            }
            if (materialsAmount >= entry.getValue()) hasMaterials++;

        }
        return (hasMaterials >= items.size());
    }

    private void removeItems(Map<RecipeChoice, Integer> cost, Player player) {
        for (Map.Entry<RecipeChoice, Integer> entry : cost.entrySet())
            removeItems(entry.getKey(), entry.getValue(), player);
    }

    private void removeItems(RecipeChoice choice, int amount, Player player) {
        int counter = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (choice instanceof RecipeChoice.MaterialChoice && item.hasItemMeta()) continue;
            if (!choice.test(item)) continue;

            if (item.getAmount() <= counter) {
                counter -= item.getAmount();
                item.setAmount(0);
            } else if (item.getAmount() > counter) {
                item.setAmount(item.getAmount() - counter);
                break;
            }
        }
    }

    private String getResearchData(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (!data.has(ConstructWorkshop.CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING)) return null;
        return data.get(ConstructWorkshop.CONSTRUCT_WORKSHOP_KEY, PersistentDataType.STRING);
    }
    private void onCloseMenu(Player player) {
        Block block = workshopMap.get(player);
        if (block == null) return;

        removeGhastUUID(block);
        workshopMap.remove(player);
    }
    private UUID getGhastUUID(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (!data.has(WORKSHOP_GHAST_UUID_KEY, PersistentDataType.STRING)) return null;
        String str = data.get(WORKSHOP_GHAST_UUID_KEY, PersistentDataType.STRING);
        if (str == null) return null;
        return UUID.fromString(str);
    }
    private void setGhastUUID(Block block, UUID uuid) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        data.set(WORKSHOP_GHAST_UUID_KEY, PersistentDataType.STRING, uuid.toString());
    }
    private void removeGhastUUID(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, BetterSurvival.getInstance());
        if (data.has(WORKSHOP_GHAST_UUID_KEY, PersistentDataType.STRING))
            data.remove(WORKSHOP_GHAST_UUID_KEY);
    }
    private String getMaterialName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
        }
        return name.toString().trim();
    }


    private List<String> getStatDescription(Modification modification) {
        List<String> description = new ArrayList<>();
        if (modification.getHealth() != 0)
            description.add((modification.getHealth() < 0 ? ChatColor.RED : ChatColor.GREEN+"+")+ "" + modification.getHealth()+" Health");
        if (modification.getArmor() != 0)
            description.add((modification.getArmor() < 0 ? ChatColor.RED : ChatColor.GREEN+"+")+ "" + modification.getArmor()+" Armor");
        if (modification.getSpeed() != 0)
            description.add((modification.getSpeed() < 0 ? ChatColor.RED : ChatColor.GREEN+"+")+ "" + (modification.getSpeed()*100)+"% Top Speed");
        if (modification.getAcceleration() != 0)
            description.add((modification.getAcceleration() < 0 ? ChatColor.RED : ChatColor.GREEN+"+")+ "" + (modification.getAcceleration()*100)+"% Acceleration");

        return description;
    }
}
