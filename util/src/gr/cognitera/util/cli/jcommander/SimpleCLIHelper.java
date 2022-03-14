package gr.cognitera.util.cli.jcommander;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

import gr.cognitera.util.rtti.RTTIUtil;


public final class SimpleCLIHelper<T> {

    private String   programName;
    private Class<T> klass;
    
    public SimpleCLIHelper(final String programName, final Class<T> klass) {
        this.programName = programName;
        this.klass       = klass;
    }

    public String usage() {
        try {
            final T cli = klass.getConstructor().newInstance();        
            final JCommander jc = new JCommander(cli);
            jc.setProgramName(programName);
            final StringBuilder sb = new StringBuilder();
            jc.usage(sb);
            return sb.toString();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    public T parse(final String args[]) {
        try {
            final T cli = klass.getConstructor().newInstance();
            final JCommander jc = new JCommander(cli);
            jc.parse(args);
            JCUtils.validateCLI(cli);
            return cli;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }                
    }
}
 
