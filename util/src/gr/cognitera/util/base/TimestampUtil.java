package gr.cognitera.util.base;

import java.sql.Timestamp;

public class TimestampUtil {

    private static final int ONE_MILLION = 1000*1000;

    public static Timestamp now() {
        return create(Util.now());
    }
    
    public static Timestamp create(long mSSE) {
        return create(mSSE, 0);
    }
    
    public static Timestamp create(long mSSE, int millionthsOfMs) {
        Timestamp rv = new Timestamp(mSSE);
        int msecRemainder = Util.castLongToInt(mSSE%1000);
        rv.setNanos(msecRemainder*ONE_MILLION+millionthsOfMs);
        return rv;
    }

    public static int getMillionthsOfMs(Timestamp ts) {
        long ms = ts.getTime();
        int msRemainder = Util.castLongToInt(ms % 1000);
        return ts.getNanos()-msRemainder*ONE_MILLION;
    }
}

