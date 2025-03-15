package org.flashmob.hunterXHunterPlugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.*;

public class RoleManager {

    private final Map<UUID, Role> playerRoleMap = new HashMap<>();
    private final Map<Role, List<Player>> rolePlayersCache = new EnumMap<>(Role.class);
    private boolean cacheDirty = true;

    public Optional<Role> getRole(Player player) {
        return Optional.ofNullable(playerRoleMap.get(player.getUniqueId()));
    }

    public List<Player> getPlayersInRole(Role role) {
        if (cacheDirty) {
            updateCache();
        }
        return rolePlayersCache.getOrDefault(role, Collections.emptyList());
    }

    private void updateCache() {
        rolePlayersCache.clear();
        for (Role role : Role.values()) {
            rolePlayersCache.put(role, new ArrayList<>());
        }

        for (Map.Entry<UUID, Role> entry : playerRoleMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                rolePlayersCache.get(entry.getValue()).add(player);
            }
        }
        cacheDirty = false;
    }

    public void assignRole(Player player, Role team) {
        playerRoleMap.put(player.getUniqueId(), team);
        cacheDirty = true; // Помечаем кэш как устаревший
    }
}
