package gr.cognitera.util.crypto;



public enum SecretKeyFactoryAlgorithm {

    PBKDF2WithHmacSHA1("PBKDF2WithHmacSHA1")
    , PBKDF2WithHmacSHA256("PBKDF2WithHmacSHA256") // available in Java 1.8
    , PBKDF2WithHmacSHA512("PBKDF2WithHmacSHA512") // available in Java 1.8
    ;

    private String name;
    public String getName() {
        return this.name;
    }

    private SecretKeyFactoryAlgorithm(String name) {
        this.name = name;
    }

    public static SecretKeyFactoryAlgorithm fromName(String name, boolean tolerateNull) {
        for (SecretKeyFactoryAlgorithm x: SecretKeyFactoryAlgorithm.values()) {
            if (x.getName().equals(name))
                return x;
        }
        if (tolerateNull)
            return null;
        else
            throw new IllegalArgumentException(String.format("Unrecognized name: [%s]"
                                                             , name));
    }
}

