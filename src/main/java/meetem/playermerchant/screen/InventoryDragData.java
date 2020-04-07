package meetem.playermerchant.screen;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class InventoryDragData {
    private int barSize;
    public InventoryDragData(int barSize){
        this.barSize = barSize;
    }

    public int fromSlot;
    public ItemStack originalItem;
    public ItemStack originalItemClone;
    public InventoryAction action;

    public boolean isFromBar(){
        return fromSlot < barSize;
    }
}
