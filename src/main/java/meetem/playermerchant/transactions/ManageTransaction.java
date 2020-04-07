package meetem.playermerchant.transactions;

import meetem.playermerchant.LocaleKeys;
import meetem.playermerchant.MerchantOfferKey;
import meetem.playermerchant.MerchantStorage;
import meetem.playermerchant.locale.Localization;
import meetem.playermerchant.screen.MerchantManageScreen;
import org.bukkit.entity.Player;

public class ManageTransaction {
    private Player player;
    private MerchantManageScreen manageScreen;

    public ManageTransaction(Player p) {
        this.player = p;
    }

    public boolean start() {
        if (manageScreen != null)
            return false;

        manageScreen = new MerchantManageScreen(player, this, Localization.getLocalized(player, LocaleKeys.TitleManage));
        manageScreen.setOffers(MerchantStorage.getInstance().getOffersByPlayer(player.getUniqueId()));
        manageScreen.show();
        return true;
    }

    public boolean remove(MerchantOfferKey key) {
        return MerchantStorage.getInstance().removeOffer(key);
    }

    public void cancel() {
        if (manageScreen != null) {
            manageScreen.closeUnhandled();
            manageScreen = null;
        }
    }
}
