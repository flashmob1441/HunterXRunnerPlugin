package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Suicide implements CommandExecutor {

    private final RoleManager roleManager;

    public Suicide(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Команда может использовать только игрок
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду могут использовать только игроки!");
            return true;
        }

        // Проверяем, что игрок имеет роль Hunters
        Optional<Role> optRole = roleManager.getRole(player);
        if (optRole.isEmpty() || optRole.get() != Role.HUNTERS) {
            player.sendMessage("Эту команду могут использовать только игроки с ролью Hunters!");
            return true;
        }

        // Убийство игрока (самоубийство)
        player.setHealth(0);
        return true;
    }
}
