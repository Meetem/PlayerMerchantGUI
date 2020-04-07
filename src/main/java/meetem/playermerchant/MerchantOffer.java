package meetem.playermerchant;

import meetem.playermerchant.serialize.SerializeVersion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class MerchantOffer {
    public final static SerializeVersion OfferVersion = new SerializeVersion(1, 0);
    private OfflinePlayer owner;
    private UUID offerId;
    private World.Environment fromEnvironment;
    private ArrayList<ItemStack> result;
    private ArrayList<ItemStack> price;
    private boolean _isExpired = false;

    public ArrayList<ItemStack> getResultItems() {return result;}
    public ArrayList<ItemStack> getPriceItems() {return price;}
    public World.Environment getEnvironment() {return fromEnvironment;}
    public UUID getOfferId() {return offerId;}
    public OfflinePlayer getOwner() {return owner;}
    public void setOwner(@NotNull OfflinePlayer owner) {this.owner = owner;}
    public boolean isExpired() {return _isExpired;}
    public void setExpired(boolean v) { _isExpired = v; }

    public MerchantOffer(World.Environment _env, ArrayList<ItemStack> _result, ArrayList<ItemStack> _price, OfflinePlayer _owner, UUID _id){
        this.fromEnvironment = _env;
        this.result = _result;
        this.price = _price;
        this.offerId = _id;
        this.owner = _owner;
    }

    public MerchantOffer(World.Environment env, ArrayList<ItemStack> offer, ArrayList<ItemStack> price, OfflinePlayer owner){
        this(env, offer, price, owner, UUID.randomUUID());
    }

    public boolean isFullyEquals(MerchantOffer offer, boolean compareUUID){
        if(offer == null)
            return false;

        if(compareUUID && !offer.getOfferId().equals(this.offerId))
            return false;

        if(!isPriceEquals(offer.price))
            return false;

        if(!isResultEquals(offer.result))
            return false;

        return true;
    }

    public boolean isPriceEquals(ArrayList<ItemStack> price){
        return ItemStackCompare.isArraysFullyEquals(this.price, price, true, true, true);
    }

    public boolean isResultEquals(ArrayList<ItemStack> result){
        return ItemStackCompare.isArraysFullyEquals(this.result, result, true, true, true);
    }
}
