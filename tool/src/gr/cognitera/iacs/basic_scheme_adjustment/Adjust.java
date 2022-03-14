package gr.cognitera.iacs.basic_scheme_adjustment;

import java.nio.charset.StandardCharsets;



import org.junit.Assert;

import com.beust.jcommander.ParameterException;

import gr.cognitera.util.cli.jcommander.CommandCLIHelper;
import gr.cognitera.util.cli.jcommander.ParameterValidationException;
import gr.cognitera.util.cli.jcommander.CommandInfo;

public class Adjust {

    public static void main(String args[]) {
        CommandCLIHelper<AdjustCLI> cliHelper = null;
        try {
            cliHelper = new CommandCLIHelper<>(AdjustCLI.class.getName()
                                               , AdjustCLI.class
                                               , AdjustCLI.COMMAND.DUMP_TABLE.getName(), DumpTableCLI.class);

            CommandInfo<AdjustCLI> cli = cliHelper.parse(args);
            if (cli.mainParams.help) {
                if (cli.commandName==null) {
                    System.out.printf("%s\n", cliHelper.usage());
                    System.exit(0);
                } else {
                    throw new ParameterValidationException(String.format("when the -h option is passed no command can be given (yet, here, [%s] was given)"
                                                                         , cli.commandName));
                }
            } else {
                if (cli.commandName!=null) {
                    AdjustCLI.COMMAND command = AdjustCLI.COMMAND.fromName(cli.commandName);
                    Assert.assertNotNull(String.format("you should expect to never see this message, if the command [%s] was not recognized, parsing of the command line arguments should have failed before this point"
                                                       , cli.commandName), command);
                    switch (command) {
                    case DUMP_TABLE: {
                        DumpTableCLI commandCLI = (DumpTableCLI) cli.commandParams;
                        CommandDumpTable.exec(commandCLI.config);
                        break;
                    }
                    default:
                        Assert.fail(String.format("Unhandled case [%s]", command));
                    }
                } else {
                    throw new ParameterValidationException("A command has to be provided when the '-h' flag is not set.");
                }
            }
        } catch (ParameterException | ParameterValidationException e) {
            e.printStackTrace();
            System.out.printf("%s\n", cliHelper.usage());
            System.exit(1);
        }
    }


}
