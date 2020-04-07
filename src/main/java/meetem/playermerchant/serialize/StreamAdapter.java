package meetem.playermerchant.serialize;

import meetem.playermerchant.Common;

import java.io.*;

public class StreamAdapter {
    public static ObjectOutputStream adapt(OutputStream stream){
        if(stream == null)
            return null;

        if(stream instanceof ObjectOutputStream)
            return (ObjectOutputStream)stream;

        try {
            return new ObjectOutputStream(stream);
        } catch (IOException e) {
            Common.printException(e);
            return null;
        }
    }

    public static ObjectInputStream adapt(InputStream stream){
        if(stream == null)
            return null;

        if(stream instanceof ObjectInputStream)
            return (ObjectInputStream)stream;

        try {
            return new ObjectInputStream(stream);
        } catch (IOException e) {
            Common.printException(e);
            return null;
        }
    }
}
