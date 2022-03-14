package gr.cognitera.iacs.basic_scheme_adjustment;

import com.beust.jcommander.Parameter;

import gr.cognitera.util.cli.jcommander.ParameterValidation;
import gr.cognitera.util.cli.jcommander.ParameterValidationException;


import gr.cognitera.util.base.Util;

public class DumpTableCLI {
    // parameter-less (for the time being)
    
    /*
    @Parameter(names = {"-u", "--username"}, description="username in the CXC application", required=true, arity=1)
    public String username;

    @Parameter(names = {"-p", "--password"}, description="password in the CXC application", required=true, arity=1)
    public String password;

    */

    @Parameter(names = {"-c", "--config"}, description="configuration file to use for DB access", required=true, arity=1) // 1 is the default arity for Strings anyway
    public String config;

    @ParameterValidation
    public void validateParams() {
    }
}
