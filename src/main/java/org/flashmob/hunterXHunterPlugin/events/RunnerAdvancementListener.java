package org.flashmob.hunterXHunterPlugin.events;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.Optional;

public class RunnerAdvancementListener implements Listener {
    private static final String VICTORY_ADVANCEMENT_KEY = "end/kill_dragon";
    private final RoleManager roleManager;

    public RunnerAdvancementListener(RoleManager roleManager) {
        this.roleManager = roleManager;
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
        player.sendMessage(event.getAdvancement().getKey().getKey());
        if (event.getAdvancement().getKey().getKey().equalsIgnoreCase(VICTORY_ADVANCEMENT_KEY)) {
            // Объявляем победу Runners
            Server server = Bukkit.getServer();
            server.playSound(Sound.sound(Key.key("minecraft:ui_toast_challenge_complete"), Sound.Source.PLAYER, 1.0f, 1.0f));
            server.sendTitlePart(TitlePart.TITLE, Component.text("Победили Runners"));
        }
    }
}
