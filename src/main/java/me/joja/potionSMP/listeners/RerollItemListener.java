package me.joja.potionSMP.listeners;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.items.RerollItem;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RerollItemListener implements Listener {

    @EventHandler
    public void onPlayerUseRerollItem(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!RerollItem.isRerollItem(item)) return;

        PlayerData pdata = YamlStorage.getPlayerData(player);
        PotionEffectType original = pdata.getOriginalEffect();

        if (original == null) {
            player.sendMessage("Error: no original effect assigned.");
            return;
        }

        // Check if player still has the original effect
        if (pdata.getEffectLevel(original) == 0) {
            player.sendMessage("You can only reroll your original effect, but you no longer have it!");
            return;
        }

        // Remove original effect from player
        pdata.removeEffect(original);
        WithdrawBuffer.removeBufferedEffect(player, original);

        // Give a new random effect
        PotionEffectType newEffect = EffectManager.giveRandomEffect(player); // adjust method to return the effect
        pdata.setOriginalEffect(newEffect); // new effect becomes original

        // Remove one reroll item
        int amount = item.getAmount();
        if (amount > 1) item.setAmount(amount - 1);
        else player.getInventory().remove(item);

        // Apply effects
        EffectManager.applyEffects(player);

        player.sendMessage("Your original effect has been rerolled to: " + EffectManager.getPrettyName(newEffect));
        event.setCancelled(true);
    }
}
