package meetem.playermerchant.iconMenu;

import meetem.playermerchant.Common;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class IconMenuBase implements Listener {
    protected Inventory inventory;
    protected InventoryView myView;
    protected ItemStack[] menuItems;
    protected int size;
    protected IconMenuHandler menuHandler;
    protected String title;
    protected Player player;

    public IconMenuBase(Player player, int size, String title) {
        this.size = size;
        this.title = title;
        this.player = player;

        menuItems = new ItemStack[size];
        inventory = Bukkit.createInventory(null, size, title);
    }

    public IconMenuHandler getMenuHandler() {
        return menuHandler;
    }

    public void setMenuHandler(IconMenuHandler handler) {
        this.menuHandler = handler;
    }

    public int getSize() {
        return size;
    }

    public ItemStack[] getMenuItems() {
        return menuItems;
    }

    public InventoryView getView() {
        return myView;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public void show() {
        myView = player.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, Common.getPlugin());
    }

    public IconMenuBase setMenuItem(int slot, ItemStack menuItem) {
        inventory.setItem(slot, menuItem);
        menuItems[slot] = menuItem;
        return this;
    }

    public IconMenuBase setMenuItem(int slot, Material material, String title) {
        return setMenuItem(slot, material, title, null);
    }

    public IconMenuBase setMenuItem(int slot, Material material, String title, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setLocalizedName(title);
        meta.setDisplayName(title);

        if (lore != null)
            meta.setLore(lore);

        item.setItemMeta(meta);
        return setMenuItem(slot, item);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onInventoryClosed(InventoryCloseEvent event) {
        if (event.getView() != myView)
            return;

        HandlerList.unregisterAll(this);
        handleClose();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onItemClicked(InventoryClickEvent event) {
        if (event.getView() != myView)
            return;

        InventoryAction action = event.getAction();
        boolean isPickup = action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME;

        int slot = event.getRawSlot();
        boolean insideMenu = slot >= 0 && slot < this.size;
        if (insideMenu) {
            ItemStack stack = event.getView().getItem(slot);
            ItemStack foundMenuItem = menuItems[slot];

            if (foundMenuItem != null && stack.equals(foundMenuItem)) {
                event.setCancelled(handleMenuItemClick(event, slot, foundMenuItem));
                return;
            }
        }

        event.setCancelled(handleSlotClick(event, slot, insideMenu));
    }

    /**
     * Called when clicked Item which was not set in menu.
     * It still may be inside menu bar
     *
     * @param eventData
     * @param slot
     * @param insideMenu
     * @return true (default) if you want to cancel further click event handling
     */
    protected boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu) {
        return menuHandler.handleSlotClick(eventData, slot, insideMenu);
    }

    /**
     * Called when player clicked on menu item,
     * ONLY called if menu item was set by setMenuItem()
     * ELSE handleSlotClick is called
     *
     * @param eventData
     * @param slot
     * @param menuItem
     * @return true (default) if you want to cancel further click event handling
     */
    protected boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem) {
        return menuHandler.handleMenuItemClick(eventData, slot, menuItem);
    }

    protected void handleClose() {
        System.out.println("Closed icon menu");
    }
}
