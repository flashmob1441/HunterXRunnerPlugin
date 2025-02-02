package org.flashmob.hunterXHunterPlugin.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;
import org.flashmob.hunterXHunterPlugin.utils.CompassTracker;
import org.jetbrains.annotations.NotNull;

public class GetCompass implements CommandExecutor {

    private final RoleManager roleManager;
    private final Plugin plugin;

    public GetCompass(RoleManager roleManager, Plugin plugin) {
        this.roleManager = roleManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        // Выдаем игроку компас
        ItemStack compassItem = new ItemStack(Material.COMPASS);
        player.getInventory().addItem(compassItem);

        // Если для игрока уже запущен трекер – останавливаем его
        if (CompassManager.hasTracker(player.getUniqueId())) {
            CompassManager.removeTracker(player.getUniqueId());
        }

        // Создаем новый трекер компаса для игрока
        CompassTracker tracker = new CompassTracker(player, roleManager, plugin);
        CompassManager.addTracker(player.getUniqueId(), tracker);

        player.sendMessage("Вы получили компас");
        return true;
    }
}
