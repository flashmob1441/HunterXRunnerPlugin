package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassUtil;
import org.jetbrains.annotations.NotNull;

public class GetCompassCommand implements CommandExecutor {

    private final Plugin plugin;

    public GetCompassCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        // Выдаем игроку компас
        CompassUtil.giveCompass(plugin, player);

        player.sendMessage("Вы получили компас");
        return true;
    }
}
