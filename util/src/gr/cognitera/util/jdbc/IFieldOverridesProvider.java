package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Clob;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import gr.cognitera.util.base.Util;

public interface IFieldOverridesProvider {

    Map<String, CustomFieldReader<?>> provideFieldOverridesBasedOnAssumedTypology(Class<?> klass);

}
