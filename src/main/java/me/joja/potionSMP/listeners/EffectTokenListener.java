package me.joja.potionSMP.listeners;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.items.EffectToken;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class EffectTokenListener implements Listener {

    @EventHandler
    public void onTokenUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        Action action = event.getAction();

        // Allow right-click air or right-click block (but not left-click)
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!EffectToken.isEffectToken(item)) return;

        Integer tier = EffectToken.getTier(item);
        if (tier == null) return;

        PlayerData pdata = YamlStorage.getPlayerData(player);

        // Give a random effect based on tier
        PotionEffectType effect = EffectManager.getRandomEffectByTier(tier);

        pdata.addEffect(effect);

        // Remove one token
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
        else player.getInventory().remove(item);

        EffectManager.applyEffects(player);

        // Play note block sound
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

        player.sendMessage("§aYou received a new effect: " + EffectManager.getPrettyName(effect));

        event.setCancelled(true);
    }


}
