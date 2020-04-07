package meetem.playermerchant.screen;

import meetem.playermerchant.Common;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SellItemStackUtil {
    private static ItemMeta getMetaSafe(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if(meta != null)
            return meta;

        return Bukkit.getItemFactory().getItemMeta(stack.getType());
    }

    /**
     * Assigning slot flag to given stack (not a clone)
     * @param stack
     * @return given stack
     */
    public static ItemStack assignShop(ItemStack stack){
        ItemMeta meta = getMetaSafe(stack);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Common.getSellItemKey(), PersistentDataType.INTEGER, 1);
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isShopItem(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return false;

        return meta.getPersistentDataContainer().has(Common.getSellItemKey(), PersistentDataType.INTEGER);
    }

    public static void removeShop(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.remove(Common.getSellItemKey());
        stack.setItemMeta(meta);
    }
}
