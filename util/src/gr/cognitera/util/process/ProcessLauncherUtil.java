package gr.cognitera.util.process;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import org.junit.Assert;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

import gr.cognitera.util.io.StreamUtil;


public final class ProcessLauncherUtil {
    
    final static Logger _logger = Logger.getLogger(ProcessLauncherUtil.class);
    private ProcessLauncherUtil() {}

    public static ProcessOutcome launch(final Environment environment
                                        , final File workingDir
                                        , final String program
                                        , final List<String> args) throws IOException, InterruptedException {
        return launch(_logger, environment, workingDir, program, args);
    }

    private static String preamble(final Logger logger) {
        if (logger==_logger)
            return "";
        else
            return String.format("(in %s) # "
                                 , ProcessLauncherUtil.class.getName());
    }

    public static ProcessOutcome launch(final Logger __logger
                                        , final Environment environment
                                        , final File workingDir
                                        , final String program
                                        , final List<String> args) throws IOException, InterruptedException {
        final Logger logger = __logger==null?_logger:__logger;
        Assert.assertNotNull(logger);
        final ProcessBuilder pb = new ProcessBuilder(args(program, args));
        pb.directory(workingDir);
        final Map<String, String> existingEnv = pb.environment();
        environment.applyTo(existingEnv);
        logger.trace(String.format("%sAbout to launch process with the following command: [%s] with working directory: [%s]"
                                   , preamble(logger)
                                   , Joiner.on(" ").join(pb.command())
                                   , workingDir));
        final Process p = pb.start();

        /* You must read both the std:out and the std:err streams concurently otherwise the process will block
         * (if either the std:out or the std:err emit copious amounts of output). For small volumes of std:out and std:err
         * outputs you don't need separate threads as the buffers are adequate but this utility class cannot know in
         * advance how much output an external program will generate on its std:out and std:error streams. As such,
         * it always creates an extra thread to read one of the two streams (the other stream is read by the thread that
         * invoked this method).
         */
        final StreamGobbler sg = new StreamGobbler(p.getErrorStream());
        final Thread sgT = new Thread(sg);
        sgT.start(); /* It is important that we first kick-off the second Thread (that gobbles up one of the streams - in this
                      * case std:err) before we begin the blocking read on the other stream.
                      */
        final String output = StreamUtil.slurpInputStreamUTF8(p.getInputStream()); // this really is the process' std:out stream (read the JavaDoc)
        sgT.join();
        if (sg.e!=null)
            throw sg.e;
        final String error  = sg.result;
        p.waitFor();
        final int exitValue = p.exitValue();
        return new ProcessOutcome(output, error, exitValue);
    }


    private static List<String> args(final String program, final List<String> args) {
        final List<String> rv = new ArrayList<>(args);
        rv.add(0, program);
        Assert.assertEquals(rv.size(), args.size()+1);
        return rv;
    }
}

class StreamGobbler implements Runnable {
    private final InputStream is;
    public String result;
    public IOException e;
    public StreamGobbler(final InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            result = StreamUtil.slurpInputStreamUTF8(is);
        } catch (IOException e) {
            this.e = e;
        }
    }
}

    
