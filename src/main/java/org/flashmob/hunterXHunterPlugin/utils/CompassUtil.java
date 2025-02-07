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

import java.util.Objects;

public class CompassUtil {

    public static void giveCompass(Plugin plugin, Player player) {
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

    public static boolean isCompass(Plugin plugin, ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "item-identifier");
        return data.has(key, PersistentDataType.STRING) && Objects.requireNonNull(data.get(key, PersistentDataType.STRING)).equalsIgnoreCase("runner-tracker");
    }
}
