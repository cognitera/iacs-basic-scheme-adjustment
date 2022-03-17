package gr.cognitera.iacs.basic_scheme_adjustment;


public enum Logger {
    INSTANCE;

    private Verbosity verbosity;
    public void setVerbosity(final Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    public void info(final String format, final Object... args) {
        if (verbosity.gte(Verbosity.INFO))
            System.out.printf(format, args);
    }

    public void debug(final String format, final Object... args) {
        if (verbosity.gte(Verbosity.DEBUG))
            System.out.printf(format, args);
    }

    public void trace(final String format, final Object... args) {
        if (verbosity.gte(Verbosity.DEBUG))
            System.out.printf(format, args);
    }
    


}
