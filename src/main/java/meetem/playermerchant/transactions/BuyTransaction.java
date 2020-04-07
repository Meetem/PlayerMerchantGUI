package meetem.playermerchant.transactions;

import meetem.playermerchant.LocaleKeys;
import meetem.playermerchant.MerchantOffer;
import meetem.playermerchant.MerchantOffersFilter;
import meetem.playermerchant.MerchantStorage;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.screen.MerchantBuyScreen;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BuyTransaction {
    private Player player;
    private MerchantBuyScreen buyScreen;

    public BuyTransaction(Player p){
        player = p;
    }

    public void start(){
        buyScreen = new MerchantBuyScreen(player, Localization.getLocalized(player, LocaleKeys.TitleBuy));

        MerchantOffersFilter filter = new MerchantOffersFilter(){
            @Override
            public ArrayList<MerchantOffer> filterOffers(ArrayList<MerchantOffer> offersList) {
                return super.filterOffers(offersList);
            }
        };

        buyScreen.setup(MerchantStorage.getInstance().getOffers(filter));
        buyScreen.show();
    }
}
