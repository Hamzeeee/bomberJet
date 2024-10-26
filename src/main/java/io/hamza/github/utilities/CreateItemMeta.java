package io.hamza.github.utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CreateItemMeta {

    public ItemStack createItem(Material material, String displayName, List<String> lore, boolean enchantmentGlintOverride) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.setEnchantmentGlintOverride(enchantmentGlintOverride);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
