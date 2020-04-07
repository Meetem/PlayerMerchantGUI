package meetem.playermerchant.screen;

import meetem.playermerchant.iconMenu.IconMenuBase;
import meetem.playermerchant.iconMenu.IconMenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

class MerchantSellOfferScreenCustom extends IconMenuBase {
    public MerchantSellOfferScreenCustom(Player p, String title) {
        this(p, 9, title);
    }

    private MerchantSellOfferScreenCustom(Player p, int size, String title) {
        super(p, size, title);
        setMenuItem(0, Material.CHEST, "Selling:", Collections.singletonList("Put item you want to sell next to this chest"));
        setMenuItem(8, Material.LIME_STAINED_GLASS_PANE, "Next", Collections.singletonList("Proceed to price"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onInventoryClosed(InventoryCloseEvent event) {
        super.onInventoryClosed(event);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    protected void onItemClicked(InventoryClickEvent event) {
        super.onItemClicked(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onItemMoving(InventoryMoveItemEvent move) {
        move.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onItemDrag(InventoryDragEvent drag) {
        drag.setCancelled(true);
    }

    @Override
    protected boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem) {
        System.out.printf("Clicked menu %d\n", slot);
        return true;
    }

    /**
     * Clears ALL current slots which are not menu
     *
     * @param pickedSlot
     * @return Item from pickedSlot
     */
    private ItemStack clearOtherSlots(int pickedSlot) {
        ItemStack retItem = myView.getItem(pickedSlot);

        for (int i = 0; i < size; i++) {
            ItemStack menuStack = menuItems[i];
            if (menuStack != null && menuStack.getType() != Material.AIR)
                continue;

            myView.setItem(i, null);
        }

        return retItem;
    }

    private boolean moveToOtherInventory(InventoryDragData dragData, InventoryClickEvent eventData, int slot) {
        //if(dragData.isFromBar())
        return true;
    }

    private boolean isSlotInsideBar(int slot) {
        return slot < this.size;
    }

    private void putItemIntoBar(InventoryDragData currentDrag, InventoryClickEvent eventData, int slot) {
        ItemStack handItem = currentDrag.originalItemClone;
        if (handItem == null)
            return;

        ItemStack shopItem = SellItemStackUtil.assignShop(handItem.clone());
        ItemStack currentItem = clearOtherSlots(slot);
        InventoryAction putAction = eventData.getAction();
        eventData.setCursor(null);

        InventoryAction dragAction = currentDrag.action;

        //No items in slot or they are not stackable
        boolean createdNew = false;
        if (currentItem == null || !currentItem.isSimilar(shopItem)) {
            if (putAction == InventoryAction.PLACE_ONE) {
                shopItem.setAmount(1);
            }

            this.myView.setItem(slot, shopItem);
            createdNew = true;
        }

        if (!createdNew) {
            if (putAction == InventoryAction.PLACE_ONE)
                shopItem.setAmount(1);
            else if (currentDrag.action == InventoryAction.PICKUP_HALF)
                shopItem.setAmount(shopItem.getAmount() / 2);

            this.myView.setItem(slot, shopItem);
        }

        if (!currentDrag.isFromBar()) {
            this.myView.setItem(currentDrag.fromSlot, handItem);
        }
    }

    @Override
    protected void handleClose() {
        System.out.println("Inventory closed");
        super.handleClose();
    }

    private boolean placeItemFromBar(InventoryDragData currentDrag, InventoryClickEvent eventData, int slot) {
        //Bar -> Bar placement
        if (isSlotInsideBar(slot)) {
            putItemIntoBar(currentDrag, eventData, slot);
            return true;
        }

        //Bar -> Other placement
        eventData.setCursor(null);
        return true;
    }

    protected InventoryDragData currentDrag;

    @Override
    protected boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu) {
        ItemStack currentItem = eventData.getCurrentItem();
        ItemStack cursorItem = eventData.getCursor();
        System.out.printf("Handling click Slot: %d InsideMenu: %s CurrentItem: %s CursorItem: %s Type: %s\n",
                slot, Boolean.toString(insideMenu),
                (currentItem == null) ? "NULL" : currentItem.toString(),
                (cursorItem == null) ? "NULL" : cursorItem.toString(),
                eventData.getAction().toString()
        );

        InventoryAction action = eventData.getAction();
        boolean isPlace = IconMenuUtil.IsPlace(action);
        boolean isDrop = IconMenuUtil.IsDrop(action);
        boolean isPickup = IconMenuUtil.IsPickup(action);

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return moveToOtherInventory(currentDrag, eventData, slot);
        }

        //Prohibit bar and inventory swaps
        if (action == InventoryAction.SWAP_WITH_CURSOR) {
            if (currentDrag == null)
                return true;

            //Discard dragging
            if (currentDrag.isFromBar() && !isSlotInsideBar(slot)) {
                eventData.setCursor(null);
                return true;
            }

            //Just put it into bar
            if (!currentDrag.isFromBar() && isSlotInsideBar(slot)) {
                putItemIntoBar(currentDrag, eventData, slot);
                return true;
            }

            return currentDrag.isFromBar() != isSlotInsideBar(slot);
        }

        if (isPickup) {
            currentDrag = new InventoryDragData(this.size);
            currentDrag.fromSlot = slot;
            currentDrag.originalItem = currentItem;

            if (currentDrag.originalItem != null)
                currentDrag.originalItemClone = currentDrag.originalItem.clone();

            currentDrag.action = action;

            System.out.printf("New pickup: %d CurrentItem: %s Type: %s\n",
                    slot,
                    (currentDrag.originalItem == null) ? "NULL" : currentDrag.originalItem.toString(),
                    eventData.getAction().toString()
            );
            return false;
        }

        if (isDrop && currentDrag != null) {
            //Discard dropping loot from shop items
            if (currentDrag.isFromBar()) {
                eventData.setCursor(null);
                return true;
            }

            return false;
        }

        if (isPlace && currentDrag != null) {
            //InventoryDragData savedDrag = currentDrag;
            //currentDrag = null;
            if (currentDrag.isFromBar()) {
                return placeItemFromBar(currentDrag, eventData, slot);
            }

            if (!currentDrag.isFromBar() && isSlotInsideBar(slot)) {
                putItemIntoBar(currentDrag, eventData, slot);
                return true;
            }
        }

        return false;
    }
}
