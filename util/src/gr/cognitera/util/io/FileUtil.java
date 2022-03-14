package gr.cognitera.util.io;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import java.net.URL;
import java.net.URISyntaxException;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;



public final class FileUtil {

    private FileUtil() {
    }


    public static String slurpFile(final Path path, final Charset encoding) throws IOException {
        final byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }
    
    public static String slurpFile(final String path, final Charset encoding) throws IOException {
        return slurpFile(Paths.get(path), encoding);
    }

    public static String slurpFileUTF8(final String path) throws IOException {
        return slurpFile(path, StandardCharsets.UTF_8);
    }

    public static String slurpFileUTF8Relative(final Class klass, final String relativePath) throws IOException, URISyntaxException {
        final URL url = klass.getResource(relativePath);
        return StreamUtil.slurpInputStreamUTF8(url.openStream());
    }    


}
