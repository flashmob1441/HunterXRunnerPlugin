package org.flashmob.hunterXHunterPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.flashmob.hunterXHunterPlugin.HunterXRunnerPlugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassUtil;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StartGame implements CommandExecutor {
    private final RoleManager roleManager;
    private final Plugin plugin;
    // Длительность обратного отсчёта (например, 10 секунд)
    private static int COUNTDOWN_SECONDS;

    public StartGame(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        this.plugin = plugin;

        COUNTDOWN_SECONDS = plugin.getConfig().getInt("start_timer_in_seconds");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Восстанавливаем здоровье, насыщение, очищаем инвентарь и сбрасываем достижения для всех игроков
        Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : allPlayers) {
            onlinePlayer.setHealth(Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
            onlinePlayer.setFoodLevel(20);
            onlinePlayer.setSaturation(5.0F);
            onlinePlayer.getInventory().clear();
            clearAdvancements(onlinePlayer);
        }

        player.getWorld().setTime(0L);

        // Для игроков с ролью HUNTERS задаём режим Adventure (заморозка)
        for (Player hunter : roleManager.getPlayersInRole(Role.HUNTERS)) {
            hunter.setGameMode(GameMode.ADVENTURE);
            CompassUtil.giveCompass(plugin, hunter);
        }

        Utils.setGameStarted(true);

        // Запускаем обратный отсчёт, который показывается всем игрокам в виде сообщения над хотбаром
        new CountdownTask(roleManager).runTaskTimer(plugin, 0L, 20L);
        return true;
    }

    // Метод для сброса выполненных ачивок (advancements) у игрока
    private void clearAdvancements(Player player) {
        Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();

        while (advancementIterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(advancementIterator.next());
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    // Внутренний класс обратного отсчёта
    private static class CountdownTask extends BukkitRunnable {
        private int secondsRemaining;
        private final RoleManager roleManager;

        public CountdownTask(RoleManager roleManager) {
            this.secondsRemaining = COUNTDOWN_SECONDS;
            this.roleManager = roleManager;
        }

        @Override
        public void run() {
            Server server = Bukkit.getServer();
            if (secondsRemaining > 0) {
                // Отправляем сообщение в action bar всем игрокам
                server.sendActionBar(Component.text("Игра начнется через " + secondsRemaining + " секунд").color(NamedTextColor.GOLD));
                Utils.playSoundForAllPlayer(server, Sound.BLOCK_NOTE_BLOCK_HAT);
                secondsRemaining--;
            } else {
                // Отсчёт завершён – для всех Hunters меняем игровой режим на Survival (разморозка)
                for (Player hunter : roleManager.getPlayersInRole(Role.HUNTERS)) {
                    hunter.setGameMode(GameMode.SURVIVAL);
                }
                // Выводим сообщение о старте игры всем игрокам
                server.sendActionBar(Component.text("Игра началась").color(NamedTextColor.GREEN));
                Utils.playSoundForAllPlayer(server, Sound.ENTITY_PLAYER_LEVELUP);
                this.cancel();
            }
        }
    }
}
