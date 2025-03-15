package org.flashmob.hunterXHunterPlugin.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.ConfigKeys;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.utils.Utils;

import java.util.Optional;

public class RunnerWinListener implements Listener {
    private static final String VICTORY_ADVANCEMENT_KEY = "end/kill_dragon";
    private final RoleManager roleManager;
    private final Plugin plugin;

    public RunnerWinListener(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();

        // Проверяем, является ли игрок Runner
        Optional<Role> roleOpt = roleManager.getRole(player);
        if (roleOpt.isEmpty() || roleOpt.get() != Role.RUNNERS) {
            return;
        }

        // Сравнение ключа достижения.
        // Метод getKey() возвращает объект NamespacedKey, у которого можно получить непосредственно ключ достижения.
        if (event.getAdvancement().getKey().getKey().equalsIgnoreCase(VICTORY_ADVANCEMENT_KEY)) {
            // Объявляем победу Runners
            Server server = Bukkit.getServer();
            Utils.playSoundForAllPlayer(server, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            String winMessage = plugin.getConfig().getString(ConfigKeys.MESSAGE_RUNNERS_WIN, "Победили Спидранеры");
            server.sendTitlePart(TitlePart.TITLE, Component.text(winMessage));
            Utils.setGameStarted(false);
        }
    }
}
