package gr.cognitera.util.rtti;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class RTTIUtil {

    private RTTIUtil() {}

    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterate upwards through inheritance hierarchy in order to retrieve methods from above the derived class
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                addMethodToListIfAnnotationIsPresent(method, annotation, methods);
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }


    @SuppressFBWarnings(value="DLS_DEAD_LOCAL_STORE"
                        , justification="In the future you may wish to extend this method with more custom logic. "
                        +"E.g. to allow it to inspect annotation parameters on the Annotation object. "
                        +"For the time being, the annotation object is ignored and all methods are returned. "
                        +"Still, it doesn't hurt if we explicitly type and showcase the getAnnotation method.")
    private static void addMethodToListIfAnnotationIsPresent(final Method method
                                                             , final Class<? extends Annotation> annotation
                                                             , final List<Method> methods) {
        if (method.isAnnotationPresent(annotation)) {
            final Annotation annotInstanceIgnored = method.getAnnotation(annotation);
            methods.add(method);
        }
    }
}
