package org.flashmob.hunterXHunterPlugin.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.flashmob.hunterXHunterPlugin.managers.CompassManager;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;

public class CompassUtil {

    private final Plugin plugin;

    public CompassUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    public void giveCompass(Player player) {
        // Выдаем игроку компас
        ItemStack compassItem = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compassItem.getItemMeta();
        compassMeta.setLodestoneTracked(false);
        compassMeta.addEnchant(Enchantment.LURE, 1, true);
        compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        compassMeta.displayName(Component.text("Компас на спидранера"));
        PersistentDataContainer data = compassMeta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "item-identifier"), PersistentDataType.STRING, "runner-tracker");
        compassItem.setItemMeta(compassMeta);
        player.getInventory().addItem(compassItem);
    }
}
