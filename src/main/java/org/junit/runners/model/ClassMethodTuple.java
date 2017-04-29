package org.junit.runners.model;

import java.lang.reflect.Method;

/**
 * Created by akash on 4/4/17.
 */
public class ClassMethodTuple extends FrameworkMethod{
    private Class<?> suiteClass;
    private String key;

    public ClassMethodTuple(Class<?> klass, Method method, String key) {
        super(method);
        this.suiteClass = klass;
        this.key = key;
    }

    public static ClassMethodTuple fromAnnotation(String hashSeparatedString) throws InitializationError {
        try {
            String[] split = hashSeparatedString.split("#");
            Class<?> klass = Class.forName(split[0]);
            Method method = klass.getMethod(split[1]);
            ClassMethodTuple ret = new ClassMethodTuple(klass, method, klass.toString());
            return ret;
        } catch (ClassNotFoundException e) {
            throw new InitializationError(hashSeparatedString + " class not found");
        } catch (NoSuchMethodException e) {
            throw new InitializationError((hashSeparatedString + " method not found"));
        }
    }

    public Class<?> getSuiteClass() {
        return suiteClass;
    }

    public String getKey() {
        return key;
    }
}