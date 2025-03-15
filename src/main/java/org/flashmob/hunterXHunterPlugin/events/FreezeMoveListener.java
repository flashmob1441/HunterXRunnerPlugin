package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.utils.Utils;

import java.util.Optional;

public class FreezeMoveListener implements Listener {
    private final RoleManager roleManager;

    public FreezeMoveListener(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!Utils.isGameStarted()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Optional<Role> roleOpt = roleManager.getRole(player);
        // Применяем блокировку только для игроков с ролью HUNTERS, находящихся в режиме Adventure
        if (roleOpt.isPresent() && roleOpt.get() == Role.HUNTERS && player.getGameMode() == GameMode.ADVENTURE) {
            event.setTo(from);
        }
    }
}
