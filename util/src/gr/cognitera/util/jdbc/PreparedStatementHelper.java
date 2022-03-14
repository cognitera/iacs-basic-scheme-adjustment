package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Clob;

import java.util.Map;
import java.util.HashMap;

import java.lang.reflect.Field;

import gr.cognitera.util.base.Util;
import gr.cognitera.util.time.ISO8601Util;
import java.sql.Date;

public final class PreparedStatementHelper {

    private PreparedStatementHelper() {
    }

    public static void setInteger(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value!=null)
            ps.setInt(idx, value);
        else
            ps.setNull(idx, Types.INTEGER);
    }

    public static void setStringAsChar(PreparedStatement ps, int idx, String value) throws SQLException {
        if (value!=null) {
            if (value.length()>1)
                throw new PreparedStatementHelperException(String.format("String is meant to be used as a CHAR parameter, yet has a length of [%d]"
                                                                         , value.length()));
            ps.setString(idx, value);
        } else
            ps.setNull(idx, Types.CHAR);
    }

    // This is another Sybase atrocity. For the gory details see cont-1505252859
    public static void setStringWithHackAgainstSybaseTreatmentOfEmptyStrings(PreparedStatement ps, int idx, String value) throws SQLException {
        if ((value!=null) && (!"".equals(value))) {
            ps.setString(idx, value);
        } else {
            ps.setNull(idx, Types.VARCHAR);
        }
    }
        
    public static void setLong(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value!=null)
            ps.setLong(idx, value);
        else
            ps.setNull(idx, Types.BIGINT);
    }

    public static void setDouble(PreparedStatement ps, int idx, Double value) throws SQLException {
        if (value!=null)
            ps.setDouble(idx, value);
        else
            ps.setNull(idx, Types.DOUBLE);
    }

    public static void setBoolean(PreparedStatement ps, int idx, Boolean value) throws SQLException {
        if (value!=null)
            ps.setBoolean(idx, value);
        else
            ps.setNull(idx, Types.BOOLEAN);
    }

    public static void setBooleanAsString(final PreparedStatement ps, int idx, Boolean value, final String valueForTrue, final String valueForFalse) throws SQLException {
        if (value!=null)
            setBoolAsString(ps, idx, value, valueForTrue, valueForFalse);
        else {
            ps.setNull(idx, Types.VARCHAR);
            /*
I used to have a bug here: I used to set the type to Types.BOOLEAN which is obviously wrong. I don't comment on fixed bugs but this one in particular 
was interesting as it emitted a misleading (from the client's point of view) error message in Sybase perhaps related to some unknown Terror lurking still
somewhere deep in Sybase (Sybase does, after all, report in the trace that "this is internal")

Specifically, upon execution of the PreparedStatement, I would get (in Sybase):

---%<-----------------------------------
IOException: java.io.IOException: JZ0SL: Unsupported SQL type 16.
at com.sybase.jdbc4.jdbc.ErrorMessage.raiseError(ErrorMessage.java:755)
at com.sybase.jdbc4.jdbc.ErrorMessage.raiseErrorCheckDead(ErrorMessage.java:1168)
at com.sybase.jdbc4.tds.Tds.handleIOE(Tds.java:5106)
at com.sybase.jdbc4.tds.Tds.handleIOE(Tds.java:5051)
at com.sybase.jdbc4.tds.Tds.rpc(Tds.java:1142)
at com.sybase.jdbc4.jdbc.SybCallableStatement.sendRpc(SybCallableStatement.java:1710)
at com.sybase.jdbc4.jdbc.SybCallableStatement.execute(SybCallableStatement.java:214)
at org.jboss.jca.adapters.jdbc.CachedPreparedStatement.execute(CachedPreparedStatement.java:297)
at org.jboss.jca.adapters.jdbc.WrappedPreparedStatement.execute(WrappedPreparedStatement.java:404
----------------------------------->%---

... moreover, in the catch block (on the DB rollback) I would get the following (also in Sybase):

---%<-----------------------------------
com.sybase.jdbc4.jdbc.SybSQLException: A wrong datastream has been sent to the server. The server was expecting token 32 but got the token 33. This is an internal error.

at com.sybase.jdbc4.tds.Tds.processEed(Tds.java:4004)
at com.sybase.jdbc4.tds.Tds.nextResult(Tds.java:3094)
at com.sybase.jdbc4.jdbc.ResultGetter.nextResult(ResultGetter.java:78)
at com.sybase.jdbc4.jdbc.SybStatement.nextResult(SybStatement.java:289)
at com.sybase.jdbc4.jdbc.SybStatement.nextResult(SybStatement.java:271)
at com.sybase.jdbc4.jdbc.SybStatement.updateLoop(SybStatement.java:2514)
at com.sybase.jdbc4.jdbc.SybStatement.executeUpdate(SybStatement.java:2498)
at com.sybase.jdbc4.jdbc.SybPreparedStatement.executeUpdate(SybPreparedStatement.java:300)
at com.sybase.jdbc4.tds.Tds.setOption(Tds.java:1881)
at com.sybase.jdbc4.jdbc.SybConnection.setAutoCommit(SybConnection.java:1630)
at org.jboss.jca.adapters.jdbc.BaseWrapperManagedConnection.checkTransaction(BaseWrapperManagedConnection.java:886)
at org.jboss.jca.adapters.jdbc.WrappedConnection.checkTransaction(WrappedConnection.java:1594)
at org.jboss.jca.adapters.jdbc.WrappedConnection.rollback(WrappedConnection.java:774)
at org.apache.commons.dbutils.DbUtils.rollback(DbUtils.java:297) [commons-dbutils-1.6.jar:1.6]
at gr.cognitera.util.jdbc.AbstractDAL.rollback(AbstractDAL.java:51) [jutil-1.0.0.jar:]
----------------------------------->%---

To complicate matters further, this latter exception (on the rollback) was the only exception trace that reached the client.
The initial exception (which is quite straightforward and not misleading at all) that triggered everything was only logged in the server logs.
This set me off on a wild goose chase (until I examined the server logs as well) looking at things like:

    https://groups.google.com/forum/#!topic/mybatis-user/FUS966kaVc0

... where some poor wretch wrote (at the end):

    "This is Sybase specific. If you pass a null as a parameter it needs to know it's type. Use the #parm:jdbctype# for the nullable args. Drove us nuts here too, but it's sybases funky exception that throws you off. 
    -Bryan"

But that was all misleading. The bug was simply that I had to do:
            ps.setNull(idx, Types.VARCHAR);
... and not:
            ps.setNull(idx, Types.BOOLEAN);

             */
        }
    }

    public static void setBoolAsString(final PreparedStatement ps, int idx, boolean value, final String valueForTrue, final String valueForFalse) throws SQLException {
        if (value)
            ps.setString(idx, valueForTrue);
        else
            ps.setString(idx, valueForFalse);
    }


    public static void setBooleanAsChar(final PreparedStatement ps, int idx, Boolean value, final char valueForTrue, final char valueForFalse) throws SQLException {
        if (value!=null)
            setBoolAsChar(ps, idx, value, valueForTrue, valueForFalse);
        else {
            ps.setNull(idx, Types.CHAR);
        }
    }
    
    public static void setBoolAsChar(final PreparedStatement ps, int idx, boolean value, final char valueForTrue, final char valueForFalse) throws SQLException {
        if (value)
            ps.setString(idx, Character.toString(valueForTrue));
        else
            ps.setString(idx, Character.toString(valueForFalse));
    }

    public static void setISO8601YYYMMDDAsDate(final PreparedStatement ps, int idx, String yyyymmdd) throws SQLException {
        final Date d = (yyyymmdd==null)?null:Date.valueOf(ISO8601Util.parseYYYYMMDD(yyyymmdd));
        ps.setDate(idx, d);
    }    


}

