package org.flashmob.hunterXHunterPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.jetbrains.annotations.NotNull;

public class HunterChat implements CommandExecutor {

    private final RoleManager roleManager;

    public HunterChat(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args) {
        // Командный чат доступен только игрокам
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        // Проверяем, что у игрока установлена роль HUNTERS
        if (roleManager.getRole(player).isEmpty() || roleManager.getRole(player).get() != Role.HUNTERS) {
            player.sendMessage("Командный чат доступен только игрокам с ролью HUNTERS.");
            return true;
        }

        // Если сообщение не передано, показываем использование команды
        if (args.length == 0) {
            player.sendMessage("Использование: /hunterchat <сообщение>");
            return true;
        }

        // Собираем сообщение из аргументов
        String message = String.join(" ", args);

        // Форматируем сообщение с префиксом и цветом
        Component formattedMessage = Component.text("[Hunter] ", NamedTextColor.GOLD)
                .append(Component.text(player.getName() + ": ", NamedTextColor.YELLOW))
                .append(Component.text(message, NamedTextColor.WHITE));

        // Отправляем сообщение всем онлайн-игрокам с ролью HUNTERS
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (roleManager.getRole(onlinePlayer).isPresent() && roleManager.getRole(onlinePlayer).get() == Role.HUNTERS) {
                onlinePlayer.sendMessage(formattedMessage);
            }
        }

        return true;
    }
}
