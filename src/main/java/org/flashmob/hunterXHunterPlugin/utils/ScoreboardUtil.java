package org.flashmob.hunterXHunterPlugin.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardUtil {

    // Обновляет scoreboard для всех игроков, выводя строку с именем runner и оставшимся временем
    public static void updateRespawnTimer(String runnerName, int secondsRemaining) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        // Для каждого игрока создаём новый scoreboard с записями обратного отсчёта
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = manager.getNewScoreboard();
            // Объект, который будет отображаться в боковой панели (display name можно менять по вкусу)
            Objective objective = board.registerNewObjective("respawn", Criteria.DUMMY, Component.text("Респаун " + runnerName));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            // Создаём строку, где выводится оставшееся время
            Score score = objective.getScore("Осталось: " + secondsRemaining + " сек");
            score.setScore(1);

            player.setScoreboard(board);
        }
    }

    // Очищает scoreboard для всех игроков (например, при возрождении runner)
    public static void clearRespawnTimerForRunner(String runnerName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        // В этом простом случае создаём пустой scoreboard, тем самым сбрасывая предыдущие данные
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = manager.getNewScoreboard();
            player.setScoreboard(board);
        }
    }
}
