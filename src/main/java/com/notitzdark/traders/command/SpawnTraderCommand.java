package com.notitzdark.traders.command;

import com.notitzdark.traders.manager.TraderManager;
import com.notitzdark.traders.model.TraderType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnTraderCommand implements CommandExecutor, TabCompleter {

    private final TraderManager traderManager;

    public SpawnTraderCommand(TraderManager traderManager) {
        this.traderManager = traderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("traders.spawn")) {
            player.sendMessage("You don't have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /spawntrader <overworld|nether>");
            return true;
        }

        String type = args[0].toLowerCase();
        TraderType traderType;

        switch (type) {
            case "overworld":
                traderType = TraderType.OVERWORLD;
                break;
            case "nether":
                traderType = TraderType.NETHER;
                break;
            default:
                player.sendMessage("§cUsage: /spawntrader <overworld|nether>");
                return true;
        }

        traderManager.spawnTrader(player.getLocation(), traderType);
        player.sendMessage("§aSpawned a " + (traderType == TraderType.OVERWORLD ? "Overworld" : "Nether") + " Trader!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("traders.spawn")) {
                completions.add("overworld");
                completions.add("nether");
            }
        }

        return completions;
    }
}
