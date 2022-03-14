package gr.cognitera.util.net;

import java.net.URL;
import java.net.URLConnection;

import java.net.MalformedURLException;
import java.io.IOException;



public final class NetUtils {

    private NetUtils() {}

    public static boolean urlIsAccessible(final String urlS) {
        try {
            final URL url = new URL(urlS);
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
