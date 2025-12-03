package me.joja.potionSMP.managers;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class EffectManager {

    // Weighted lists
    private static final Map<PotionEffectType, Integer> effectWeights = new HashMap<>();

    public static final Map<PotionEffectType, String> PRETTY_NAMES = Map.ofEntries(
            Map.entry(PotionEffectType.HASTE, "Haste"),
            Map.entry(PotionEffectType.SPEED, "Speed"),
            Map.entry(PotionEffectType.SLOWNESS, "Slowness"),
            Map.entry(PotionEffectType.STRENGTH, "Strength"),
            Map.entry(PotionEffectType.RESISTANCE, "Resistance"),
            Map.entry(PotionEffectType.REGENERATION, "Regeneration"),
            Map.entry(PotionEffectType.FIRE_RESISTANCE, "Fire_Resistance"),
            Map.entry(PotionEffectType.INVISIBILITY, "Invisibility"),
            Map.entry(PotionEffectType.JUMP_BOOST, "Jump_Boost"),
            Map.entry(PotionEffectType.WATER_BREATHING, "Water_Breathing"),
            Map.entry(PotionEffectType.NIGHT_VISION, "Night_Vision"),
            Map.entry(PotionEffectType.HEALTH_BOOST, "Health_Boost"),
            Map.entry(PotionEffectType.ABSORPTION, "Absorption")
    );

    static {
        // Common
        effectWeights.put(PotionEffectType.WATER_BREATHING, 10);
        effectWeights.put(PotionEffectType.LUCK, 10);
        effectWeights.put(PotionEffectType.HERO_OF_THE_VILLAGE, 10);
        effectWeights.put(PotionEffectType.DOLPHINS_GRACE, 10);

        // Uncommon
        effectWeights.put(PotionEffectType.SPEED, 8);
        effectWeights.put(PotionEffectType.JUMP_BOOST, 8);
        effectWeights.put(PotionEffectType.CONDUIT_POWER, 8);
        effectWeights.put(PotionEffectType.HASTE, 8);
        effectWeights.put(PotionEffectType.FIRE_RESISTANCE, 8);
        effectWeights.put(PotionEffectType.STRENGTH, 8);

        // Rare
        effectWeights.put(PotionEffectType.INVISIBILITY, 6);
        effectWeights.put(PotionEffectType.SATURATION, 6);
        effectWeights.put(PotionEffectType.REGENERATION, 6);
        effectWeights.put(PotionEffectType.HEALTH_BOOST, 6);
        effectWeights.put(PotionEffectType.RESISTANCE, 6);
    }

    private static final Random random = new Random();

    /** Duration for "smooth permanent" effects in ticks (12 seconds) */
    private static final int EFFECT_DURATION_TICKS = 12 * 20;

    /** Give a player a random effect based on weight */
    public static PotionEffectType giveRandomEffect(Player player) {
        PlayerData data = YamlStorage.getPlayerData(player.getUniqueId());
        PotionEffectType effect = getWeightedRandomEffect();
        data.addEffect(effect);
        applyEffects(player);
        return effect;
    }

    public static List<String> getValidEffectNames() {
        List<String> names = new ArrayList<>();
        for (PotionEffectType type : effectWeights.keySet()) {
            names.add(getPrettyName(type));
        }
        return names;
    }

    /** Returns all PotionEffectTypes that are part of the plugin's effect pool */
    public static List<PotionEffectType> getWeightedEffects() {
        return new ArrayList<>(effectWeights.keySet());
    }


    /** Returns a weighted random effect without applying it */
    public static PotionEffectType getRandomEffect() {
        return getWeightedRandomEffect();
    }

    /** Select a weighted random effect */
    private static PotionEffectType getWeightedRandomEffect() {
        int totalWeight = effectWeights.values().stream().mapToInt(i -> i).sum();
        int r = random.nextInt(totalWeight) + 1;
        int cumulative = 0;
        for (Map.Entry<PotionEffectType, Integer> entry : effectWeights.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) {
                return entry.getKey();
            }
        }
        return PotionEffectType.SPEED; // fallback
    }

    /** Apply all effects with smooth duration */
    public static void applyEffects(Player player) {
        PlayerData data = YamlStorage.getPlayerData(player.getUniqueId());

        // Clear existing effects first
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null) player.removePotionEffect(type);
        }

        // Count occurrences for stacking
        Map<PotionEffectType, Integer> counts = new HashMap<>();
        for (TimestampedEffect te : data.getEffects()) {
            counts.put(te.getType(), counts.getOrDefault(te.getType(), 0) + 1);
        }

        // Apply each effect with level = count and short duration
        for (Map.Entry<PotionEffectType, Integer> entry : counts.entrySet()) {
            player.addPotionEffect(new PotionEffect(
                    entry.getKey(),
                    EFFECT_DURATION_TICKS,
                    entry.getValue() - 1,
                    true,
                    true,
                    true
            ));
        }

        // Schedule reapply to make it feel permanent
        Bukkit.getScheduler().runTaskLater(PotionSMP.getInstance(), () -> applyEffects(player), EFFECT_DURATION_TICKS - 1L);
    }

    /** Add effect to player and apply */
    public static void addEffect(Player player, PotionEffectType type) {
        PlayerData data = me.joja.potionSMP.utils.YamlStorage.getPlayerData(player.getUniqueId());

        // Check WithdrawBuffer for existing timestamp
        TimestampedEffect cached = me.joja.potionSMP.utils.WithdrawBuffer.getBufferedEffect(player, type);
        if (cached != null) {
            // Re-add with original timestamp
            data.addEffect(cached.getType(), cached.getTimestamp());
            WithdrawBuffer.removeBufferedEffect(player, type);
        } else {
            // Add as new effect
            data.addEffect(type);
        }

        applyEffects(player);
    }


    /** Remove one instance of effect and apply */
    public static boolean removeEffect(Player player, PotionEffectType type) {
        PlayerData data = YamlStorage.getPlayerData(player.getUniqueId());
        boolean removed = data.removeEffect(type);
        applyEffects(player);
        return removed;
    }

    /** Helper for pretty name */
    public static String getPrettyName(PotionEffectType type) {
        return PRETTY_NAMES.getOrDefault(type, type.getName());
    }
}
