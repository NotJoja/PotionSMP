package me.joja.potionSMP.items;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.utils.EffectNameHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public class EffectItem {

    private static final NamespacedKey KEY = new NamespacedKey(PotionSMP.getInstance(), "effect_type");

    /** Creates an item representing a potion effect */
    public static ItemStack createItem(PotionEffectType type) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aEffect: " + EffectNameHelper.getPrettyName(type));
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, type.getName());
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Retrieve the PotionEffectType from a given item, or null if not an effect item */
    public static PotionEffectType getEffectType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String typeName = meta.getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
        if (typeName == null) return null;
        return PotionEffectType.getByName(typeName);
    }

    /** Checks if an item is an effect item */
    public static boolean isEffectItem(ItemStack item) {
        return getEffectType(item) != null;
    }
}
