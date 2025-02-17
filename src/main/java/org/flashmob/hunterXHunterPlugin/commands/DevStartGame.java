package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class DevStartGame implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Utils.setGameStarted(true);
        return true;
    }
}
