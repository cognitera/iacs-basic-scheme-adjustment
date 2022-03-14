package gr.cognitera.util.base;

import java.util.List;
import java.util.Arrays;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;



public class ClippedLoggingUtil {

    private ClippedLoggingUtil() {}

    private static List<Level> supportedLogLevels = Arrays.asList(new Level[]{Level.FATAL
                                                                              , Level.ERROR
                                                                              , Level.WARN
                                                                              , Level.INFO
                                                                              , Level.DEBUG
                                                                              , Level.TRACE
        });

    public static void fatal(final Logger logger
                            , final String context
                            , final String msg
                            , final int N) {
        logClipped(logger, Level.FATAL, context, msg, N, N);
    }

    public static void error(final Logger logger
                            , final String context
                            , final String msg
                            , final int N) {
        logClipped(logger, Level.ERROR, context, msg, N, N);
    }


    public static void warn(final Logger logger
                            , final String context
                            , final String msg
                            , final int N) {
        logClipped(logger, Level.WARN, context, msg, N, N);
    }

    public static void info(final Logger logger
                            , final String context
                            , final String msg
                            , final int N) {
        logClipped(logger, Level.INFO, context, msg, N, N);
    }

    public static void debug(final Logger logger
                             , final String context
                             , final String msg
                             , final int N) {
        logClipped(logger, Level.DEBUG, context, msg, N, N);
    }

    public static void trace(final Logger logger
                             , final String context
                             , final String msg
                             , final int N) {
        logClipped(logger, Level.TRACE, context, msg, N, N);
    }

    public static void logClipped(final Logger logger
                                  , final Level level
                                  , final String context
                                  , final String msg
                                  , final int N) {
        logClipped(logger, level, context, msg, N, N);
    }


    public static void logClipped(final Logger logger
                                  , final Level level
                                  , final String context
                                  , final String msg
                                  , final int numOfHeaderChars
                                  , final int numOfTrailerChars) {
        if (!supportedLogLevels.contains(level))
            throw new IllegalArgumentException(String.format("unsupported Level: [%s]"
                                                             , level));
        if (msg.length()<=numOfHeaderChars+numOfTrailerChars) {
            _log(logger, level, String.format("[%s] * entire message of length [%d] is: START-->%s<--END"
                                              , context
                                              , msg.length()
                                              , msg));
        } else {
            _log(logger, level, String.format("[%s] * message length is [%d]; first [%d] characters are: START->%s<-- END"
                                              , context
                                              , msg.length()
                                              , numOfHeaderChars
                                              , msg.substring(0, numOfHeaderChars)));

            _log(logger, level, String.format("[%s] * message length is [%d]; last [%d] characters are: START->%s<-- END"
                                              , context
                                              , msg.length()
                                              , numOfTrailerChars
                                              , msg.substring(msg.length()-numOfTrailerChars, msg.length())));
        }
    }

    private static void _log(final Logger logger, final Level level, final String msg) {
        if (level.equals(Level.FATAL))
            logger.fatal(msg);
        else if (level.equals(Level.ERROR))
            logger.error(msg);
        else if (level.equals(Level.WARN))
            logger.warn(msg);
        else if (level.equals(Level.INFO))
            logger.info(msg);
        else if (level.equals(Level.DEBUG))
            logger.debug(msg);
        else if (level.equals(Level.TRACE))
            logger.trace(msg);
        else throw new IllegalArgumentException(String.format("unsupported Level: [%s]"
                                                              , level));
    }

    private static String _beginning(final String s, final int n) {
        Assert.assertNotNull(s);
        Assert.assertTrue(n>=0);
        Assert.assertTrue(s.length() > n);
        return String.format("[beginning %d chars START]-->%s<--[beginning %d chars END]"
                             , n
                             , s.substring(0, n)
                             , n);
    }

    private static String _end(final String s, final int n) {
        Assert.assertNotNull(s);
        Assert.assertTrue(n>=0);
        Assert.assertTrue(s.length() > n);
        return String.format("[trailing %d chars START]-->%s<--[trailing %d chars END]"
                             , n
                             , s.substring(s.length()-n, s.length())
                             , n);
    }

    public static String clipIfTooLong(final String s, final int nbeg, final int nend) {
        return clipIfTooLong("string", s, nbeg, nend);
    }
    
    public static String clipIfTooLong(final String nameOfString, final String s, final int nbeg, final int nend) {
        final int maximumTotalLengthForFullDisplay = nbeg + nend;
        if (s.length()<=maximumTotalLengthForFullDisplay)
            return String.format("entire %s of length %d follows: [START]-->%s<--[END]"
                                 , nameOfString
                                 , s.length()
                                 , s);
        else
            return String.format("%s is of length %d and thus too long (by %d characters) to display here in full."
                                 +" Therefore, only the beginning and the end of the %s are given:"
                                 +" First [%d] characters of the %s are: %s."
                                 +" Last [%d] characters of the %s are: %s."
                                 , nameOfString
                                 , s.length()
                                 , s.length()-maximumTotalLengthForFullDisplay
                                 , nameOfString
                                 , nbeg, nameOfString, _beginning(s, nbeg)
                                 , nend, nameOfString, _end      (s, nend));
    }
}
