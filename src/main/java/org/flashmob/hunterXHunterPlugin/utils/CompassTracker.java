package org.flashmob.hunterXHunterPlugin.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;

import java.util.ArrayList;
import java.util.List;

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
            updateHeldCompassName("Компас указывает на: Нет игроков");
            return;
        }

        if (targetIndex >= validTargets.size()) {
            targetIndex = 0;
        }

        Player target = validTargets.get(targetIndex);

        if (player.getWorld().equals(target.getWorld())) {
            // Если hunter и runner находятся в одном мире, устанавливаем позицию runner.
            player.setCompassTarget(target.getLocation());
            updateHeldCompassName("Компас указывает на: " + target.getName());
        } else {
            // Если runner в другом мире, пытаемся получить сохранённую точку портала
            Location portalLocation = PortalTracker.getPortalLocation(target.getUniqueId());
            if (portalLocation != null && portalLocation.getWorld().equals(player.getWorld())) {
                player.setCompassTarget(portalLocation);
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
            updateHeldCompassName("Компас указывает на: Нет игроков");
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
        // Сначала пробуем обновить компас, если он находится в основном слоте
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (isCompass(mainHandItem)) {
            setItemDisplayName(mainHandItem, newDisplayName);
            return;
        }
        // Если нет – пробуем off-hand
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (isCompass(offHandItem)) {
            setItemDisplayName(offHandItem, newDisplayName);
        }
    }

    // Проверка, что заданный ItemStack — компас
    private boolean isCompass(ItemStack item) {
        return item != null && item.getType() == Material.COMPASS;
    }

    // Устанавливает новое отображаемое имя для предмета
    private void setItemDisplayName(ItemStack item, String displayName) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(displayName));
            item.setItemMeta(meta);
        }
    }

    private List<Player> getValidTargets() {
        List<Player> runners = roleManager.getPlayersInRole(Role.RUNNERS);
        List<Player> validTargets = new ArrayList<>();
        for (Player p : runners) {
            if (!p.equals(player)) {
                validTargets.add(p);
            }
        }
        return validTargets;
    }
}
