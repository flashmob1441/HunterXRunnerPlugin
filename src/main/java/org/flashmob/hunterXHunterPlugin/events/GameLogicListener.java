package org.flashmob.hunterXHunterPlugin.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.RespawnCountdownTask;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.utils.ScoreboardUtil;
import org.flashmob.hunterXHunterPlugin.utils.Utils;

import java.util.Optional;

public class GameLogicListener implements Listener {

    private final RoleManager roleManager;
    private final Plugin plugin;
    // Задержка до возрождения мертвого Runner (секунды)
    private final int RESPAWN_DELAY_SECONDS;

    public GameLogicListener(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        this.plugin = plugin;

        RESPAWN_DELAY_SECONDS = plugin.getConfig().getInt("runner_time_to_spectator_in_seconds");
    }

    @EventHandler
    public void onRunnerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getPlayer();
        Optional<Role> roleOpt = roleManager.getRole(deadPlayer);
        if (roleOpt.isEmpty() || roleOpt.get() != Role.RUNNERS || !Utils.isGameStarted()) {
            // Если это не Runner, выходим
            return;
        }

        // Запускаем задачу обратного отсчёта для обновления scoreboard
        new RespawnCountdownTask(deadPlayer.getName(), RESPAWN_DELAY_SECONDS).runTaskTimer(plugin, 0L, 20L);

        // Через 1 тик переводим игрока в режим Наблюдателя и назначаем объект наблюдения (если возможно)
        new BukkitRunnable() {
            @Override
            public void run() {
                deadPlayer.setGameMode(GameMode.SPECTATOR);
                // Пытаемся назначить в качестве цели наблюдения любого живого Runner (в режиме выживания)
                Player target = getAliveRunner(deadPlayer);
                if (target != null) {
                    deadPlayer.setSpectatorTarget(target);
                }
            }
        }.runTaskLater(plugin, 1L);

        // Через несколько тиков (2 тикa) проверяем, остались ли живые Runners – если нет, завершаем игру
        new BukkitRunnable() {
            @Override
            public void run() {
                checkGameEnd();
            }
        }.runTaskLater(plugin, 2L);

        // Запланировать возрождение мертвого Runner через заданное время, если игра ещё идет
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.isGameStarted()) {
                    deadPlayer.setGameMode(GameMode.SURVIVAL);
                    Player target = getAliveRunner(deadPlayer);
                    if (target != null) {
                        deadPlayer.teleport(target.getLocation());
                    }
                    ScoreboardUtil.clearRespawnTimerForRunner(deadPlayer.getName());
                    Utils.playSoundForAllPlayer(plugin.getServer(), Sound.ENTITY_VILLAGER_TRADE);
                }
            }
        }.runTaskLater(plugin, RESPAWN_DELAY_SECONDS * 20L);
    }

    // Метод для поиска живого Runner (в режиме выживания), отличного от только что умершего
    private Player getAliveRunner(Player excluded) {
        for (Player runner : roleManager.getPlayersInRole(Role.RUNNERS)) {
            if (!runner.equals(excluded) && runner.getGameMode() == GameMode.SURVIVAL) {
                return runner;
            }
        }
        return null;
    }

    // Проверка всех Runners: если ни один не находится в режиме выживания, завершаем игру
    private void checkGameEnd() {
        boolean allDead = true;
        for (Player runner : roleManager.getPlayersInRole(Role.RUNNERS)) {
            if (runner.getGameMode() == GameMode.SURVIVAL) {
                allDead = false;
                break;
            }
        }
        if (allDead && Utils.isGameStarted()) {
            Utils.setGameStarted(false);
            Server server = Bukkit.getServer();
            // Выводим сообщение о победе Hunters для всех игроков и проигрываем звук
            Utils.playSoundForAllPlayer(server, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            server.sendTitlePart(TitlePart.TITLE, Component.text("Победили Хантеры"));
        }
    }
}
