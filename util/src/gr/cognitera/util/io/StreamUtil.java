package gr.cognitera.util.io;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
    
import org.apache.commons.io.IOUtils;
    
public final class StreamUtil {

    private StreamUtil() {}

    public static String slurpInputStream(final InputStream is, final Charset charset) throws IOException {
        final StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, charset);
        return writer.toString();
    }

    public static String slurpInputStreamUTF8(final InputStream is) throws IOException {
        return slurpInputStream(is, StandardCharsets.UTF_8);
    }
    
}
