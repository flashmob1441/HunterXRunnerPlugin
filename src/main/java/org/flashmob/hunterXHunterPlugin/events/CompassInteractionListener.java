package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassTracker;

public class CompassInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Обрабатываем правый клик (по воздуху или по блоку)
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
                Player player = event.getPlayer();
                CompassTracker tracker = CompassManager.getTracker(player.getUniqueId());
                if (tracker != null) {
                    tracker.nextTarget();
                }
            }
        }
    }
}
