package me.joja.potionSMP.managers;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.items.EffectToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeManager {

    public static void registerRecipes() {
        // Basic / Common Token
        ShapedRecipe basic = new ShapedRecipe(
                new NamespacedKey(PotionSMP.getInstance(), "basic_token"),
                EffectToken.createToken(EffectToken.TokenTier.BASIC)
        );
        basic.shape("DDD", "DND", "DDD"); // P = PAPER, N = NETHER_STAR
        basic.setIngredient('D', Material.DIAMOND);
        basic.setIngredient('N', Material.NETHER_STAR);
        Bukkit.addRecipe(basic);

        // Medium / Uncommon Token
        ShapedRecipe medium = new ShapedRecipe(
                new NamespacedKey(PotionSMP.getInstance(), "medium_token"),
                EffectToken.createToken(EffectToken.TokenTier.MEDIUM)
        );
        medium.shape("BBB", "BNB", "BBB"); // G = GOLD_INGOT
        medium.setIngredient('B', Material.DIAMOND_BLOCK);
        medium.setIngredient('N', Material.NETHER_STAR);
        Bukkit.addRecipe(medium);

        // Rare Token
        ShapedRecipe rare = new ShapedRecipe(
                new NamespacedKey(PotionSMP.getInstance(), "rare_token"),
                EffectToken.createToken(EffectToken.TokenTier.RARE)
        );
        rare.shape("SSS", "SNS", "SSS"); // D = DIAMOND
        rare.setIngredient('S', Material.NETHERITE_SCRAP);
        rare.setIngredient('N', Material.NETHER_STAR);
        Bukkit.addRecipe(rare);
    }
}
