package gr.cognitera.util.cli.jcommander;

import java.util.Map;
import java.util.LinkedHashMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

import gr.cognitera.util.rtti.RTTIUtil;


public final class CommandCLIHelper<T> {

    private String                programName;
    private Class<T>              mainArgClass;
    private Map<String, Class<?>> commandToCLIClass;
    
    public CommandCLIHelper(final String programName, final Class<T> mainArgClass, final Object ...commandsStrsAndCLIClasses) {
        this.programName  = programName;
        this.mainArgClass = mainArgClass;
        Assert.assertTrue(String.format("commandsStrsAndCLIClasses needs to be of even length (current length=%d)"
                                        , commandsStrsAndCLIClasses.length)
                          , commandsStrsAndCLIClasses.length % 2 == 0);


        this.commandToCLIClass = new LinkedHashMap<>();        
        for (int i = 0 ; i < commandsStrsAndCLIClasses.length / 2 ; i++) {
            String command = (String) commandsStrsAndCLIClasses[2*i  ];
            Class  klass   = (Class)  commandsStrsAndCLIClasses[2*i+1];
            Assert.assertNull (commandToCLIClass.put(command , klass));
        }
    }


    public CommandInfo<T> parse(final String args[]) {
        try {
            final T mainArg = mainArgClass.newInstance();

            final JCommander jc = new JCommander(mainArg);

            final Map<String, Object> commandToCLIObjs = new LinkedHashMap<>();
        
            for (String commandName: commandToCLIClass.keySet()) {
                final Object commandObj = commandToCLIClass.get(commandName).newInstance();
                Assert.assertNull(commandToCLIObjs.put(commandName, commandObj));
                jc.addCommand(commandName, commandObj);
            }
        
            jc.parse(args);
            String parsedCommand = jc.getParsedCommand();
            JCUtils.validateCLI(mainArg);
            Object commandObj = null;
            if (parsedCommand!=null) {
                commandObj = commandToCLIObjs.get(parsedCommand);  
                JCUtils.validateCLI(commandObj);
            }
            return new CommandInfo<T>(mainArg, parsedCommand, commandObj);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e); 
        }
    }

    
    public String usage() {
        try {
            final JCommander jc = new JCommander(mainArgClass.newInstance());
            jc.setProgramName(programName);
            for (String commandName: commandToCLIClass.keySet()) {
                final Object commandObj = commandToCLIClass.get(commandName).newInstance();
                jc.addCommand(commandName, commandObj);
            }        
            StringBuilder sb = new StringBuilder();
            jc.usage(sb);
            return sb.toString();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);             
        }
    }
}
 
