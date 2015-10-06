package ru.gubber.utils.trasformutils.transformation.transformator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:38
 */
public abstract class ATransformator implements ITransfiormator {

    private static Logger logger = Logger.getLogger(ATransformator.class);

    protected String fieldName;

    protected ATransformator(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Вычисляет значение свойства объекта. Можно использовать сквозные свайства, на пример: user.client.id
     *
     * @param orig
     * @param fullFieldName
     * @return
     */
    final protected Object evaluateFieldValue(Object orig, String fullFieldName) {

        Object o = null;
        try {
            o = PropertyUtils.getProperty(orig, fullFieldName);
        } catch (Exception e) {
            logger.error(e, e);
        }
        return o;
    }

    public abstract void setOperation(String s);
}