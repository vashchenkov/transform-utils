package ru.gubber.utils.trasformutils.transformation.transformator;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 11:13
 */
public class ExpressionLanguageTransformator extends ATransformator {

    private String operation;
    private Map params;
    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    public ExpressionLanguageTransformator(String fieldName) {
        super(fieldName);
    }

    @Override
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public void setParametersMap(Map params) {
        this.params = params;
    }

    @Override
    public Object transform(Object orig) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // Create or retrieve a JexlEngine
        JexlEngine jexl = new JexlEngine();
        // Create an expression object
        String jexlExp = operation;
        Expression e = jexl.createExpression(jexlExp);

        // Create a context and add data
        JexlContext jc = new MapContext();
        jc.set("foo", orig);
        jc.set("ymd", ymd);
        if (params != null)
            for (Iterator iterator = params.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                jc.set(key, params.get(key));
            }
        // Now evaluate the expression, getting the result
        return e.evaluate(jc);
    }
}