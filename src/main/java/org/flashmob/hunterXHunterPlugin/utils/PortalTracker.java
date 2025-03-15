package org.flashmob.hunterXHunterPlugin.utils;

import org.bukkit.Location;
import org.bukkit.World;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalTracker {

    private static final Map<UUID, Map<String, Location>> runnerPortalLocations = new HashMap<>();

    /**
     * Сохраняет позицию портала для игрока в определённом мире.
     *
     * @param playerId UUID игрока
     * @param location Локация портала, с указанием мира
     */
    public static void setPortalLocation(UUID playerId, Location location) {
        // Получаем или создаём вложенную мапу для игрока
        Map<String, Location> worldLocations = runnerPortalLocations.computeIfAbsent(playerId, k -> new HashMap<>());
        // Ключ – имя мира
        String worldName = location.getWorld().getName();
        worldLocations.put(worldName, location);
    }

    /**
     * Возвращает сохраненную локацию портала для данного игрока в указанном мире.
     *
     * @param playerId UUID игрока
     * @param world    Мир, для которого необходимо получить координаты
     * @return локация портала или null, если координаты для данного мира отсутствуют
     */
    public static Location getPortalLocation(UUID playerId, World world) {
        Map<String, Location> worldLocations = runnerPortalLocations.get(playerId);
        if (worldLocations == null) {
            return null;
        }
        return worldLocations.get(world.getName());
    }

    public static void clearAll() {
        runnerPortalLocations.clear();
    }
}
