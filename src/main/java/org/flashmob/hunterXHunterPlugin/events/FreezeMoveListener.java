package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.Optional;

public class FreezeMoveListener implements Listener {
    private final RoleManager roleManager;

    public FreezeMoveListener(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Optional<Role> roleOpt = roleManager.getRole(player);
        // Применяем блокировку только для игроков с ролью HUNTERS, находящихся в режиме Adventure
        if (roleOpt.isPresent() && roleOpt.get() == Role.HUNTERS && player.getGameMode() == GameMode.ADVENTURE) {
            Location from = event.getFrom();
            Location to = event.getTo();
            // Допускаем изменение координаты Y (прыжок), но запрещаем изменение X и Z (ходьбу)
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                event.setTo(from);
            }
        }
    }

}
