package meetem.playermerchant.transactions;

import meetem.playermerchant.LocaleKeys;
import meetem.playermerchant.filters.CombinedOffersFilter;
import meetem.playermerchant.filters.MerchantExcludePlayerFilter;
import meetem.playermerchant.MerchantStorage;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.screen.MerchantBuyScreen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BuyTransaction {
    private Player player;
    private MerchantBuyScreen buyScreen;

    public BuyTransaction(Player p){
        player = p;
    }

    public void start(){
        buyScreen = new MerchantBuyScreen(player, Localization.getLocalized(player, LocaleKeys.TitleBuy));

        CombinedOffersFilter offersFilter = new CombinedOffersFilter(true);
        offersFilter.addFilter(new MerchantExcludePlayerFilter(Bukkit.getOfflinePlayer(player.getUniqueId())));

        buyScreen.setup(MerchantStorage.getInstance().getOffers(offersFilter));
        buyScreen.show();
    }
}
