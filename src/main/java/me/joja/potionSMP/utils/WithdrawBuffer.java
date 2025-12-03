package me.joja.potionSMP.utils;

import me.joja.potionSMP.data.TimestampedEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class WithdrawBuffer {

    // Maps player UUID -> list of buffered effects
    private static final Map<UUID, List<BufferedEffect>> buffer = new HashMap<>();

    // Inner class to store effect + timestamp
    private static class BufferedEffect {
        TimestampedEffect effect;
        long timestamp;

        BufferedEffect(TimestampedEffect effect) {
            this.effect = effect;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /** Add a withdrawn effect to the buffer */
    public static void add(Player player, TimestampedEffect effect) {
        buffer.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
                .add(new BufferedEffect(effect));
    }

    /** Get a buffered effect of a specific type (returns null if not found or expired) */
    public static TimestampedEffect getBufferedEffect(Player player, PotionEffectType type) {
        List<BufferedEffect> list = buffer.get(player.getUniqueId());
        if (list == null) return null;

        Iterator<BufferedEffect> it = list.iterator();
        while (it.hasNext()) {
            BufferedEffect be = it.next();
            // Remove expired entries (>3 minutes)
            if (System.currentTimeMillis() - be.timestamp > 3 * 60 * 1000L) {
                it.remove();
                continue;
            }

            if (be.effect.getType() == type) {
                it.remove(); // Remove from buffer once retrieved
                return be.effect;
            }
        }

        return null;
    }

    public static void removeBufferedEffect(Player player, PotionEffectType type) {
        List<BufferedEffect> list = buffer.get(player.getUniqueId());
        if (list == null) return;

        Iterator<BufferedEffect> it = list.iterator();
        while (it.hasNext()) {
            BufferedEffect be = it.next();
            if (be.effect.getType() == type) {
                it.remove();
                break; // remove only the first matching effect
            }
        }
    }

}
