package meetem.playermerchant;

import meetem.playermerchant.serialize.MerchantOfferSerializer;
import meetem.playermerchant.serialize.SerializeVersion;
import meetem.playermerchant.serialize.SerializeVersionReadWriter;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;

public class PlayerMerchantDataPersistent implements PersistentDataType<byte[], PlayerMerchantData> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<PlayerMerchantData> getComplexType() {
        return PlayerMerchantData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull PlayerMerchantData merchantData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return toPrimitiveNC(merchantData);
    }

    public byte @NotNull [] toPrimitiveNC(@NotNull PlayerMerchantData merchantData) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            SerializeVersionReadWriter.writeVersion(stream, PlayerMerchantData.DataVersion);

            UUID ownerUUID = merchantData.owner.getUniqueId();
            stream.writeLong(ownerUUID.getMostSignificantBits());
            stream.writeLong(ownerUUID.getLeastSignificantBits());

            stream.writeInt(merchantData.offers.size());
            for (int i = 0; i < merchantData.offers.size(); i++) {
                MerchantOffer offer = merchantData.offers.get(i);
                if (offer == null)
                    continue;

                MerchantOfferSerializer.writeOffer(stream, offer);
            }

            stream.flush();
            stream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        return new byte[0];
    }

    @NotNull
    @Override
    public PlayerMerchantData fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return fromPrimitiveNC(bytes);
    }

    @NotNull
    public PlayerMerchantData fromPrimitiveNC(byte @NotNull [] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        PlayerMerchantData data = new PlayerMerchantData(null);

        try {
            ObjectInputStream stream = new ObjectInputStream(inputStream);
            SerializeVersion version = SerializeVersionReadWriter.readVersion(stream);

            long idMost = stream.readLong();
            long idLeast = stream.readLong();
            UUID ownerUUID = new UUID(idMost, idLeast);
            data.owner = Bukkit.getOfflinePlayer(ownerUUID);
            if(data.owner == null)
                throw new NullPointerException(String.format("Unable to find offline player %s", ownerUUID.toString()));

            int countOffers = stream.readInt();
            for (int i = 0; i < countOffers; i++) {
                MerchantOffer offer = MerchantOfferSerializer.readOffer(stream);
                offer.setOwner(data.owner);
                data.offers.add(offer);
            }

            inputStream.close();
        } catch (Exception e) {
            Common.printException(e);
        }

        return data;
    }
}
