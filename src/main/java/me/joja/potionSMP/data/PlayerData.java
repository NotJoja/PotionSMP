package me.joja.potionSMP.data;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final List<TimestampedEffect> effects;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.effects = new ArrayList<>();
    }

    public void addEffect(PotionEffectType type, long timestamp) {
        effects.add(new TimestampedEffect(type, timestamp));
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<TimestampedEffect> getEffects() {
        return effects;
    }

    private PotionEffectType originalEffect;

    public PotionEffectType getOriginalEffect() {
        return originalEffect;
    }

    public void setOriginalEffect(PotionEffectType type) {
        this.originalEffect = type;
    }

    /** Adds an effect to the player with current timestamp */
    public void addEffect(PotionEffectType type) {
        effects.add(new TimestampedEffect(type, System.currentTimeMillis()));
    }

    /** Removes one instance of the effect (oldest occurrence) */
    public boolean removeEffect(PotionEffectType type) {
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).getType() == type) {
                effects.remove(i);
                return true;
            }
        }
        return false;
    }

    /** Gets the oldest effect the player has (returns null if none) */
    public TimestampedEffect getOldestEffect() {
        if (effects.isEmpty()) return null;
        TimestampedEffect oldest = effects.get(0);
        for (TimestampedEffect effect : effects) {
            if (effect.getTimestamp() < oldest.getTimestamp()) {
                oldest = effect;
            }
        }
        return oldest;
    }

    /** Gets the level of an effect (count of instances) */
    public int getEffectLevel(PotionEffectType type) {
        int count = 0;
        for (TimestampedEffect effect : effects) {
            if (effect.getType() == type) count++;
        }
        return count;
    }
}
