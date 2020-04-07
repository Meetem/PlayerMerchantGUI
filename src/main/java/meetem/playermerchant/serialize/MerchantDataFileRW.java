package meetem.playermerchant.serialize;

import meetem.playermerchant.PlayerMerchantData;
import meetem.playermerchant.PlayerMerchantDataPersistent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MerchantDataFileRW {
    static final byte[][] fileBuffer = {new byte[1024 * 1024]};
    private static int maxFileSize = 32 * 1024 * 1024;//32mb

    public static PlayerMerchantData readMerchant(File fp) throws IOException {
        if(fp.length() >= maxFileSize){
            throw new IOException(String.format("File is too big %f mB", fp.length() / (1024.0 * 1024.0)));
        }

        //Expand the buffer is there is not enough space
        int fileLen = (int)fp.length();
        FileInputStream fr = new FileInputStream(fp);
        if(fileBuffer[0] == null || fileLen >= fileBuffer[0].length){
            fileBuffer[0] = new byte[fileLen];
        }

        int readBytes = fr.read(fileBuffer[0], 0, fileLen);
        if(readBytes < 0)
            throw new IOException("Unexpected EOF, read() returned -1");

        fr.close();
        return readMerchant(fileBuffer[0], readBytes);
    }

    public static PlayerMerchantData readMerchant(byte[] fileBuffer, int readBytes) {
        PlayerMerchantDataPersistent dataType = new PlayerMerchantDataPersistent();
        return dataType.fromPrimitiveNC(fileBuffer);
    }

    public static void writeMerchant(OutputStream stream, PlayerMerchantData data) throws IOException {
        PlayerMerchantDataPersistent dataType = new PlayerMerchantDataPersistent();
        byte[] serialized = dataType.toPrimitiveNC(data);
        stream.write(serialized);
    }

    public static void clearBuffer(){
        fileBuffer[0] = null;
    }
}
