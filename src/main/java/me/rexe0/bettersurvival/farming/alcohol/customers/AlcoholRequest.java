package me.rexe0.bettersurvival.farming.alcohol.customers;

import me.rexe0.bettersurvival.farming.alcohol.AlcoholType;
import me.rexe0.bettersurvival.farming.alcohol.BarrelType;
import me.rexe0.bettersurvival.farming.alcohol.SpiritType;
import me.rexe0.bettersurvival.farming.alcohol.WineType;
import me.rexe0.bettersurvival.item.ItemType;
import me.rexe0.bettersurvival.item.drugs.Spirit;
import me.rexe0.bettersurvival.item.drugs.Wine;
import me.rexe0.bettersurvival.util.ItemDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class AlcoholRequest extends Request {
    private int minimumConcentration;
    private Boolean isWine;
    private final AlcoholType type; // Whether the request is for a spirit or wine. If null, it can be either.
    private final WineType secondaryFlavor;
    private final BarrelType tertiaryFlavor;
    private final WineType quaternaryFlavor;
    private final int minimumAge;

    public AlcoholRequest(AlcoholType type, WineType secondaryFlavor, BarrelType tertiaryFlavor, WineType quaternaryFlavor, int minimumAge) {
        this.type = type;
        if (this.type == null)
            this.isWine = null; // Can be either wine or spirit
        else
            this.isWine = type instanceof WineType;

        this.secondaryFlavor = secondaryFlavor;
        this.tertiaryFlavor = tertiaryFlavor;
        this.quaternaryFlavor = quaternaryFlavor;
        this.minimumAge = minimumAge;
    }

    public AlcoholRequest(AlcoholType type, WineType secondaryFlavor, BarrelType tertiaryFlavor, WineType quaternaryFlavor) {
        this(type, secondaryFlavor, tertiaryFlavor, quaternaryFlavor, 0);
    }
    public AlcoholRequest(AlcoholType type, WineType secondaryFlavor, BarrelType tertiaryFlavor) {
        this(type, secondaryFlavor, tertiaryFlavor, null);
    }
    public AlcoholRequest(AlcoholType type, WineType secondaryFlavor) {
        this(type, secondaryFlavor, null);
    }
    public AlcoholRequest(AlcoholType type) {
        this(type, null);
    }
    public AlcoholRequest(Boolean isWine) {
        this();
        this.isWine = isWine;
    }
    public AlcoholRequest() {
        this(null, null, null, null, 0);
    }


    public int getMinimumConcentration() {
        return minimumConcentration;
    }

    public Boolean getWine() {
        return isWine;
    }

    public AlcoholType getType() {
        return type;
    }

    public WineType getSecondaryFlavor() {
        return secondaryFlavor;
    }

    public BarrelType getTertiaryFlavor() {
        return tertiaryFlavor;
    }

    public WineType getQuaternaryFlavor() {
        return quaternaryFlavor;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumConcentration(int concentration) {
        this.minimumConcentration = concentration;
    }

    private int checkRequirements(ItemStack item) {
        int priceIncrease = 0;

        double concentration = ItemDataUtil.getDoubleValue(item, "concentration");
        if (concentration < minimumConcentration) return -1; // Not enough concentration
        else priceIncrease++;

        if (isWine != null) {
            if (isWine) {
                if (!ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID())) return -1;
                else priceIncrease++;
            } else {
                if (!ItemDataUtil.isItem(item, ItemType.SPIRIT.getItem().getID())) return -1;
                else priceIncrease++;
            }
            if (type != null) {
                AlcoholType type;
                if (isWine)
                    type = WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType"));
                else
                    type = SpiritType.valueOf(ItemDataUtil.getStringValue(item, "spiritType"));
                if (!this.type.equals(type)) return -1; // Wrong type
                else priceIncrease++;
            }
        }
        if (secondaryFlavor != null){
            if (secondaryFlavor != WineType.valueOf(ItemDataUtil.getStringValue(item, "secondaryFlavor"))) return -1; // Wrong secondary flavor
            else priceIncrease++;
        }
        if (tertiaryFlavor != null) {
            if (tertiaryFlavor != BarrelType.valueOf(ItemDataUtil.getStringValue(item, "tertiaryFlavor"))) return -1; // Wrong tertiary flavor
            else priceIncrease+=2;
        }
        if (quaternaryFlavor != null) {
            if (quaternaryFlavor != WineType.valueOf(ItemDataUtil.getStringValue(item, "quaternaryFlavor"))) return -1; // Wrong quaternary flavor
            else priceIncrease++;
        }

        if (minimumAge > ItemDataUtil.getIntegerValue(item, "age")) return -1; // Not aged enough
        else priceIncrease++;

        return priceIncrease;
    }

    public int getPrice(ItemStack item) {
        double concentration = ItemDataUtil.getDoubleValue(item, "concentration");
        if (concentration == 0) return -1; // Not an alcohol item

        int priceIncrease = checkRequirements(item);
        if (priceIncrease == -1) return -1; // Requirements not met

        boolean isWine = this.isWine != null ? this.isWine : ItemDataUtil.isItem(item, ItemType.WINE.getItem().getID());
        WineType secondary = ItemDataUtil.getStringValue(item, "secondaryFlavor").isEmpty() ? null : WineType.valueOf(ItemDataUtil.getStringValue(item, "secondaryFlavor"));
        BarrelType tertiary = ItemDataUtil.getStringValue(item, "tertiaryFlavor").isEmpty() ? null : BarrelType.valueOf(ItemDataUtil.getStringValue(item, "tertiaryFlavor"));
        WineType quaternary = ItemDataUtil.getStringValue(item, "quaternaryFlavor").isEmpty() ? null : WineType.valueOf(ItemDataUtil.getStringValue(item, "quaternaryFlavor"));

        if (isWine) {
            Wine wine = new Wine(
                    concentration,
                    WineType.valueOf(ItemDataUtil.getStringValue(item, "wineType")),
                    ItemDataUtil.getIntegerValue(item, "age"),
                    secondary,
                    tertiary
            );
            return wine.getPrice()+priceIncrease;
        }
        if (ItemDataUtil.getIntegerValue(item, "hasMethanol") == 1) return -3;
        Spirit spirit = new Spirit(
                concentration,
                SpiritType.valueOf(ItemDataUtil.getStringValue(item, "spiritType")),
                ItemDataUtil.getIntegerValue(item, "age"),
                secondary,
                tertiary,
                quaternary,
                false
        );
        return spirit.getPrice()+priceIncrease;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("I want");
        if (isWine == null) {
            sb.append(" any alcoholic drink");
        } else {
            if (type == null) {
                sb.append(" a ");
                if (isWine) sb.append(ChatColor.GREEN+"wine"+ChatColor.RESET);
                else sb.append(ChatColor.GREEN+"spirit"+ChatColor.RESET);
            } else {
                sb.append(" ").append(type.getNameColor()).append(type.getName()).append(ChatColor.RESET);
            }
        }
        if (secondaryFlavor != null) {
            String flavorName = secondaryFlavor.getFlavorName();
            if (secondaryFlavor == WineType.BEER) flavorName = "Bitterness";
            else if (secondaryFlavor == WineType.SUGAR_WASH) flavorName = "Sweetness";

            sb.append(" with a taste of ").append(secondaryFlavor.getNameColor()).append(flavorName).append(ChatColor.RESET);
            if (tertiaryFlavor != null) {
                sb.append(" and a hint of ").append(tertiaryFlavor.getName()).append(" Wood").append(ChatColor.RESET);
                if (quaternaryFlavor != null) {
                    flavorName = quaternaryFlavor.getFlavorName();
                    if (quaternaryFlavor == WineType.BEER) flavorName = "Bitterness";
                    else if (quaternaryFlavor == WineType.SUGAR_WASH) flavorName = "Sweetness";
                    sb.append(" and a touch of ").append(quaternaryFlavor.getNameColor()).append(flavorName).append(ChatColor.RESET);
                }
            }
        }

        if (minimumAge > 0) {
            boolean days = minimumAge < 4;
            sb.append(" aged for at least "+ChatColor.GREEN).append(days ? minimumAge * 30 : (minimumAge/4d)).append(days ? " days" : " years").append(ChatColor.RESET);
        }
        if (minimumConcentration > 0) {
            sb.append(" with a concentration of at least ").append(ChatColor.GREEN).append(minimumConcentration).append("%").append(ChatColor.RESET);
        }
        sb.append(".");
        return sb.toString();
    }
}
