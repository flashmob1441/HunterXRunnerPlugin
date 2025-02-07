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

import java.util.*;

public class MoveTo implements CommandExecutor {

    private final RoleManager roleManager;
    // Содержит время окончания кулдауна для каждого игрока (UUID -> timestamp в мс)
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    // Хранит UUID игроков, которые использовали команду в текущей жизни
    private static final Set<UUID> usedInLife = new HashSet<>();
    // Длительность кулдауна после смерти (например, 30 секунд)
    private static int COOLDOWN_DURATION = 1;

    public MoveTo(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;

        COOLDOWN_DURATION = plugin.getConfig().getInt("teleport_cooldown_in_seconds");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Команду могут использовать только игроки
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }
        UUID playerId = player.getUniqueId();

        // Проверяем, что у игрока роль HUNTERS
        Optional<Role> playerRole = roleManager.getRole(player);
        if (playerRole.isEmpty() || playerRole.get() != Role.HUNTERS) {
            player.sendMessage("Эту команду могут использовать только игроки с ролью HUNTERS.");
            return true;
        }

        // Если игрок находится на кулдауне, проверяем оставшееся время
        if (cooldowns.containsKey(playerId)) {
            long remaining = cooldowns.get(playerId) - System.currentTimeMillis();
            if (remaining > 0) {
                player.sendMessage("Команда недоступна. Повторное использование возможно через " + (remaining / 1000) + " секунд.");
                return true;
            } else {
                cooldowns.remove(playerId);
            }
        }

        // Если игрок уже использовал команду в этой жизни, запрещаем повторное использование
        if (usedInLife.contains(playerId)) {
            player.sendMessage("Вы уже использовали команду в этой жизни. Дождитесь смерти и завершения кулдауна.");
            return true;
        }

        // Проверяем наличие аргумента – имени целевого игрока
        if (args.length < 1) {
            player.sendMessage("Использование: /moveto <имя_игрока>");
            return true;
        }

        String targetName = args[0];

        // Ищем целевого игрока (точное совпадение имени)
        Player targetPlayer = Bukkit.getPlayerExact(targetName);
        if (targetPlayer == null) {
            player.sendMessage("Игрок \"" + targetName + "\" не найден или не в онлайне.");
            return true;
        }

        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("Самому себе нельзя телепортироваться.");
            return true;
        }

        if (!player.getWorld().getEnvironment().equals(targetPlayer.getWorld().getEnvironment())) {
            player.sendMessage("Игрок в другом мире.");
            return true;
        }

        // Проверяем, что целевой игрок имеет роль HUNTERS
        Optional<Role> targetRole = roleManager.getRole(targetPlayer);
        if (targetRole.isEmpty() || targetRole.get() != Role.HUNTERS) {
            player.sendMessage("Вы можете телепортироваться только к игрокам с ролью HUNTERS.");
            return true;
        }

        if (targetPlayer.isDead()) {
            player.sendMessage("Игрок мертв.");
            return true;
        }

        // Телепортируем игрока к целевой локации и сообщаем об успехе
        player.teleport(targetPlayer.getLocation());
        player.getServer().sendMessage(Component
                .text(player.getName() + " телепортировался к " + targetPlayer.getName())
                .color(NamedTextColor.GOLD)
        );

        // Отмечаем, что команда уже использована в этой жизни
        usedInLife.add(playerId);
        return true;
    }

    /**
     * Метод, вызываемый при смерти игрока.
     * Сбрасывает флаг использования команды и устанавливает кулдаун.
     *
     * @param playerId UUID игрока.
     */
    public static void handlePlayerDeath(UUID playerId) {
        if (usedInLife.contains(playerId)) {
            usedInLife.remove(playerId);
            cooldowns.put(playerId, System.currentTimeMillis() + COOLDOWN_DURATION * 1000L);
        }
    }
}
