package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.farming.alcohol.AlcoholType;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.SpiritType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.ItemType;
import org.bukkit.inventory.ItemStack;

public abstract class Request {
    // Return price in emeralds for offer, return -1 if rejected
    public abstract int getPrice(ItemStack item);

    // Get the message the villager will say to describe the request
    public abstract String getMessage();

    public static String encodeAsString(Request request) {
        if (request instanceof DrugRequest drug) return "DRUG:"+drug.getItemType().name()+":"+drug.getAmount();
        else if (request instanceof AlcoholRequest alcohol) {
            StringBuilder sb = new StringBuilder();
            sb.append("ALCOHOL:");
            if (alcohol.getWine() != null) {
                sb.append(alcohol.getWine() ? "WINE" : "SPIRIT").append(":");
            } else {
                sb.append("ANY:"); // Can be either wine or spirit
            }
            if (alcohol.getType() != null) {
                sb.append(alcohol.getType().name()).append(":");
            } else {
                sb.append("NONE:");
            }
            if (alcohol.getSecondaryFlavor() != null) {
                sb.append(alcohol.getSecondaryFlavor().name()).append(":");
            } else {
                sb.append("NONE:");
            }
            if (alcohol.getTertiaryFlavor() != null) {
                sb.append(alcohol.getTertiaryFlavor().name()).append(":");
            } else {
                sb.append("NONE:");
            }
            if (alcohol.getQuaternaryFlavor() != null) {
                sb.append(alcohol.getQuaternaryFlavor().name()).append(":");
            } else {
                sb.append("NONE:");
            }
            sb.append(alcohol.getMinimumAge()).append(":").append(alcohol.getMinimumConcentration());
            return sb.toString();
        }
        return "";
    }
    public static Request decodeString(String string) {
        String name = string.split(":")[0];
        if (name.isEmpty()) throw new IllegalArgumentException("Unknown request type: " + name);
        String[] parts = string.substring(name.length() + 1).split(":");

        switch (name) {
            case "DRUG" -> {
                return new DrugRequest(
                            ItemType.valueOf(parts[0]) == ItemType.CANNABIS,
                            Integer.parseInt(parts[1])
                    );
            }
            case "ALCOHOL" -> {
                AlcoholType type = null;
                WineType secondaryFlavor = null;
                BarrelType tertiaryFlavor = null;
                WineType quaternaryFlavor = null;

                if (parts[0].equals("WINE")) {
                    type = WineType.valueOf(parts[1]);
                } else if (parts[0].equals("SPIRIT")) {
                    type = SpiritType.valueOf(parts[1]);
                } else if (!parts[0].equals("ANY")) {
                    throw new IllegalArgumentException("Invalid alcohol type: " + parts[0]);
                }

                if (!parts[2].equals("NONE")) {
                    secondaryFlavor = WineType.valueOf(parts[2]);
                }
                if (!parts[3].equals("NONE")) {
                    tertiaryFlavor = BarrelType.valueOf(parts[3]);
                }
                if (!parts[4].equals("NONE")) {
                    quaternaryFlavor = WineType.valueOf(parts[4]);
                }

                int minimumAge = Integer.parseInt(parts[5]);
                int minimumConcentration = Integer.parseInt(parts[6]);

                AlcoholRequest request = new AlcoholRequest(type, secondaryFlavor, tertiaryFlavor, quaternaryFlavor, minimumAge);
                request.setMinimumConcentration(minimumConcentration);
                return request;
            }
            default -> throw new IllegalArgumentException("Unknown request type: " + name);
        }

    }
}
