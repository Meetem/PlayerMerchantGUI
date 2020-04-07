package meetem.playermerchant;

import java.util.UUID;

public class MerchantOfferKey {
    private UUID owner;
    private UUID offer;

    public UUID getOwnerId(){
        return owner;
    }

    public UUID getOfferId(){
        return offer;
    }

    public boolean isValid(){
        return offer != null;
    }

    public static MerchantOfferKey empty(){
        return new MerchantOfferKey(null, null);
    }

    public MerchantOfferKey(UUID ownerId, UUID offerId){
        this.owner = ownerId;
        this.offer = offerId;
    }
}
