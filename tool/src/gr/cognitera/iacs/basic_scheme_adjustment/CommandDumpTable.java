package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.ArrayList;

import java.io.IOException;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.apache.http.Header;



public class CommandDumpTable {
    final static Logger logger = Logger.getLogger(CommandDumpTable.class);

    public static void exec(final String configFPath) {
        logger.info(String.format("Configuration file is: [%s]\n", configFPath));
    }
}

