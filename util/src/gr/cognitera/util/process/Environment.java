package gr.cognitera.util.process;

import java.util.Set;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;


public final class Environment {

    final boolean clearExisting;
    final Set<String> varsToRemove;
    final Map<String, String> varsToAddOrOverwrite;
    
    public Environment(final boolean clearExisting
                       , final Set<String> varsToRemove
                       , final Map<String, String> varsToAddOrOverwrite) {
        this.clearExisting = clearExisting;
        this.varsToRemove = varsToRemove;
        this.varsToAddOrOverwrite = varsToAddOrOverwrite;
        if (clearExisting && !varsToRemove.isEmpty())
            throw new IllegalArgumentException();
    }


    public void applyTo(final Map<String, String> existingEnv) {
        if (clearExisting)
            existingEnv.clear();
        for (final String varToRemove: varsToRemove) {
            existingEnv.remove(varToRemove);
        }
        existingEnv.putAll(varsToAddOrOverwrite);                   
    }

    public static Environment existing() {
        return new Environment(false
                               , new LinkedHashSet<String>()
                               , new LinkedHashMap<String, String>());
    }

}
