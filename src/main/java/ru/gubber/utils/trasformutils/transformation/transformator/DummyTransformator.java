package ru.gubber.utils.trasformutils.transformation.transformator;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:39
 */
public class DummyTransformator extends ATransformator {

    public DummyTransformator(String fieldName) {
        super(fieldName);
    }

    @Override
    public void setOperation(String s) {

    }

    @Override
    public void setParametersMap(Map params) {

    }

    @Override
    public Object transform(Object orig) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return evaluateFieldValue(orig, fieldName);
    }
}