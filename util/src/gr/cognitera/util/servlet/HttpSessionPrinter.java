package gr.cognitera.util.servlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.google.common.base.Joiner;

import javax.servlet.http.HttpSession;
    
public class HttpSessionPrinter {


    public static final String stringify(final HttpSession session, final boolean printAttributeNames) {
        String newStatus;
        try {
            newStatus = Boolean.toString(session.isNew());
        } catch (IllegalStateException e) {
            /*
             * When we moved from JBoss EAP 6.2 to JBoss EAP 7.1 it was discovered that the JBoss 7 Undertow servlet
             * container throws IllegalStateException when isNew() is called on an invalidated session. This makes
             * sense I guess. However, this is not what JBoss Web (the Tomcat-based JBoss 6 servlet container)
             * was doing --- hence this try-catch.
             *
             */
            newStatus = "can't be pronounced (it is likely that the session is already invalidated and"
                      + " you are running in an Undertow servlet container)";
        }
        return String.format("[id: %s, creation MSSE: %d, last accessed MSSE: %d, max inactive secs: %d, is new? %s%s]"
                             , session.getId()
                             , session.getCreationTime()
                             , session.getLastAccessedTime()
                             , session.getMaxInactiveInterval()
                             , newStatus
                             , printAttributeNames?String.format(" # Attributes: {%s}"
                                                                 , stringifyAttributeNames(session))
                             :"");
    }

    private static final String stringifyAttributeNames(final HttpSession session) {
        final List<String> rv = new ArrayList<>();
        for (String attr: Collections.list(session.getAttributeNames()))
            rv.add(String.format("%s=[%s]"
                                 , attr
                                 , session.getAttribute(attr)));
        return Joiner.on(", ").join(rv);
    }
}
