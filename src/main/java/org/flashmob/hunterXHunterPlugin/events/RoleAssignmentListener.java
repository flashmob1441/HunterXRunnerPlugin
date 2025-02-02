package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.Optional;

public class RoleAssignmentListener implements Listener {

    private final RoleManager roleManager;

    public RoleAssignmentListener(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Если у игрока ещё не назначена роль, назначаем роль HUNTERS по умолчанию
        Optional<Role> currentRole = roleManager.getRole(player);
        if (currentRole.isEmpty()) {
            roleManager.assignRole(player, Role.HUNTERS);
        }
    }
}
