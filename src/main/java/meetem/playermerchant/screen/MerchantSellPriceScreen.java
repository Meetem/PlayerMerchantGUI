package meetem.playermerchant.screen;

import meetem.playermerchant.LocaleKeys;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.transactions.OfferTransaction;
import meetem.playermerchant.Util;
import meetem.playermerchant.iconMenu.IconMenuBase;
import meetem.playermerchant.iconMenu.IconMenuUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class MerchantSellPriceScreen extends IconMenuBase {
    private static final int ChestIconSlot = 0;
    private static final int NextIconSlot = 8;

    private static final int NextPageSlot = 54 - 1;
    private static final int PrevPageSlot = NextPageSlot - 8;
    private ArrayList<ItemStack> priceItems = new ArrayList<>();
    private int currentPage = 0;
    private int currentPageSize = 0;
    private int pageStart = 9;
    private int pageSize = 54 - pageStart - 9;

    private OfferTransaction transaction;

    public MerchantSellPriceScreen(Player p, OfferTransaction transaction, String title) {
        this(p, transaction, 54, title);
    }

    private MerchantSellPriceScreen(Player p, OfferTransaction transaction, int size, String title) {
        super(p, size, title);
        this.transaction = transaction;

        setMenuItem(ChestIconSlot, Material.GOLD_INGOT,
                Localization.getLocalized(player, LocaleKeys.PriceSingle),
                Collections.singletonList(Localization.getLocalized(player, LocaleKeys.PriceLore))
        );

        for (int i = ChestIconSlot + 2; i < NextIconSlot; i++) {
            setMenuItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<String>());
        }

        setMenuItem(NextIconSlot, Material.LIME_CARPET,  Localization.getLocalized(player, LocaleKeys.SellButton));
        updateButtons();
    }

    public void setPriceItems(ArrayList<ItemStack> prices) {
        priceItems.clear();
        priceItems.addAll(prices);

        setupPage(0);
    }

    private int numPages() {
        if(priceItems == null)
            return 0;

        return (int) Math.ceil(priceItems.size() / (double) pageSize);
    }

    private boolean hasPrevPage(int current) {
        int pages = numPages();
        if (pages <= 1)
            return false;

        return current > 0;
    }

    private boolean hasNextPage(int current) {
        int pages = numPages();
        return (current + 1) != pages && pages > 1;
    }

    private void updateButtons() {
        boolean hasPrev = hasPrevPage(currentPage);
        boolean hasNext = hasNextPage(currentPage);
        setMenuItem(PrevPageSlot, hasPrev ? Material.CYAN_CARPET : Material.BLACK_CARPET,
                Localization.getLocalized(player, hasPrev ? LocaleKeys.PrevPageActive : LocaleKeys.PrevPageNo),
                new ArrayList<String>()
        );

        setMenuItem(NextPageSlot, hasNext ? Material.CYAN_CARPET : Material.BLACK_CARPET,
                Localization.getLocalized(player, hasNext ? LocaleKeys.NextPageActive : LocaleKeys.NextPageNo),
                new ArrayList<String>()
        );
    }

    private void setupPage(int page) {
        ItemStack emptyItem = new ItemStack(Material.AIR);
        for (int i = pageStart; i < pageSize + 9; i++) {
            int pIdx = (i - pageStart) + (page * pageSize);
            ItemStack setItem = emptyItem;
            if (pIdx < priceItems.size()) {
                setItem = priceItems.get(pIdx).clone();
            }

            this.inventory.setItem(i, setItem);
        }

        currentPageSize = Math.min(Math.max(priceItems.size() - page * pageSize, 0), pageSize);
        currentPage = page;
        updateButtons();
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

    private ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> priceItems = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (menuItems[i] != null && menuItems[i].getType() != Material.AIR)
                continue;

            ItemStack item = this.myView.getItem(i);
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0)
                continue;

            priceItems.add(item);
            break;
        }

        return priceItems;
    }

    @Override
    protected boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem) {
        if (slot == NextPageSlot) {
            if (hasNextPage(currentPage)) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                setupPage(currentPage + 1);
            }
        } else if (slot == PrevPageSlot) {
            if (hasPrevPage(currentPage)) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                setupPage(currentPage - 1);
            }
        } else if (slot == NextIconSlot) {
            if (!transaction.setPrice(getItems())) {
                player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                transaction.cancel();
                Localization.printLocalized(player, LocaleKeys.NoPriceError);
                return true;
            } else {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                transaction.complete();
            }
        }

        return true;
    }

    @Override
    public void show() {
        super.show();
        setupPage(0);
    }

    private boolean isSlotInsideBar(int slot) {
        return slot < this.size;
    }

    private boolean skipCloseHandling = false;

    @Override
    protected void handleClose() {
        if (skipCloseHandling) {
            return;
        }

        System.out.println("Handling close");
        transaction.cancel();
        skipCloseHandling = false;
    }

    private InventoryDragData currentDrag;

    @Override
    protected boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu) {
        ItemStack currentItem = eventData.getCurrentItem();
        ItemStack cursorItem = eventData.getCursor();

        /*
        System.out.printf("Handling click Slot: %d InsideMenu: %s CurrentItem: %s CursorItem: %s Type: %s\n",
                slot, Boolean.toString(insideMenu),
                (currentItem == null) ? "NULL" : currentItem.toString(),
                (cursorItem == null) ? "NULL" : cursorItem.toString(),
                eventData.getAction().toString()
        );
        */

        InventoryAction action = eventData.getAction();
        boolean isPlace = IconMenuUtil.IsPlace(action);
        boolean isDrop = IconMenuUtil.IsDrop(action);
        boolean isPickup = IconMenuUtil.IsPickup(action);

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return true;
        }

        //Clicked on page black blocks
        if (slot >= currentPageSize + 9) {
            return true;
        }

        //Clocked on bottom bar
        if (slot > (size - 9)) {
            return true;
        }

        if (currentDrag != null && currentDrag.isFromBar() && isDrop) {
            eventData.setCursor(null);
            setupPage(currentPage);
            return true;
        }

        //Discard any player inventory moving
        if (isPickup && !isSlotInsideBar(slot)) {
            currentDrag = null;
            return true;
        }

        if (currentDrag != null && currentDrag.isFromBar() && !isSlotInsideBar(slot)) {
            return true;
        }

        //Prohibit bar and inventory swaps
        if (action == InventoryAction.SWAP_WITH_CURSOR) {
            if (currentDrag == null)
                return true;

            return currentDrag.isFromBar() != isSlotInsideBar(slot);
        }

        if (isPickup) {
            currentDrag = new InventoryDragData(this.size);
            currentDrag.fromSlot = slot;
            currentDrag.originalItem = currentItem;

            if (currentDrag.originalItem != null)
                currentDrag.originalItemClone = currentDrag.originalItem.clone();

            currentDrag.action = action;

            /*
            System.out.printf("New pickup: %d CurrentItem: %s Type: %s\n",
                    slot,
                    (currentDrag.originalItem == null) ? "NULL" : currentDrag.originalItem.toString(),
                    eventData.getAction().toString()
            );
            */

            return false;
        }

        return false;
    }

    public void closeUnhandled() {
        skipCloseHandling = true;

        if (this.myView != null)
            myView.close();
    }
}
