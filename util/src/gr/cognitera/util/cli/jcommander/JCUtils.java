package gr.cognitera.util.cli.jcommander;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

import gr.cognitera.util.rtti.RTTIUtil;


public final class JCUtils {

    private JCUtils() {}

    public static void validateCLI(Object cli) {
        Class klass = cli.getClass();
        try {        
            List<Method> paramValidationMethods = RTTIUtil.getMethodsAnnotatedWith(klass, ParameterValidation.class);
            for (Method method: paramValidationMethods) {
                {
                    int modifiers = method.getModifiers();
                    Assert.assertTrue(parameterValMsg(method, klass, "It should be public.")
                                      , Modifier.isPublic(modifiers));

                    Assert.assertFalse(parameterValMsg(method, klass, "It shouldn't be static.")
                                      , Modifier.isStatic(modifiers));                    
                }
                {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    Assert.assertTrue(parameterValMsg(method
                                                      , klass
                                                      , String.format("It shouldn't expect any arguments. Yet %d arguments were expected: %s."
                                                                      , paramTypes.length
                                                                      , str(paramTypes)))
                                      , paramTypes.length==0);
                }
                {
                    Class<?> returnType   = method.getReturnType();
                    Assert.assertTrue(parameterValMsg(method
                                                      , klass
                                                      , String.format("Should have a return type of [%s], yet a return type of [%s] was found."
                                                                      , Void.TYPE
                                                                      , returnType.getName()))
                                      , returnType==Void.TYPE);
                }
                try {
                    method.invoke(cli);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t instanceof ParameterValidationException) {
                        throw (ParameterValidationException) t;
                    } else {
                        Assert.fail(String.format(parameterValMsg(method
                                                                  , klass
                                                                  , String.format("Therefore, this is a validation method. However, when it was invoked it threw a [%s] whose underlying cause was not an instance of [%s] as expected, but rather an instance of [%s]. The trace of the original [%s] is:%n%n%s%n%nThe trace of the %s is:%n%n%s%n"
                                                                                  , e.getClass().getName()
                                                                                  , ParameterValidationException.class.getName() 
                                                                                  , t.getClass().getName()
                                                                                  , e.getClass().getName()
                                                                                  , Throwables.getStackTraceAsString(e)
                                                                                  , t.getClass().getName()
                                                                                  , Throwables.getStackTraceAsString(t)))));
                    }
                }
            }
        } catch (IllegalAccessException  e) {
            throw new RuntimeException(e);
        }        
    }

    private static String parameterValMsg(Method method, Class klass, String msg) {
        return String.format("Method [%s] on class [%s] is annotated with the [%s] annotation. %s"
                             , method.getName()
                             , klass.getName()
                             , ParameterValidation.class.getName()
                             , msg);
    }

    private static String str(Class<?>[] klasses) {
        List<String> rv = new ArrayList<>();
        for (Class klass: klasses)
            rv.add(klass.getName());
        return Joiner.on(", ").join(rv);
    }
}
 
