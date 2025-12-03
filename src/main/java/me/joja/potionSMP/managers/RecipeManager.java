package me.joja.potionSMP.managers;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.items.RerollItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ItemStack;

public class RecipeManager {

    private final PotionSMP plugin;

    public RecipeManager(PotionSMP plugin) {
        this.plugin = plugin;
    }

    public void registerRerollRecipe() {
        ItemStack rerollItem = RerollItem.createRerollItem();

        NamespacedKey key = new NamespacedKey(plugin, "reroll_item");
        ShapedRecipe recipe = new ShapedRecipe(key, rerollItem);

        recipe.shape(
                "FBF",
                "INI",
                "FDF"
        );

        recipe.setIngredient('F', Material.DISC_FRAGMENT_5);
        recipe.setIngredient('B', Material.BREWING_STAND);
        recipe.setIngredient('I', Material.NETHERITE_INGOT);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('N', Material.NETHER_STAR);

        Bukkit.addRecipe(recipe);
    }
}
