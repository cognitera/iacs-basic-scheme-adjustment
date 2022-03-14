package gr.cognitera.util.base;

public class MapificationConfig {

    public static enum DuplicateKeyHandling {
        DUPL_KEYS_NOT_ALLOWED
        , TOLERATE_DUPL_KEYS_ONLY_IF_VALUES_IDENTICAL
        , TOLERATE_DUPL_KEYS_LAST_VALUE_WINS;
    }

    public final String               chunkSeparator;
    public final String               keyValueSeparator;
    public final DuplicateKeyHandling duplicateKeyHandling;


    public MapificationConfig(final String               chunkSeparator,
                              final String               keyValueSeparator,
                              final DuplicateKeyHandling duplicateKeyHandling) {
        this.chunkSeparator       = chunkSeparator;
        this.keyValueSeparator    = keyValueSeparator;
        this.duplicateKeyHandling = duplicateKeyHandling;
    }
}
