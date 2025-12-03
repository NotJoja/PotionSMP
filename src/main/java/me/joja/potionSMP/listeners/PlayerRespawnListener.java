package me.joja.potionSMP.listeners;

import me.joja.potionSMP.PotionSMP;
import me.joja.potionSMP.managers.EffectManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Delay by 1 tick to ensure effects stick
        PotionSMP.getInstance().getServer().getScheduler().runTaskLater(PotionSMP.getInstance(), () -> {
            EffectManager.applyEffects(player);
        }, 1L);
    }
}