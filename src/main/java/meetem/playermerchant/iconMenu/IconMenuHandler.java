package meetem.playermerchant.iconMenu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class IconMenuHandler {
    protected IconMenuBase menu;
    public IconMenuHandler(IconMenuBase menu){
        this.menu = menu;
    }

    /**
     * Called when clicked Item which was not set in menu.
     * It still may be inside menu bar
     * @param eventData
     * @param slot
     * @param insideMenu
     * @return true (default) if you want to cancel further click event handling
     */
    public abstract boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu);

    /**
     * Called when player clicked on menu item,
     * ONLY called if menu item was set by setMenuItem()
     * ELSE handleSlotClick is called
     * @param eventData
     * @param slot
     * @param menuItem
     * @return true (default) if you want to cancel further click event handling
     */
    public abstract boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem);
}
