package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassTracker;
import org.flashmob.hunterXHunterPlugin.utils.Role;
import org.flashmob.hunterXHunterPlugin.utils.Utils;

public class PlayerJoinListener implements Listener {

    private final RoleManager roleManager;
    private final Plugin plugin;

    public PlayerJoinListener(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Если для игрока уже запущен трекер – останавливаем его
        if (CompassManager.hasTracker(player.getUniqueId())) {
            CompassManager.removeTracker(player.getUniqueId());
        }

        // Создаем новый трекер компаса для игрока
        CompassTracker tracker = new CompassTracker(player, roleManager, plugin);
        CompassManager.addTracker(player.getUniqueId(), tracker);

        if (Utils.isGameStarting() && roleManager.getRole(player).filter(role -> role == Role.HUNTERS).isPresent()) {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }
}
