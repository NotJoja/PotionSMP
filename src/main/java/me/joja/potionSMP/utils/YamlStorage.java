package me.joja.potionSMP.utils;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlStorage {

    private static final Map<UUID, PlayerData> players = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    // -----------------------------------------------------------------------
    // LOAD
    // -----------------------------------------------------------------------
    public static void load() {
        file = new File(PotionSMP.getInstance().getDataFolder(), "playerdata.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException ignored) {}
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            PlayerData pdata = new PlayerData(uuid);

            // --- Load effects list ---
            List<Map<?, ?>> effectList = config.getMapList(key + ".effects");
            for (Map<?, ?> map : effectList) {
                String typeStr = (String) map.get("type");
                Object tsObj = map.get("timestamp");

                if (tsObj == null) continue;

                long ts = (tsObj instanceof Number num) ? num.longValue() : 0L;
                PotionEffectType type = PotionEffectType.getByName(typeStr);

                if (type != null) {
                    pdata.getEffects().add(new TimestampedEffect(type, ts));
                }
            }

            // --- Load originalEffect ---
            String original = config.getString(key + ".originalEffect");
            if (original != null) {
                PotionEffectType origType = PotionEffectType.getByName(original);
                pdata.setOriginalEffect(origType);
            }

            players.put(uuid, pdata);
        }
    }

    // -----------------------------------------------------------------------
    // SAVE SINGLE PLAYER
    // -----------------------------------------------------------------------
    public static void savePlayerData(UUID uuid) {
        if (config == null) return;

        PlayerData pdata = players.get(uuid);
        if (pdata == null) return;

        String path = uuid.toString();

        // ----- Save effects list -------
        List<Map<String, Object>> effectList = new ArrayList<>();
        for (TimestampedEffect te : pdata.getEffects()) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", te.getType().getName());
            map.put("timestamp", te.getTimestamp());
            effectList.add(map);
        }
        config.set(path + ".effects", effectList);

        // ----- Save original effect -------
        if (pdata.getOriginalEffect() != null) {
            config.set(path + ".originalEffect", pdata.getOriginalEffect().getName());
        } else {
            config.set(path + ".originalEffect", null);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // convenience overload
    public static void savePlayerData(PlayerData pdata) {
        savePlayerData(pdata.getUuid());
    }

    // -----------------------------------------------------------------------
    // SAVE ALL
    // -----------------------------------------------------------------------
    public static void saveAll() {
        if (config == null) return;

        for (UUID uuid : players.keySet()) {
            savePlayerData(uuid);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------
    public static PlayerData getPlayerData(UUID uuid) {
        return players.computeIfAbsent(uuid, PlayerData::new);
    }

    public static PlayerData getPlayerData(org.bukkit.entity.Player player) {
        return getPlayerData(player.getUniqueId());
    }
}
