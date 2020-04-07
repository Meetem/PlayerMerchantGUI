package meetem.playermerchant;

import meetem.playermerchant.serialize.SerializeVersion;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerMerchantData {
    public final static SerializeVersion DataVersion = new SerializeVersion(1, 0);
    public OfflinePlayer owner;
    public ArrayList<MerchantOffer> offers = new ArrayList<>();
    private boolean _isDirty = true;
    public boolean isDirty() {return _isDirty;}

    public PlayerMerchantData(OfflinePlayer owner){
        this.owner = owner;
    }

    public MerchantOffer getOfferReference(UUID offerId){
        if(offerId == null)
            return null;

        for (MerchantOffer offer : offers) {
            if (offer.getOfferId().equals(offerId))
                return offer;
        }

        return null;
    }

    public MerchantOffer getOfferReference(MerchantOffer testAgainst, boolean compareUUID){
        if(testAgainst == null)
            return null;

        for (MerchantOffer offer : offers) {
            if (offer.isFullyEquals(testAgainst, compareUUID))
                return offer;
        }

        return null;
    }

    public void addOffer(MerchantOffer offer){
        if(offer == null)
            return;

        offers.add(offer);
        _isDirty = true;
    }

    public boolean removeOffer(MerchantOffer directReference){
        if(directReference == null)
            return false;

        if(offers.remove(directReference)){
            _isDirty = true;
            return true;
        }

        return false;
    }

    public void flush(){
        _isDirty = false;
    }
    public void clear(){
        offers.clear();
    }
}
