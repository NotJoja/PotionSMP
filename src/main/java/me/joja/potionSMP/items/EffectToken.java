package me.joja.potionSMP.items;

import me.joja.potionSMP.PotionSMP;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EffectToken {

    private static final NamespacedKey KEY = new NamespacedKey(PotionSMP.getInstance(), "effect_token_tier");

    /** Creates an effect token of the given tier (int version) */
    public static ItemStack createToken(int tier) {
        Material mat;
        String name;
        switch (tier) {
            case 1 -> { mat = Material.NETHER_STAR; name = "Effect Token (Common)"; }
            case 2 -> { mat = Material.NETHER_STAR; name = "Effect Token (Uncommon)"; }
            case 3 -> { mat = Material.NETHER_STAR; name = "Effect Token (Rare)"; }
            default -> { mat = Material.NETHER_STAR; name = "Effect Token (Common)"; }
        }

        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b" + name);
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.INTEGER, tier);
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Creates an effect token using the TokenTier enum */
    public static ItemStack createToken(TokenTier tier) {
        int t = switch (tier) {
            case BASIC -> 1;
            case MEDIUM -> 2;
            case RARE -> 3;
        };
        return createToken(t); // reuse int version
    }

    /** Get the tier of this token */
    public static Integer getTier(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(KEY, PersistentDataType.INTEGER);
    }

    /** Check if this item is an effect token */
    public static boolean isEffectToken(ItemStack item) {
        return getTier(item) != null;
    }

    public enum TokenTier { BASIC, MEDIUM, RARE }
}
