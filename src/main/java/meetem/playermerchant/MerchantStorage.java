package meetem.playermerchant;

import com.sun.istack.internal.NotNull;
import meetem.playermerchant.serialize.MerchantStorageRW;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class MerchantStorage {
    private static MerchantStorage _instance;
    private ArrayList<PlayerMerchantData> merchants = new ArrayList<>();
    private Plugin _plugin;

    private MerchantStorage(Plugin plugin){
        this._plugin = plugin;
    }

    private Path getMerchantsFolder(boolean createPath){
        File pluginFolder = _plugin.getDataFolder();
        File merchantsFolder = new File(pluginFolder, "Merchants");
        if(createPath && !merchantsFolder.exists())
            merchantsFolder.mkdirs();

        return Paths.get(merchantsFolder.getAbsolutePath());
    }

    private void loadStorage() throws IOException {
        merchants.clear();
        ArrayList<PlayerMerchantData> loadedMerchants = MerchantStorageRW.loadFiles(getMerchantsFolder(true));
        merchants.addAll(loadedMerchants);
    }

    public void saveStorage(boolean dirtyOnly){
        MerchantStorageRW.saveFiles(getMerchantsFolder(true), this.merchants, dirtyOnly);
    }

    private PlayerMerchantData findData(UUID ownerUUID){
        if(ownerUUID == null)
            return null;

        for (PlayerMerchantData fd : merchants) {
            if(fd.owner == null)
                Common.getLogger().severe("For fucks sake there is not owner of player data");

            if (fd.owner.getUniqueId().equals(ownerUUID)) {
                return fd;
            }
        }

        return null;
    }

    public ArrayList<MerchantOffer> getOffersByPlayer(@NotNull UUID playerId){
        PlayerMerchantData data = findData(playerId);
        ArrayList<MerchantOffer> offers = new ArrayList<>();

        if(data != null && data.offers != null){
            for(MerchantOffer offer : data.offers){
                if(offer == null || offer.isExpired())
                    continue;

                offers.add(offer);
            }
        }

        return offers;
    }

    public ArrayList<MerchantOffer> getOffers(@NotNull MerchantOffersFilter filter){
        ArrayList<MerchantOffer> allOffers = new ArrayList<>();
        for(PlayerMerchantData d : merchants){
            allOffers.addAll(d.offers);
        }

        return filter.filterOffers(allOffers);
    }

    public MerchantOffer getOffer(MerchantOfferKey key){
        if(key == null || !key.isValid())
            return null;

        UUID ownerId = key.getOwnerId();
        UUID offerId = key.getOfferId();

        if(ownerId != null){
            PlayerMerchantData data = findData(ownerId);
            if(data == null)
                return null;

            return data.getOfferReference(offerId);
        }

        for (PlayerMerchantData fd : merchants) {
            MerchantOffer offer = fd.getOfferReference(offerId);

            if (offer != null) {
                return offer;
            }
        }

        return null;
    }

    public boolean removeOffer(MerchantOfferKey key){
        MerchantOffer offer = getOffer(key);
        if(offer == null)
            return false;

        PlayerMerchantData merchantData = findData(offer.getOwner().getUniqueId());
        if(merchantData == null)
            return false;

        offer.setExpired(true);
        return merchantData.removeOffer(offer);
    }

    public boolean removeOffer(MerchantOffer offer, boolean compareUUID){
        if(offer.getOwner() == null)
            return false;

        PlayerMerchantData merchantData = findData(offer.getOwner().getUniqueId());
        if(merchantData == null)
            return false;

        MerchantOffer offerReference = merchantData.getOfferReference(offer, compareUUID);
        if(offerReference != null){
            offerReference.setExpired(true);
            return merchantData.removeOffer(offerReference);
        }

        return false;
    }

    public boolean addOffer(MerchantOffer offer){
        if(offer.getOwner() == null)
            return false;

        PlayerMerchantData merchantData = findData(offer.getOwner().getUniqueId());
        if(merchantData == null){
            merchantData = new PlayerMerchantData(offer.getOwner());
            merchants.add(merchantData);
        }

        offer.setExpired(false);
        merchantData.addOffer(offer);
        return true;
    }

    private void unloadStorage(){
        saveStorage(false);
    }

    static void enable(Plugin plugin) throws IOException {
        _instance = new MerchantStorage(plugin);
        _instance.loadStorage();
    }

    static void disable(){
        _instance.unloadStorage();
        _instance = null;
    }

    public static MerchantStorage getInstance(){
        return _instance;
    }
}
