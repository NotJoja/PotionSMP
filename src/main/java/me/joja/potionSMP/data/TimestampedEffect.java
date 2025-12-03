package me.joja.potionSMP.data;

import org.bukkit.potion.PotionEffectType;

public class TimestampedEffect {

    private final PotionEffectType type;
    private final long timestamp;

    public TimestampedEffect(PotionEffectType type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public PotionEffectType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
