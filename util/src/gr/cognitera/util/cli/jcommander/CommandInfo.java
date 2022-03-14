package gr.cognitera.util.cli.jcommander;

import org.junit.Assert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
                    justification="used from other packages")    
public class CommandInfo<T> {

    public T      mainParams;
    public String commandName;
    public Object commandParams;

    public CommandInfo(T mainParams, String commandName, Object commandParams) {
        Assert.assertTrue( ((commandName==null) && (commandParams==null)) ||
                           ((commandName!=null) && (commandParams!=null)) );
        this.mainParams    = mainParams;
        this.commandName   = commandName;
        this.commandParams = commandParams;
    }
}
