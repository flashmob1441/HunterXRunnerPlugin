package org.flashmob.hunterXHunterPlugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompassTracker {
    private final Player player; // охотник (hunter)
    private final RoleManager roleManager;
    private final Plugin plugin;
    private int targetIndex;
    private int taskId;

    public CompassTracker(Player player, RoleManager roleManager, Plugin plugin) {
        this.player = player;
        this.roleManager = roleManager;
        this.plugin = plugin;
        this.targetIndex = 0;
        startUpdating();
    }

    // Запускает повторяющуюся задачу обновления компаса (каждые 5 секунд)
    private void startUpdating() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::updateCompassTarget, 0L, 20L);
    }

    /**
     * Обновляет направление компаса. Если hunter и runner находятся в одном мире,
     * применяется стандартное поведение.
     * Если runner переместился в другой мир, то пытаемся получить точку портала,
     * через которую runner переместился, и установить её как target.
     */
    public void updateCompassTarget() {
        List<Player> validTargets = getValidTargets();

        if (validTargets.isEmpty()) {
            updateHeldCompassName("Нет спидранеров");
            return;
        }

        if (targetIndex >= validTargets.size()) {
            targetIndex = 0;
        }

        Player target = validTargets.get(targetIndex);

        if (player.getWorld().getEnvironment().equals(target.getWorld().getEnvironment())) {
            // Если hunter и runner находятся в одном мире, устанавливаем позицию runner.
            updateCompassLodestoneLoc(target.getLocation());
            updateHeldCompassName("Компас указывает на: " + target.getName());
        } else {
            // Если runner в другом мире, пытаемся получить сохранённую точку портала
            Location portalLocation = PortalTracker.getPortalLocation(target.getUniqueId(), player.getWorld());
            if (portalLocation != null && portalLocation.getWorld().getEnvironment().equals(player.getWorld().getEnvironment())) {
                updateCompassLodestoneLoc(portalLocation);
                updateHeldCompassName("Компас указывает на: " + target.getName() + " (портал)");
            } else {
                // Если сохранённой точки нет – просто обновляем название,
                // информируя, что runner находится в другом мире
                updateHeldCompassName("Компас указывает на: " + target.getName() + " (другой мир)");
            }
        }
    }

    /**
     * Переключает цель на следующего runner из списка.
     */
    public void nextTarget() {
        List<Player> validTargets = getValidTargets();

        if (validTargets.isEmpty()) {
            updateHeldCompassName("Нет спидранеров");
            return;
        }
        targetIndex = (targetIndex + 1) % validTargets.size();
        updateCompassTarget();
    }

    /**
     * Останавливает задачу обновления компаса (например, при отключении игрока).
     */
    public void cancel() {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    /**
     * Обновляет отображаемое имя компаса, который находится в руках игрока.
     *
     * @param newDisplayName новое имя для компаса
     */
    private void updateHeldCompassName(String newDisplayName) {
        ItemStack compass = getPlayerCompass();
        if (compass != null && !PlainTextComponentSerializer.plainText().serialize(compass.displayName()).equals("[" + newDisplayName + "]")) {
            setItemDisplayName(compass, newDisplayName);
        }
    }

    private void updateCompassLodestoneLoc(Location location) {
        ItemStack compass = getPlayerCompass();
        if (compass != null) {
            CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
            if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                if (compassMeta.isLodestoneCompass()) {
                    compassMeta.clearLodestone();
                    compass.setItemMeta(compassMeta);
                }
                player.setCompassTarget(location);
            } else {
                compassMeta.setLodestone(location);
                compassMeta.setLodestoneTracked(false);
                compass.setItemMeta(compassMeta);
            }
        }
    }

    private ItemStack getPlayerCompass() {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (CompassUtil.isCompass(plugin, mainHandItem)) {
            return mainHandItem;
        }
        // Если нет – пробуем off-hand
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (CompassUtil.isCompass(plugin, offHandItem)) {
            return offHandItem;
        }
        return null;
    }

    // Устанавливает новое отображаемое имя для предмета
    private void setItemDisplayName(ItemStack item, String displayName) {
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.displayName(Component.text(displayName));
        item.setItemMeta(meta);
    }

    private List<Player> getValidTargets() {
        List<Player> runners = roleManager.getPlayersInRole(Role.RUNNERS);
        List<Player> validTargets = new ArrayList<>();
        for (Player p : runners) {
            if (!p.equals(player) && !p.getGameMode().equals(GameMode.SPECTATOR)) {
                validTargets.add(p);
            }
        }
        return validTargets;
    }
}
