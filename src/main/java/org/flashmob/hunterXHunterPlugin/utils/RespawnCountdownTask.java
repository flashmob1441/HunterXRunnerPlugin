package org.flashmob.hunterXHunterPlugin.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnCountdownTask extends BukkitRunnable {

    private int timeLeft;
    private final String runnerName;

    public RespawnCountdownTask(String runnerName, int timeLeft) {
        this.runnerName = runnerName;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run() {
        if (timeLeft <= 0) {
            ScoreboardUtil.clearRespawnTimerForRunner(runnerName);
            cancel();
            return;
        }
        // Обновляем информацию в scoreboard для всех игроков
        ScoreboardUtil.updateRespawnTimer(runnerName, timeLeft);
        timeLeft--;
    }
}
