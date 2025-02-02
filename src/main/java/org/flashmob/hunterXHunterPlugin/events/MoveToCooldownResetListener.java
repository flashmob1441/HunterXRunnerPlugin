package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.flashmob.hunterXHunterPlugin.commands.MoveTo;

import java.util.UUID;

public class MoveToCooldownResetListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        MoveTo.handlePlayerDeath(playerId);
    }
}
