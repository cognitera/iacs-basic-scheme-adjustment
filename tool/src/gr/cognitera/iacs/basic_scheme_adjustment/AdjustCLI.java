package gr.cognitera.iacs.basic_scheme_adjustment;

import com.beust.jcommander.Parameter;

import gr.cognitera.util.cli.jcommander.CommandCLIHelper;
import gr.cognitera.util.cli.jcommander.ParameterValidation;
import gr.cognitera.util.cli.jcommander.ParameterValidationException;
import gr.cognitera.util.cli.jcommander.CommandInfo;

import gr.cognitera.util.base.Util;

public class AdjustCLI {

    public static enum COMMAND {
        DUMP_TABLE("dump-table");

        private String name;
        private COMMAND(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static COMMAND fromName(String name) {
            for (COMMAND command: COMMAND.values()) {
                if (command.getName().equals(name))
                    return command;
            }
            return null;
        }        
    }


    @Parameter(names = {"-h", "--help"}, description="print help and exit", required=false)
    public boolean help = false;

    @ParameterValidation
    public void validateParams() {
        /*
        if ((help && (config!=null)) || (config!=null && help))
            throw new ParameterValidationException("the help flag must be used alone");
        if ( (!help) && (config==null) )
            throw new ParameterValidationException("either the help flag or the config parameter must be used");
        */
        /*
        if (!Util.allAreNullOrNoneIsNull(config, username, password))
            throw new ParameterValidationException("config, username and password must either be all null or all non-null");
        */
    }
}
