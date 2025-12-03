package me.joja.potionSMP.commands;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.items.EffectItem;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        PlayerData pdata = YamlStorage.getPlayerData(player);

        if (pdata.getEffects().isEmpty()) {
            player.sendMessage("You have no effects to withdraw!");
            return true;
        }

        TimestampedEffect effectToWithdraw;
        if (args.length == 0) {
            effectToWithdraw = pdata.getOldestEffect();
        } else {
            String input = args[0].toUpperCase().replace(" ", "_");
            effectToWithdraw = null;
            for (TimestampedEffect te : pdata.getEffects()) {
                String prettyName = EffectManager.getPrettyName(te.getType());
                String typeName = te.getType().toString(); // safer than name()
                if (typeName.equalsIgnoreCase(input) || prettyName.equalsIgnoreCase(args[0])) {
                    effectToWithdraw = te;
                    break;
                }
            }

            if (effectToWithdraw == null) {
                player.sendMessage("Effect not found: " + args[0]);
                return true;
            }
        }

        pdata.removeEffect(effectToWithdraw.getType());
        WithdrawBuffer.add(player, effectToWithdraw);
        player.getInventory().addItem(EffectItem.createItem(effectToWithdraw.getType()));
        player.sendMessage("You have withdrawn: " + EffectManager.getPrettyName(effectToWithdraw.getType()));

        EffectManager.applyEffects(player);
        return true;
    }

    // --------------- Tab Completion ------------------
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        if (args.length == 1) {
            PlayerData pdata = YamlStorage.getPlayerData(player);
            List<String> suggestions = new ArrayList<>();
            for (TimestampedEffect te : pdata.getEffects()) {
                suggestions.add(EffectManager.getPrettyName(te.getType()));
            }
            return suggestions;
        }

        return Collections.emptyList();
    }
}
