package meetem.playermerchant.screen;

import meetem.playermerchant.*;
import meetem.playermerchant.iconMenu.IconMenuBase;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.transactions.ManageTransaction;
import meetem.playermerchant.transactions.OfferTransaction;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MerchantManageScreen extends IconMenuBase {
    private ManageTransaction manage;
    private static final int NextPageSlot = 54 - 1;
    private static final int PrevPageSlot = NextPageSlot - 8;
    private static final int AddOfferSlot = PrevPageSlot + 1;
    private ArrayList<MerchantOffer> offerItems = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = (2 * 5);

    private int numPages() {
        return (int) Math.ceil(offerItems.size() / (double) pageSize);
    }

    public MerchantManageScreen(Player p, ManageTransaction manage, String title) {
        this(p, manage, 54, title);
    }

    private MerchantManageScreen(Player p, ManageTransaction manage, int size, String title) {
        super(p, size, title);
        this.manage = manage;
    }

    private ItemStack addLore(ItemStack item, String lore) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        List<String> loreList = meta.getLore();
        if (loreList == null)
            loreList = new ArrayList<>();

        loreList.add(0, lore);
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createRemoveButton(MerchantOffer offer) {
        ItemStack item = new ItemStack(Material.RED_CARPET);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        meta.setDisplayName(Localization.getLocalized(player, LocaleKeys.RemoveButton));
        meta.setLocalizedName(meta.getDisplayName());
        item.setItemMeta(meta);

        return ItemStackOffer.markItem(null, offer, item, false);
    }

    public void setOffers(ArrayList<MerchantOffer> offers) {
        this.offerItems.clear();
        this.offerItems.addAll(offers);

        setupPage(currentPage);
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

        setMenuItem(AddOfferSlot, Material.GREEN_CARPET,
                Localization.getLocalized(player, LocaleKeys.AddButton),
                Collections.singletonList(Localization.getLocalized(player, LocaleKeys.AddButtonLore)));
    }

    private void clear() {
        //Clear
        for (int i = 0; i < size; i++) {
            menuItems[i] = null;
            this.inventory.setItem(i, null);
        }
    }

    private void setupPage(int page) {
        if (this.offerItems.size() <= 0) {
            clear();
            updateButtons();
            return;
        }

        while (offerItems.size() <= (page * pageSize)) {
            page--;

            if (page < 0) {
                page = 0;
                break;
            }
        }

        page = Math.max(page, 0);

        clear();
        int y = 0;
        int x = 0;
        for (int i = 0; i < pageSize; i++) {
            int pIdx = (page * pageSize) + i;
            if (pIdx >= offerItems.size())
                break;

            MerchantOffer offer = offerItems.get(pIdx);
            if (offer == null)
                continue;

            List<ItemStack> result = offer.getResultItems();
            List<ItemStack> price = offer.getPriceItems();
            if (result == null || result.size() <= 0)
                continue;

            if (price == null || price.size() <= 0)
                continue;

            setMenuItem(x + y * 9, Material.WHITE_BANNER,
                    Localization.getLocalized(player, LocaleKeys.OfferAddPrice),
                    Collections.singletonList(Localization.getLocalized(player, LocaleKeys.OfferManageLore)));

            x++;
            setMenuItem(x + y * 9, addLore(result.get(0), Localization.getLocalized(player, LocaleKeys.OfferSingle)));
            x++;
            setMenuItem(x + y * 9, addLore(price.get(0), Localization.getLocalized(player, LocaleKeys.PriceSingle)));
            x++;
            setMenuItem(x + y * 9, createRemoveButton(offer));
            x++;

            //Add gap
            x += 1;
            if (x >= 9) {
                x = 0;
                y++;
            }
        }

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

    @Override
    protected boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem) {
        if (slot == AddOfferSlot) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            OfferTransaction transaction = new OfferTransaction(player);
            manage.cancel();
            transaction.start();
            return true;
        }

        if (menuItem == null)
            return true;

        MerchantOfferKey key = ItemStackOffer.getOfferKeyFromItem(menuItem);
        if (!key.isValid())
            return true;

        if (manage.remove(key)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            setOffers(MerchantStorage.getInstance().getOffersByPlayer(player.getUniqueId()));
        }

        return true;
    }

    @Override
    public void show() {
        super.show();
        setupPage(0);
    }

    private boolean skipCloseHandling = false;

    @Override
    protected void handleClose() {
        if (skipCloseHandling) {
            return;
        }

        System.out.println("Handling close");
        manage.cancel();
        skipCloseHandling = false;
    }

    @Override
    protected boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu) {
        InventoryAction action = eventData.getAction();
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return true;
        }

        //Clicked outside editing window
        if (slot > size) {
            return true;
        }

        //Prohibit bar and inventory swaps
        if (action == InventoryAction.SWAP_WITH_CURSOR) {
            return true;
        }

        return true;
    }

    public void closeUnhandled() {
        skipCloseHandling = true;

        if (this.myView != null)
            myView.close();
    }
}
