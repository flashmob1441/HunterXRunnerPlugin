package org.flashmob.hunterXHunterPlugin.managers;

import org.flashmob.hunterXHunterPlugin.utils.CompassTracker;

import java.util.HashMap;
import java.util.UUID;

public class CompassManager {

    private static final HashMap<UUID, CompassTracker> trackerMap = new HashMap<>();

    public static void addTracker(UUID uuid, CompassTracker tracker) {
        trackerMap.put(uuid, tracker);
    }

    public static boolean hasTracker(UUID uuid) {
        return trackerMap.containsKey(uuid);
    }

    public static CompassTracker getTracker(UUID uuid) {
        return trackerMap.get(uuid);
    }

    public static void removeTracker(UUID uuid) {
        CompassTracker tracker = trackerMap.remove(uuid);
        if (tracker != null) {
            tracker.cancel();
        }
    }
}
