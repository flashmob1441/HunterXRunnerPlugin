package org.flashmob.hunterXHunterPlugin.commands;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if (args.length == 0) {
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
            String names = playersInTeam.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
            player.sendMessage("Игроки в команде " + team.getDisplayName() + ": " + names);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // Подсказки должны выводиться, когда игрок вводит первый аргумент
        if (args.length != 1) {
            return new ArrayList<>();
        }
        String input = args[0].toLowerCase();
        return Stream.of("hunters", "runners")
                .filter(option -> option.startsWith(input))
                .collect(Collectors.toList());
    }
}
