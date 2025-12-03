package me.joja.potionSMP.items;

import me.joja.potionSMP.PotionSMP;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RerollItem {

    private static final NamespacedKey KEY = new NamespacedKey(PotionSMP.getInstance(), "reroll_item");

    /** Create the reroll item */
    public static ItemStack createRerollItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1); // example
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Reroll Effect");
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Check if an item is a reroll item */
    public static boolean isRerollItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        Byte val = item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.BYTE);
        return val != null && val == 1;
    }
}
