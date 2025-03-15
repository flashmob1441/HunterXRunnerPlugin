package org.flashmob.hunterXHunterPlugin.utils;

import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Utils {

    private static boolean gameStarted = false;
    private static boolean gameStarting = false;

    public static boolean isGameStarted() {
        return gameStarted;
    }

    public static boolean isGameStarting() {
        return gameStarting;
    }

    public static void setGameStarted(boolean gameStarted) {
        Utils.gameStarted = gameStarted;
    }

    public static void setGameStarting(boolean gameStarting) {
        Utils.gameStarting = gameStarting;
    }

    public static void playSoundForAllPlayer(Server server, Sound sound) {
        for (Player player : server.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }
}
