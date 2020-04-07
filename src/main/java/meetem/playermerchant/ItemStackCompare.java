package meetem.playermerchant;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemStackCompare {
    public static boolean isFullyEquals(ItemStack first, ItemStack second, boolean compareAmount, boolean compareMeta, boolean compareEnchantments){
        if(first == second)
            return true;

        if(first == null || second == null){
            return false;
        }

        if(first == null && second != null)
            return false;

        if(first != null && second == null)
            return false;

        if(!first.getType().equals(second.getType())){
            return false;
        }

        if(compareAmount && (first.getAmount() != second.getAmount()))
            return false;

        if(compareMeta && first.hasItemMeta() != second.hasItemMeta())
            return false;

        if(first.getDurability() != second.getDurability())
            return false;

        if(compareEnchantments && !first.getEnchantments().equals(second.getEnchantments()))
            return false;

        if(compareMeta && first.hasItemMeta() && !Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta()))
            return false;

        return true;
    }

    public static boolean isArraysFullyEquals(ArrayList<ItemStack> first, ArrayList<ItemStack> second, boolean compareAmount, boolean compareMeta, boolean compareEnchantments){
        int sz1 = (first == null) ? 0 : first.size();
        int sz2 = (second == null) ? 0 : second.size();

        //both empty arrays
        if(sz1 == sz2 && sz1 <= 0)
            return true;

        if(sz1 != sz2)
            return false;

        for(int i = 0;i<sz1;i++){
            ItemStack stack1 = first.get(i);
            ItemStack stack2 = second.get(i);

            if(!isFullyEquals(stack1, stack2, compareAmount, compareMeta, compareEnchantments))
                return false;
        }

        return true;
    }
}
