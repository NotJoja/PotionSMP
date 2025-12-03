package me.joja.potionSMP.commands;

import me.joja.potionSMP.data.PlayerData;
import me.joja.potionSMP.data.TimestampedEffect;
import me.joja.potionSMP.items.EffectItem;
import me.joja.potionSMP.managers.EffectManager;
import me.joja.potionSMP.utils.WithdrawBuffer;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectAdminCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("potionsmp.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§eUsage: /effectadmin <add|remove|withdraw|reroll> <player> [effect]");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        PlayerData pdata = YamlStorage.getPlayerData(target);

        switch (action) {
            case "add", "remove" -> {
                if (args.length < 3) {
                    sender.sendMessage("§eUsage: /effectadmin " + action + " <player> <effect>");
                    return true;
                }

                // Find effect by pretty name from EffectManager
                PotionEffectType type = null;
                for (PotionEffectType t : EffectManager.getWeightedEffects()) {
                    if (EffectManager.getPrettyName(t).equalsIgnoreCase(args[2])) {
                        type = t;
                        break;
                    }
                }

                if (type == null) {
                    sender.sendMessage("§cEffect not found or not in plugin pool: " + args[2]);
                    return true;
                }

                if (action.equals("add")) {
                    pdata.addEffect(type);
                    EffectManager.applyEffects(target);
                    sender.sendMessage("§aAdded " + EffectManager.getPrettyName(type) + " to " + target.getName());
                } else {
                    pdata.removeEffect(type);
                    EffectManager.applyEffects(target);
                    sender.sendMessage("§aRemoved " + EffectManager.getPrettyName(type) + " from " + target.getName());
                }
            }

            case "withdraw" -> {
                TimestampedEffect oldest = pdata.getOldestEffect();
                if (oldest == null) {
                    sender.sendMessage("§cTarget has no effects to withdraw.");
                    return true;
                }

                pdata.removeEffect(oldest.getType());
                WithdrawBuffer.add(target, oldest);
                target.getInventory().addItem(EffectItem.createItem(oldest.getType()));
                EffectManager.applyEffects(target);
                sender.sendMessage("§aWithdrawn " + EffectManager.getPrettyName(oldest.getType()) + " from " + target.getName());
            }

            case "reroll" -> {
                TimestampedEffect oldest = pdata.getOldestEffect();
                if (oldest == null) {
                    sender.sendMessage("§cTarget has no effects to reroll.");
                    return true;
                }

                pdata.removeEffect(oldest.getType());
                PotionEffectType newEffect = EffectManager.giveRandomEffect(target);
                EffectManager.applyEffects(target);
                sender.sendMessage("§aRerolled " + EffectManager.getPrettyName(oldest.getType()) + " to " +
                        EffectManager.getPrettyName(newEffect) + " for " + target.getName());
            }

            default -> sender.sendMessage("§cUnknown action. Use add, remove, withdraw, or reroll.");
        }

        return true;
    }

    // ---------------- Tab Completion ------------------
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("potionsmp.admin")) return Collections.emptyList();

        if (args.length == 1) {
            return List.of("add", "remove", "withdraw", "reroll");
        } else if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) players.add(p.getName());
            return players;
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            return EffectManager.getValidEffectNames(); // only plugin effects
        }

        return Collections.emptyList();
    }
}
