package me.joja.potionSMP.managers;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class EffectManager {

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

    private static final Random random = new Random();
    private static final int EFFECT_DURATION_TICKS = 12 * 20; // 12 seconds

    static {
        // common
        effectWeights.put(PotionEffectType.WATER_BREATHING, 10);
        effectWeights.put(PotionEffectType.LUCK, 10);
        effectWeights.put(PotionEffectType.HERO_OF_THE_VILLAGE, 10);
        effectWeights.put(PotionEffectType.DOLPHINS_GRACE, 10);
        // uncommon
        effectWeights.put(PotionEffectType.SPEED, 8);
        effectWeights.put(PotionEffectType.JUMP_BOOST, 8);
        effectWeights.put(PotionEffectType.CONDUIT_POWER, 8);
        effectWeights.put(PotionEffectType.HASTE, 8);
        effectWeights.put(PotionEffectType.FIRE_RESISTANCE, 8);
        effectWeights.put(PotionEffectType.STRENGTH, 8);
        // rare
        effectWeights.put(PotionEffectType.INVISIBILITY, 6);
        effectWeights.put(PotionEffectType.SATURATION, 6);
        effectWeights.put(PotionEffectType.REGENERATION, 6);
        effectWeights.put(PotionEffectType.HEALTH_BOOST, 6);
        effectWeights.put(PotionEffectType.RESISTANCE, 6);
    }

    /** Give a weighted random effect to a player and return it */
    public static PotionEffectType giveRandomEffect(Player player) {
        PlayerData data = YamlStorage.getPlayerData(player);
        PotionEffectType effect = getWeightedRandomEffect();
        data.addEffect(effect);
        applyEffects(player);
        return effect;
    }

    /** Add an effect manually (respects WithdrawBuffer) */
    public static void addEffect(Player player, PotionEffectType type) {
        PlayerData data = YamlStorage.getPlayerData(player);

        TimestampedEffect cached = WithdrawBuffer.getBufferedEffect(player, type);
        if (cached != null) {
            data.addEffect(cached.getType(), cached.getTimestamp());
            WithdrawBuffer.removeBufferedEffect(player, type);
        } else {
            data.addEffect(type);
        }

        applyEffects(player);
    }

    /** Remove one instance */
    public static boolean removeEffect(Player player, PotionEffectType type) {
        PlayerData data = YamlStorage.getPlayerData(player);
        boolean removed = data.removeEffect(type);
        applyEffects(player);
        return removed;
    }

    /** Apply all effects smoothly while handling Health Boost & Absorption */
    public static void applyEffects(Player player) {
        PlayerData data = YamlStorage.getPlayerData(player.getUniqueId());

        // Track total Health Boost levels
        int healthBoostLevel = 0;
        int absorptionLevel = 0;

        // Clear all effects except Health Boost and Absorption
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null && type != PotionEffectType.HEALTH_BOOST && type != PotionEffectType.ABSORPTION) {
                player.removePotionEffect(type);
            }
        }

        // Count occurrences for stacking and store special effects
        Map<PotionEffectType, Integer> counts = new HashMap<>();
        for (TimestampedEffect te : data.getEffects()) {
            if (te.getType() == PotionEffectType.HEALTH_BOOST) healthBoostLevel++;
            else if (te.getType() == PotionEffectType.ABSORPTION) absorptionLevel++;
            counts.put(te.getType(), counts.getOrDefault(te.getType(), 0) + 1);
        }

        // Apply regular effects with short duration
        for (Map.Entry<PotionEffectType, Integer> entry : counts.entrySet()) {
            PotionEffectType type = entry.getKey();
            if (type == PotionEffectType.HEALTH_BOOST || type == PotionEffectType.ABSORPTION) continue;

            player.addPotionEffect(new PotionEffect(
                    type,
                    EFFECT_DURATION_TICKS,
                    entry.getValue() - 1,
                    true,
                    true,
                    true
            ));
        }

        // Apply Health Boost correctly
        if (healthBoostLevel > 0) {
            int maxHealth = 20 + healthBoostLevel * 4; // each level = 2 extra hearts (4 health)
            player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            if (player.getHealth() > maxHealth) player.setHealth(maxHealth);
        } else {
            // Reset to default max health if player lost Health Boost
            player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(20.0);
            if (player.getHealth() > 20) player.setHealth(20.0);
        }

        // Apply Absorption separately
        if (absorptionLevel > 0) {
            int absorptionAmount = absorptionLevel * 4; // 2 hearts per level
            player.setAbsorptionAmount(absorptionAmount);
        } else {
            player.setAbsorptionAmount(0);
        }

        // Schedule reapply for all effects to make them "permanent"
        Bukkit.getScheduler().runTaskLater(PotionSMP.getInstance(), () -> applyEffects(player), EFFECT_DURATION_TICKS - 1L);
    }


    /** Pretty name helper */
    public static String getPrettyName(PotionEffectType type) {
        return PRETTY_NAMES.getOrDefault(type, type.getName());
    }

    /** Weighted random selection */
    private static PotionEffectType getWeightedRandomEffect() {
        int totalWeight = effectWeights.values().stream().mapToInt(i -> i).sum();
        int r = random.nextInt(totalWeight) + 1;
        int cumulative = 0;
        for (Map.Entry<PotionEffectType, Integer> entry : effectWeights.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) return entry.getKey();
        }
        return PotionEffectType.SPEED; // fallback
    }

    public static List<PotionEffectType> getWeightedEffects() {
        return new ArrayList<>(effectWeights.keySet());
    }

    public static List<String> getValidEffectNames() {
        List<String> names = new ArrayList<>();
        for (PotionEffectType type : effectWeights.keySet()) names.add(getPrettyName(type));
        return names;
    }

    /** Get a weighted random effect by token tier */
    public static PotionEffectType getRandomEffectByTier(int tier) {
        Map<PotionEffectType, Integer> pool = new HashMap<>();

        for (Map.Entry<PotionEffectType, Integer> entry : effectWeights.entrySet()) {
            PotionEffectType type = entry.getKey();
            int weight = entry.getValue();

            if (tier == 1 && isCommon(type)) pool.put(type, weight);
            else if (tier == 2 && isUncommon(type)) pool.put(type, weight);
            else if (tier == 3 && isRare(type)) pool.put(type, weight);
        }

        if (pool.isEmpty()) return PotionEffectType.SPEED; // fallback

        // Weighted random selection
        int total = pool.values().stream().mapToInt(i -> i).sum();
        int r = random.nextInt(total) + 1;
        int cumulative = 0;
        for (Map.Entry<PotionEffectType, Integer> entry : pool.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) return entry.getKey();
        }

        return PotionEffectType.SPEED; // fallback
    }

    private static boolean isCommon(PotionEffectType type) {
        return type == PotionEffectType.WATER_BREATHING
                || type == PotionEffectType.LUCK
                || type == PotionEffectType.HERO_OF_THE_VILLAGE
                || type == PotionEffectType.DOLPHINS_GRACE;
    }

    private static boolean isUncommon(PotionEffectType type) {
        return type == PotionEffectType.SPEED
                || type == PotionEffectType.JUMP_BOOST
                || type == PotionEffectType.CONDUIT_POWER
                || type == PotionEffectType.HASTE
                || type == PotionEffectType.FIRE_RESISTANCE
                || type == PotionEffectType.STRENGTH;
    }

    private static boolean isRare(PotionEffectType type) {
        return type == PotionEffectType.INVISIBILITY
                || type == PotionEffectType.SATURATION
                || type == PotionEffectType.REGENERATION
                || type == PotionEffectType.HEALTH_BOOST
                || type == PotionEffectType.RESISTANCE;
    }
}
