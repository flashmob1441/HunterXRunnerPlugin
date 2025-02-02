package org.flashmob.hunterXHunterPlugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.flashmob.hunterXHunterPlugin.utils.Role;

import java.util.*;

public class RoleManager {

    private final Map<UUID, Role> playerRoleMap = new HashMap<>();

    public void assignRole(Player player, Role team) {
        playerRoleMap.put(player.getUniqueId(), team);
    }

    public Optional<Role> getRole(Player player) {
        return Optional.ofNullable(playerRoleMap.get(player.getUniqueId()));
    }

    public List<Player> getPlayersInRole(Role team) {
        List<Player> playersInTeam = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (team.equals(playerRoleMap.get(player.getUniqueId()))) {
                playersInTeam.add(player);
            }
        }
        return playersInTeam;
    }
}
