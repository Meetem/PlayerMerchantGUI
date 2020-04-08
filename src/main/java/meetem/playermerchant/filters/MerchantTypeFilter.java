package meetem.playermerchant.filters;

import meetem.playermerchant.MerchantOffer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MerchantTypeFilter extends MerchantOffersFilter{
    Material type;
    public MerchantTypeFilter(Material type){
        this.type = type;
    }

    public Material getType(){
        return type;
    }

    public MerchantTypeFilter setType(Material type){
        this.type = type;
        return this;
    }

    @Override
    public ArrayList<MerchantOffer> filterOffers(ArrayList<MerchantOffer> offersList) {
        ArrayList<MerchantOffer> output = new ArrayList<MerchantOffer>();
        for(MerchantOffer offer : offersList){
            ArrayList<ItemStack> result = offer.getResultItems();
            if(result.size() <= 0)
                continue;

            if(result.get(0).getType() != type)
                continue;

            output.add(offer);
        }

        return output;
    }
}
