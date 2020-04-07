package meetem.playermerchant.transactions;

import meetem.playermerchant.*;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.screen.MerchantSellOfferScreen;
import meetem.playermerchant.screen.MerchantSellPriceScreen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;

public class OfferTransaction {
    public static final int STATE_UNKNOWN = -2;
    public static final int STATE_CANCELED = -1;
    public static final int STATE_WAITING_RESULT = 0;
    public static final int STATE_WAITING_PRICE = 1;
    public static final int STATE_WAITING_COMPLETE = 2;
    public static final int STATE_COMPLETED = 3;
    public int getState(){
        return state;
    }

    private int state = STATE_UNKNOWN;
    private ArrayList<ItemStack> returnItems = null;
    private ArrayList<ItemStack> result = null;
    private ArrayList<ItemStack> price = null;

    private Player player;
    private MerchantSellOfferScreen offerScreen;
    private MerchantSellPriceScreen priceScreen;

    public OfferTransaction(Player p){
        this.player = p;
    }
    public boolean start(){
        if(state != STATE_UNKNOWN)
            return false;

        offerScreen = new MerchantSellOfferScreen(player, this, String.format("%s%sOffer", ChatColor.BOLD, ChatColor.BLACK));
        offerScreen.show();

        state = STATE_WAITING_RESULT;
        return true;
    }

    private ArrayList<ItemStack> getResultItem(ArrayList<ItemStack> items){
        ArrayList<ItemStack> result = new ArrayList<>();
        if(items == null)
            return result;

        for(ItemStack item : items){
            if(item == null
            || item.getType() == Material.AIR
            || item.getAmount() <= 0)
                continue;

            result.add(item);
            break;
        }

        return result;
    }

    public boolean setResult(ArrayList<ItemStack> result){
        if(state != STATE_WAITING_RESULT)
            return false;

        this.returnItems = result;
        this.result = getResultItem(result);
        if(this.result.size() <= 0)
            return false;

        state = STATE_WAITING_PRICE;
        return true;
    }

    public boolean goToPrice(){
        if(state != STATE_WAITING_PRICE)
            return false;

        closeOfferScreen();

        priceScreen = new MerchantSellPriceScreen(player, this, Localization.getLocalized(player, LocaleKeys.PriceFor, Util.prettyPrintItemPrice(this.result.get(0))));
        priceScreen.setPriceItems(new ArrayList<ItemStack>(Arrays.asList(Config.getConfig().priceItems)));
        priceScreen.show();
        return true;
    }

    public boolean setPrice(ArrayList<ItemStack> price){
        if(state != STATE_WAITING_PRICE)
            return false;

        this.price = price;
        if(this.price == null || this.price.size() <= 0)
            return false;

        this.state = STATE_WAITING_COMPLETE;
        return true;
    }

    public void complete() {
        if(state != STATE_WAITING_COMPLETE)
            return;

        closeOfferScreen();
        closePriceScreen();

        MerchantStorage.getInstance().addOffer(new MerchantOffer(
                player.getWorld().getEnvironment(),
                result,
                price,
                Bukkit.getOfflinePlayer(player.getUniqueId())
        ));

        MerchantStorage.getInstance().saveStorage(true);

        Localization.printLocalized(player, LocaleKeys.OfferCompleted,
                Util.prettyPrintItemPrice(result.get(0)),
                Util.prettyPrintItemPrice(this.price.get(0))
        );
        state = STATE_COMPLETED;
    }

    private void closePriceScreen(){
        if(priceScreen != null){
            priceScreen.closeUnhandled();
            priceScreen = null;
        }
    }

    private void closeOfferScreen(){
        if(offerScreen != null){
            offerScreen.closeUnhandled();
            offerScreen = null;
        }
    }

    public void cancel(){
        if(state < STATE_WAITING_RESULT)
            return;

        if(this.returnItems != null){
            for (ItemStack item : returnItems) {
                Util.giveItems(this.player, item, true);
            }
        }

        closeOfferScreen();
        closePriceScreen();
        state = STATE_CANCELED;
    }
}