// cont-1505252859
/*
source: http://www.dbforums.com/showthread.php?1005533-Empty-string-to-be-converted-to-a-single-space

----------------------------------------
I'm using Sybase ASE 12.5.2 (both windows and unix environment) for my development.
Encountered one situation that in need of expert's advises on this... Here's the situation.
When I pass a empty string from my VB program to perform INSERT or EDIT stmt, Sybase will automatically convert it to a single space, something like " " and to be stored in that particular column...

My doubts were:
1. Is this a norm for Sybase?
2. If I want to set the Sybase to convert it to NULL instead of " ", how can I do it?

Looks forward for any helps available. TQ...

----------------------------------------
Yes this is the case, and this is silly. From what I recall this is because ASE stores NULL strings as an empty string (!) "" instead of an actual NULL, so in order to differentiate between really NULL and just an empty string, it pads an empty string with a space.

Two things you could do:
have your application explicitly insert NULL (which is kind of what it should do since the empty string is not equivalent to NULL)
put an insert/update trigger on that table (or tables) which intercepts the empty string and converts it to NULL

Neither are particularly great
Thanks,

Matt

----------------------------------------
Default that argument to NULL in SP definition . If you don't want to pass that value do not include that argument in the VB calling code. Sybase will automatically use the NULL value.

~Ratheesh

----------------------------------------
Refering to 
http://manuals.sybase.com/onlinebook...190;pt=16190#X

It saids that...

"Adaptive Server truncates entries to the specified column length without warning or error, unless you set string_rtruncation on. See the set command in the Reference Manual for more information. The empty string, ""or '', is stored as a single space rather than as NULL. Thus, "abc" + "" + "def" is equivalent to "abc def", not to "abcdef"."

Will this setting able to solve my problem? If so, how and where to perform this SET STRING_RTRUNCTION ON ?

Thank you...

----------------------------------------
You execute:
1> SET STRING_RTRUNCATION ON
2> GO

However if you go to the SET section of the REFERENCE MANUAL you see the following:
string_rtruncation
determines whether Adaptive Server raises a SQLSTATE exception when an insert or update command truncates a char, unichar, varchar or univarchar string. If the truncated characters consist only of spaces, no exception is raised. The default setting, off, does not raise the SQLSTATE exception, and the character string is silently truncated.
So I'm not sure that solves your problem. If you set the attribute to be "NULL"-able and then insert a NULL instead of a single space you might be OK. 

I really hate this behavior of ASE but I don't know if anyone at Sybase has really looked into "fixing" it.
Thanks,

Matt


 */
