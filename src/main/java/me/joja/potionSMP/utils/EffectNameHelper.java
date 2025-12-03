package me.joja.potionSMP.utils;

import org.bukkit.potion.PotionEffectType;

public class EffectNameHelper {

    public static String getPrettyName(PotionEffectType type) {
        if (type == null) return "Unknown";

        return switch (type.getName()) {
            case "FAST_DIGGING" -> "Haste";
            case "SLOW_DIGGING" -> "Mining Fatigue";
            case "INCREASE_DAMAGE" -> "Strength";
            case "DAMAGE_RESISTANCE" -> "Resistance";
            case "FIRE_RESISTANCE" -> "Fire Resistance";
            case "WATER_BREATHING" -> "Water Breathing";
            case "INVISIBILITY" -> "Invisibility";
            case "JUMP" -> "Jump Boost";
            case "SPEED" -> "Speed";
            case "SLOW" -> "Slowness";
            case "REGENERATION" -> "Regeneration";
            case "NIGHT_VISION" -> "Night Vision";
            case "ABSORPTION" -> "Absorption";
            case "HEALTH_BOOST" -> "Health Boost";
            case "HERO_OF_THE_VILLAGE" -> "Hero of the Village";
            case "LUCK" -> "Luck";
            case "UNLUCK" -> "Bad Luck";
            case "GLOWING" -> "Glowing";
            default -> capitalize(type.getName().toLowerCase().replace("_", " "));
        };
    }

    private static String capitalize(String s) {
        String[] parts = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }
}
