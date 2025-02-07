package org.flashmob.hunterXHunterPlugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassTracker;
import org.flashmob.hunterXHunterPlugin.utils.CompassUtil;

public class CompassInteractionListener implements Listener {

    private final Plugin plugin;

    public CompassInteractionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Если предмет отсутствует или это не компас, выходим
        ItemStack item = event.getItem();
        if (item == null || CompassUtil.isCompass(plugin, item)) {
            return;
        }

        Player player = event.getPlayer();
        CompassTracker tracker = CompassManager.getTracker(player.getUniqueId());
        if (tracker == null) {
            return;
        }

        // Переключаем цель компаса на следующего игрока
        tracker.nextTarget();
    }
}
