package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetRole implements CommandExecutor, TabCompleter {

    private final RoleManager roleManager;

    public SetRole(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Использование: /setrole <hunters|runners> [playerNames]");
            return true;
        }

        Role team = Role.fromString(args[0]);
        if (team == null) {
            player.sendMessage("Неверное значение. Введите hunters или runners");
            return true;
        }

        String[] playerNames = Arrays.copyOfRange(args, 1, args.length);

        for (String playerName : playerNames) {
            Player selectedPlayer = Bukkit.getPlayer(playerName);

            if (selectedPlayer != null) {
                roleManager.assignRole(selectedPlayer, team);
            }
            else {
                player.sendMessage("Игрок " + playerName + " не найден");
            }
        }

        player.sendMessage("Игроки назначены в команду: " + team);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // Подсказки должны выводиться, когда игрок вводит первый аргумент
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("hunters");
            suggestions.add("runners");

            // Фильтруем подсказки по введённому префиксу
            String input = args[0].toLowerCase();
            suggestions.removeIf(option -> !option.startsWith(input));
        } else if (args.length >= 2) {
            String typed = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(typed)) {
                    suggestions.add(player.getName());
                }
            }
        }
        return suggestions;
    }
}
