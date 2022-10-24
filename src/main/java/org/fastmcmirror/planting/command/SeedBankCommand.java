package org.fastmcmirror.planting.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fastmcmirror.planting.seedbank.SeedBankGUI;
import org.fastmcmirror.planting.utils.Color;

public class SeedBankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.color("&cYour need to execute this command with a player"));
            return false;
        }
        Player player = (Player) sender;
        player.openInventory(SeedBankGUI.getInventory());
        return false;
    }
}
