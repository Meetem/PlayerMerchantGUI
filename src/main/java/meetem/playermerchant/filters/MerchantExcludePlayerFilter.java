package meetem.playermerchant.filters;

import meetem.playermerchant.MerchantOffer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MerchantExcludePlayerFilter extends MerchantOffersFilter{
    private OfflinePlayer player;
    public MerchantExcludePlayerFilter(@NotNull OfflinePlayer player){
        this.player = player;
    }

    @Override
    public ArrayList<MerchantOffer> filterOffers(ArrayList<MerchantOffer> offersList) {
        ArrayList<MerchantOffer> output = new ArrayList<>();
        for(MerchantOffer offer : offersList){
            OfflinePlayer owner = offer.getOwner();
            if(owner == null)
                continue;

            if(owner.getUniqueId().equals(player.getUniqueId()))
                continue;

            output.add(offer);
        }

        return output;
    }
}
