package org.flashmob.hunterXHunterPlugin.utils;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalTracker {
    private static final Map<UUID, Location> runnerPortalLocations = new HashMap<>();

    // Сохраняет позицию портала (откуда runner переместился)
    public static void setPortalLocation(UUID playerId, Location location) {
        runnerPortalLocations.put(playerId, location);
    }

    // Возвращает сохранённую позицию для заданного runner'а
    public static Location getPortalLocation(UUID playerId) {
        return runnerPortalLocations.get(playerId);
    }
}
