package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListRole implements CommandExecutor, TabCompleter {

    private final RoleManager roleManager;

    public ListRole(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Использование: /listrole <hunters|runners>");
            return true;
        }

        Role team = Role.fromString(args[0]);
        if (team == null) {
            player.sendMessage("Неверное значение. Введите hunters (Охотники) или runners (Бегуны).");
            return true;
        }

        List<Player> playersInTeam = roleManager.getPlayersInRole(team);
        if (playersInTeam.isEmpty()) {
            player.sendMessage("В команде " + team.getDisplayName() + " нет игроков.");
        } else {
            StringBuilder message = new StringBuilder("Игроки в команде " + team.getDisplayName() + ": ");
            for (Player teamPlayer : playersInTeam) {
                message.append(teamPlayer.getName()).append(", ");
            }
            // Удаляем завершающую запятую и пробел
            message.setLength(message.length() - 2);
            player.sendMessage(message.toString());
        }
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
        }
        return suggestions;
    }
}
