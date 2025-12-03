package me.joja.potionSMP.listeners;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.items.EffectItem;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.EffectNameHelper;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

public class EffectItemListener implements Listener {

    @EventHandler
    public void onPlayerUseEffectItem(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Only main hand
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!EffectItem.isEffectItem(item)) return;

        PotionEffectType type = EffectItem.getEffectType(item);
        PlayerData pdata = YamlStorage.getPlayerData(player);

        // Check withdraw buffer
        TimestampedEffect buffered = WithdrawBuffer.getBufferedEffect(player, type);
        if (buffered != null) {
            pdata.getEffects().add(buffered); // restore original timestamp
        } else {
            pdata.addEffect(type); // new effect
        }

        // Apply effects
        EffectManager.applyEffects(player);

        // Remove one item
        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().remove(item);
        }

        player.sendMessage("You have regained the effect: " + EffectNameHelper.getPrettyName(type));
        event.setCancelled(true);
    }


}
