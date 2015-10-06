package ru.gubber.utils.trasformutils.transformation.transformator;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:40
 * Реализации этого интерфейса будут отвечать за трансформацию свойств из доменного объекта в объект,
 * передаваемый на клиент.
 */
public interface ITransfiormator {

    void setParametersMap(Map params);

    Object transform(Object orig) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}