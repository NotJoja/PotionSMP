package me.joja.potionSMP.listeners;

import me.joja.potionSMP.items.EffectItem;
import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.EffectNameHelper;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        Player killer = victim.getKiller();
        PlayerData vData = YamlStorage.getPlayerData(victim);

        TimestampedEffect oldest = vData.getOldestEffect();
        if (oldest == null) return;

        if (killer != null && killer != victim) {
            // PvP: steal oldest effect
            vData.removeEffect(oldest.getType());
            EffectManager.addEffect(killer, oldest.getType());
            killer.sendMessage("You stole " + EffectNameHelper.getPrettyName(oldest.getType()) + " from " + victim.getName());
        } else {
            // Non-player death: drop oldest effect as item
            vData.removeEffect(oldest.getType());
            ItemStack drop = EffectItem.createItem(oldest.getType());
            victim.getWorld().dropItemNaturally(victim.getLocation(), drop);
            victim.sendMessage("You dropped your oldest effect: " + EffectNameHelper.getPrettyName(oldest.getType()));
        }

        // Reapply remaining effects to victim (if any)
        EffectManager.applyEffects(victim);
    }
}
