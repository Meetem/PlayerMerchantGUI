package meetem.playermerchant.serialize;

public class SerializeVersion {
    private int major = 0;
    private int minor = 0;

    public int getMajor(){
        return major;
    }

    public int getMinor(){
        return minor;
    }

    public SerializeVersion(int major, int minor){
        this.major = major;
        this.minor = minor;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
