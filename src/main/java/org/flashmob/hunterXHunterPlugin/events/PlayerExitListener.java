package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;

public class PlayerExitListener implements Listener {

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CompassManager.removeTracker(player.getUniqueId());
    }
}
