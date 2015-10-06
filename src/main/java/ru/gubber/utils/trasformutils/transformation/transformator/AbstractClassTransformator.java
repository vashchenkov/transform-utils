package ru.gubber.utils.trasformutils.transformation.transformator;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 8:35
 */
public abstract class AbstractClassTransformator<T, N> {

    public Map<String, Object> createContext(){
        return new HashMap<>();
    }

    public abstract T transform2TO(N orig) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    public T transform2TO(N orig, Map<String, Object> Context) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException{
        return transform2TO(orig);
    }
}