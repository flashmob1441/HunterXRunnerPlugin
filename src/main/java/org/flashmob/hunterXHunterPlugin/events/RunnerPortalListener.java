package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.PortalTracker;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.Optional;

public class RunnerPortalListener implements Listener {
    private final RoleManager roleManager;

    public RunnerPortalListener(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        // Если игрок имеет роль RUNNERS, сохраняем позицию портала
        Optional<Role> roleOpt = roleManager.getRole(player);
        if (roleOpt.isPresent() && roleOpt.get() == Role.RUNNERS) {
            // event.getFrom() – локация портала (в мире, откуда игрок ушёл)
            PortalTracker.setPortalLocation(player.getUniqueId(), event.getFrom());
        }
    }
}
