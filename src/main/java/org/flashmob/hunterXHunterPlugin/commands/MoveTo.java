package org.flashmob.hunterXHunterPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MoveTo implements CommandExecutor {

    private final RoleManager roleManager;
    // Содержит время окончания кулдауна для каждого игрока (UUID -> timestamp в мс)
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    // Хранит UUID игроков, которые использовали команду в текущей жизни
    private static final Set<UUID> usedInLife = new HashSet<>();
    // Длительность кулдауна после смерти (в секундах)
    private static int COOLDOWN_DURATION = 1;

    public MoveTo(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        COOLDOWN_DURATION = plugin.getConfig().getInt("teleport_cooldown_in_seconds");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        UUID playerId = player.getUniqueId();

        // Проверка роли игрока (только HUNTERS могут использовать эту команду)
        if (roleManager.getRole(player).filter(role -> role == Role.HUNTERS).isEmpty()) {
            player.sendMessage("Эту команду могут использовать только игроки с ролью HUNTERS.");
            return true;
        }

        // Проверка кулдауна
        long now = System.currentTimeMillis();
        Long cooldownEnd = cooldowns.get(playerId);
        if (cooldownEnd != null && (cooldownEnd - now) > 0) {
            long secondsRemaining = (cooldownEnd - now) / 1000;
            player.sendMessage("Команда недоступна. Повторное использование возможно через " + secondsRemaining + " секунд.");
            return true;
        } else if (cooldownEnd != null) {
            cooldowns.remove(playerId);
        }

        // Если команда уже была использована в этой жизни
        if (usedInLife.contains(playerId)) {
            player.sendMessage("Вы уже использовали команду в этой жизни. Дождитесь смерти и завершения кулдауна.");
            return true;
        }

        // Проверка аргументов (имя целевого игрока)
        if (args.length < 1) {
            player.sendMessage("Использование: /moveto <имя_игрока>");
            return true;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayerExact(targetName);
        if (targetPlayer == null) {
            player.sendMessage("Игрок \"" + targetName + "\" не найден или не в онлайне.");
            return true;
        }

        if (targetPlayer.getUniqueId().equals(playerId)) {
            player.sendMessage("Самому себе нельзя телепортироваться.");
            return true;
        }

        // Игроки должны находиться в одном окружении (например, в одном измерении)
        if (!player.getWorld().getEnvironment().equals(targetPlayer.getWorld().getEnvironment())) {
            player.sendMessage("Игрок в другом мире.");
            return true;
        }

        // Проверка роли целевого игрока (он также должен быть HUNTERS)
        if (roleManager.getRole(targetPlayer).filter(role -> role == Role.HUNTERS).isEmpty()) {
            player.sendMessage("Вы можете телепортироваться только к игрокам с ролью HUNTERS.");
            return true;
        }

        if (targetPlayer.isDead()) {
            player.sendMessage("Игрок мертв.");
            return true;
        }

        // Телепортируем игрока к целевому местоположению
        player.teleport(targetPlayer.getLocation());
        Bukkit.getServer().sendMessage(
                Component.text(player.getName() + " телепортировался к " + targetPlayer.getName())
                        .color(NamedTextColor.GOLD)
        );

        // Отмечаем использование команды в данной жизни
        usedInLife.add(playerId);
        return true;
    }

    /**
     * Метод, вызываемый при смерти игрока:
     * сбрасывает флаг использования команды и устанавливает кулдаун.
     *
     * @param playerId UUID игрока.
     */
    public static void handlePlayerDeath(UUID playerId) {
        if (usedInLife.remove(playerId)) {
            cooldowns.put(playerId, System.currentTimeMillis() + COOLDOWN_DURATION * 1000L);
        }
    }
}