package meetem.playermerchant.screen;

import meetem.playermerchant.LocaleKeys;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.transactions.OfferTransaction;
import meetem.playermerchant.iconMenu.IconMenuBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class MerchantSellOfferScreen extends IconMenuBase {
    private OfferTransaction transaction;
    private static final int ChestIconSlot = 0;
    private static final int NextIconSlot = 8;

    public MerchantSellOfferScreen(Player p, OfferTransaction transaction, String title) {
        this(p, transaction, 9, title);
    }

    private MerchantSellOfferScreen(Player p, OfferTransaction transaction, int size, String title) {
        super(p, size, title);
        this.transaction = transaction;

        setMenuItem(ChestIconSlot, Material.CHEST,
                Localization.getLocalized(player, LocaleKeys.OfferSingle),
                Collections.singletonList(Localization.getLocalized(player, LocaleKeys.OfferLore))
        );

        for (int i = ChestIconSlot + 2; i < NextIconSlot; i++) {
            setMenuItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<String>());
        }

        setMenuItem(NextIconSlot, Material.LIME_CARPET,
                Localization.getLocalized(player, LocaleKeys.NextButton),
                Collections.singletonList(Localization.getLocalized(player, LocaleKeys.ProceedToPrice))
        );
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST)
    protected void onInventoryClosed(InventoryCloseEvent event) {
        super.onInventoryClosed(event);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    protected void onItemClicked(InventoryClickEvent event) {
        super.onItemClicked(event);
    }

    @Override
    protected void handleClose() {
        if (skipCloseHandling) {
            return;
        }

        System.out.println("Handling close");
        transaction.setResult(this.getItems());
        transaction.cancel();
        skipCloseHandling = false;
    }

    private ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (menuItems[i] != null && menuItems[i].getType() != Material.AIR)
                continue;

            ItemStack stack = this.myView.getItem(i);
            if (stack == null || stack.getType() == Material.AIR)
                continue;

            items.add(stack);
        }

        return items;
    }

    @Override
    protected boolean handleMenuItemClick(InventoryClickEvent eventData, int slot, ItemStack menuItem) {
        if (slot != NextIconSlot)
            return true;

        if (!transaction.setResult(getItems())) {
            player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1f, 1f);
            transaction.cancel();
            Localization.printLocalized(player, LocaleKeys.NoOfferError);
            return true;
        }

        transaction.goToPrice();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        return true;
    }

    @Override
    protected boolean handleSlotClick(InventoryClickEvent eventData, int slot, boolean insideMenu) {
        return false;
    }

    protected boolean skipCloseHandling = false;

    public void closeUnhandled() {
        skipCloseHandling = true;

        if (this.myView != null)
            this.myView.close();
    }
}
