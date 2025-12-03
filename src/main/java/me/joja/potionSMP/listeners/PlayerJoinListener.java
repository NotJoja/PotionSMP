package me.joja.potionSMP.listeners;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData pdata = YamlStorage.getPlayerData(player);

        if (pdata.getEffects().isEmpty()) {

            // Give random effect
            PotionEffectType newEffect = EffectManager.giveRandomEffect(player);

            // MUST SET ORIGINAL EFFECT HERE
            pdata.setOriginalEffect(newEffect);

            YamlStorage.savePlayerData(pdata);
        }

        // Reapply effects
        EffectManager.applyEffects(player);
    }
}
