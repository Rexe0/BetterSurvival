package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.BetterSurvival;
import me.rexe0.bettersurvival.farming.alcohol.AlcoholType;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.SpiritType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.util.EntityDataUtil;
import me.rexe0.bettersurvival.weather.SeasonListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CustomerListener implements Listener {
    // Villagers that players have already talked to
    private final Map<Player, VillagerHistory> villagerHistory = new HashMap<>();


    public boolean hasTalkedToVillager(Player player, Villager villager) {
        VillagerHistory history = getVillagerHistory(player);
        return history.hasOffer(villager);
    }
    public ItemStack getVillagerOffer(Player player, Villager villager) {
        VillagerHistory history = getVillagerHistory(player);

        return history.getOffer(villager);
    }
    public void setVillagerOffer(Player player, Villager villager, ItemStack item) {
        VillagerHistory history = getVillagerHistory(player);
        history.addOffer(villager, item);
    }
    public void removeVillagerOffer(Player player, Villager villager) {
        VillagerHistory history = getVillagerHistory(player);
        history.removeOffer(villager);
    }
    private VillagerHistory getVillagerHistory(Player player) {
        villagerHistory.putIfAbsent(player, new VillagerHistory());
        return villagerHistory.get(player);
    }

    private boolean isTravellingCustomer(Villager villager) {
        return villager.getScoreboardTags().contains("isTravellingCustomer");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        villagerHistory.remove(e.getPlayer());
    }

    @EventHandler
    public void onNitwitSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) e.getEntity();

        // Small chance for breeding to yield a nitwit
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING)
            if (Math.random() < 0.05) villager.setProfession(Villager.Profession.NITWIT);

        if (villager.getProfession() != Villager.Profession.NITWIT) return;

        if (isTravellingCustomer(villager))
            EntityDataUtil.setStringValue(villager, "request", Request.encodeAsString(generateRequest(ItemType.WINE)));
    }
    @EventHandler
    public void onTalk(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) e.getRightClicked();
        if (villager.getProfession() != Villager.Profession.NITWIT) return;
        Player player = e.getPlayer();

        String requestString = EntityDataUtil.getStringValue(villager, "request");
        Request request = null;
        if (requestString.isEmpty()) {
            if (isTravellingCustomer(villager)) {
                sendMessage(player, "Thanks for the trade. I'll be leaving soon...");
                return;
            } else {
                int requestDay = EntityDataUtil.getIntegerValue(villager, "requestDay");
                int day = SeasonListener.getDays();
                if (requestDay == day) {
                    sendMessage(player, "I don't have any other requests for the day. Thanks.");
                    return;
                } else {
                    request = generateRequest(ItemType.WINE);
                    EntityDataUtil.setStringValue(villager, "request", Request.encodeAsString(request));
                    EntityDataUtil.setIntegerValue(villager, "requestDay", day);
                }
            }
        }
        if (request == null) request = Request.decodeString(requestString);
        onTalk(player, villager, request);
    }

    private void onTalk(Player player, Villager villager, Request request) {
        if (!hasTalkedToVillager(player, villager)) {
            String message = switch (new Random().nextInt(4)) {
                case 0 -> "Hello, I was wondering if I could buy something from you.";
                case 1 -> "Hey, I'm willing to pay you some emeralds for something.";
                case 2 -> "Hey, I heard you were selling.";
                default -> "Hi, I have a request for you.";
            };
            sendMessage(player, message, request.getMessage());
            setVillagerOffer(player, villager, null);
            return;
        }
        ItemStack item = player.getEquipment().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendMessage(player, request.getMessage());
            return;
        }

        int price = request.getPrice(item);
        if (price == -1) {
            sendMessage(player, "That's not what I want.", request.getMessage());
            return;
        }

        if (price == -2) {
            sendMessage(player, "Looks good, but I want more of it.", request.getMessage());
            return;
        }
        if (price == -3) {
            sendMessage(player, "What are you trying to do? Kill me?");
            return;
        }
        ItemStack offer = getVillagerOffer(player, villager);
        if (offer == null || !getVillagerOffer(player, villager).equals(item)) {
            sendMessage(player, "Hmm, I'll pay " + ChatColor.DARK_GREEN + price + " Emeralds" + ChatColor.RESET + " for that. Right Click me again to confirm the trade.");
            setVillagerOffer(player, villager, item);
            return;
        }
        if (request instanceof AmountRequest amountRequest) {
            item.setAmount(item.getAmount() - amountRequest.getAmount());
        } else item.setAmount(0);

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE  , 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP  , 1, 1.8f);

        while (price > 0) {
            ItemStack emerald = new ItemStack(Material.EMERALD);
            if (price >= 64) {
                emerald.setAmount(64);
                price -= 64;
            } else {
                emerald.setAmount(price);
                price = 0;
            }
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(emerald);
            if (!leftover.isEmpty())
                for (ItemStack left : leftover.values()) {
                    Item droppedItem = player.getWorld().dropItemNaturally(player.getLocation(), left);
                    droppedItem.setOwner(player.getUniqueId());
                }
        }
        EntityDataUtil.setStringValue(villager, "request", "");
        removeVillagerOffer(player, villager);
    }
    private void sendMessage(Player player, String... messages) {
        sendMessage(player, 50, messages);
    }
    private void sendMessage(Player player, long delay, String... messages) {
        int i = 0;
        for (String msg : messages) {
            Bukkit.getScheduler().runTaskLater(BetterSurvival.getInstance(), () -> player.sendMessage(ChatColor.YELLOW + "[Villager] " + ChatColor.WHITE + msg), i * delay);
            i++;
        }
    }
    public Request generateRequest(ItemType type) {
        Random random = new Random();
        return switch (type) {
            // Normal distribution mean = 16.5
            default -> new DrugRequest(type == ItemType.CANNABIS, random.nextInt(2, 11)+random.nextInt(2, 11)+random.nextInt(2, 11));
            case WINE,SPIRIT -> {
                AlcoholRequest request = generateAlcoholRequest(random);
                if (request.getWine() != null && random.nextInt(3) == 0) request.setMinimumConcentration(request.getWine() ? random.nextInt(5, 16) : random.nextInt(40, 71));

                yield request;
            }
        };
    }
    public AlcoholRequest generateAlcoholRequest(Random random) {
        if (random.nextBoolean())
            return new AlcoholRequest();

        if (random.nextBoolean())
            return new AlcoholRequest(random.nextBoolean());

        AlcoholType alcoholType = random.nextBoolean()
                ? WineType.values()[random.nextInt(WineType.values().length)]
                : SpiritType.values()[random.nextInt(SpiritType.values().length)];
        if (random.nextBoolean())
            return new AlcoholRequest(alcoholType);

        WineType secondary = WineType.values()[random.nextInt(WineType.values().length)];
        if (random.nextBoolean())
            return new AlcoholRequest(alcoholType, secondary);

        BarrelType tertiary = BarrelType.values()[random.nextInt(BarrelType.values().length)];
        if (alcoholType instanceof WineType || random.nextBoolean())
            return new AlcoholRequest(alcoholType, secondary, tertiary);

        WineType quaternary = WineType.values()[random.nextInt(WineType.values().length)];
        if (random.nextBoolean())
            return new AlcoholRequest(alcoholType, secondary, tertiary, quaternary);
        return new AlcoholRequest(alcoholType, secondary, tertiary, quaternary, random.nextInt(6, 10));
    }

    static class VillagerHistory {
        private final Map<UUID, ItemStack> villagerOffer;

        public VillagerHistory() {
            villagerOffer = new HashMap<>();
        }
        public void addOffer(Villager villager, ItemStack item) {
            villagerOffer.put(villager.getUniqueId(), item);

            for (UUID uuid : villagerOffer.keySet()) {
                Villager v = (Villager) Bukkit.getEntity(uuid);
                if (v != null && v.isDead())
                    villagerOffer.remove(uuid);
            }
        }
        public boolean hasOffer(Villager villager) {
            return villagerOffer.containsKey(villager.getUniqueId());
        }
        public void removeOffer(Villager villager) {
            villagerOffer.remove(villager.getUniqueId());
        }
        public ItemStack getOffer(Villager villager) {
            return villagerOffer.get(villager.getUniqueId());
        }
    }
}
