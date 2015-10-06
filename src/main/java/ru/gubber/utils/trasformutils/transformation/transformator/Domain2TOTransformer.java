package ru.gubber.utils.trasformutils.transformation.transformator;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 13.09.2014
 * Time: 23:19
 *  Класс, производящий трансформацию доменных объектов в объекты, отправляющиеся на клиент.
 */

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import ru.gubber.utils.trasformutils.transformation.ClassTransformAnnotation;
import ru.gubber.utils.trasformutils.transformation.TransformAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Domain2TOTransformer {

    private static Logger logger = Logger.getLogger(Domain2TOTransformer.class);


    public static Object transform(Object orig, Class clazz) {
        Object result = null;
        Map<String, Object> context = initContext(clazz);
        if (orig instanceof Map) {
            Map src = (Map) orig;
            Map dst = newInstance(src.getClass());
            for (Object key : src.keySet()) {
                Object resObj = transformSingle(src.get(key), clazz, context);
                dst.put(key, resObj);
            }
            result = dst;
        } else if (orig instanceof Collection) {
            Collection src = (Collection) orig;
            Collection collection;
            if (Set.class.isAssignableFrom(src.getClass())) {
                collection = new HashSet();
            } else if (List.class.isAssignableFrom(src.getClass())) {
                collection = new ArrayList();
            } else {
                collection = newInstance(src.getClass());
            }
            for (Object obj : ((Collection) orig)) {
                Object resObj = transformSingle(obj, clazz, context);
                collection.add(resObj);
            }
            result = collection;
        } else if (orig != null) {
            result = transformSingle(orig, clazz, context);
        }
        return result;
    }

    private static Map<String, Object> initContext(Class clazz) {
        ClassTransformAnnotation annotation = (ClassTransformAnnotation) clazz.getAnnotation(ClassTransformAnnotation.class);
        if (annotation != null) {
            try {
                AbstractClassTransformator transformator = (AbstractClassTransformator) annotation.transformator().newInstance();
                return transformator.createContext();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Ошибка конвертации:\n" +
                        "\n clazz = " + clazz.getName(), e);
            }
        }
        return new HashMap<>();
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Ошибка конвертации объекта", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Ошибка конвертации объекта", e);
        }
    }

    private static Object transformSingle(Object orig, Class clazz, Map<String, Object> context) {
        return fillSingle(orig, newInstance(clazz), context);
    }

    private static Object fillSingle(Object orig, Object result, Map<String, Object> context) {
        if (result == null) {
            throw new RuntimeException("Ошибка конвертации объекта");
        }

        Class clazz = result.getClass();

        ClassTransformAnnotation annotation = (ClassTransformAnnotation) clazz.getAnnotation(ClassTransformAnnotation.class);
        if (annotation != null) {
            try {
                AbstractClassTransformator transformator = (AbstractClassTransformator) annotation.transformator().newInstance();
                result = transformator.transform2TO(orig, context);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException("Ошибка конвертации объекта:\n" +
                        "orig = " + orig + "\n clazz = " + clazz.getName(), e);
            }
        } else
            try {

                try {
                    PropertyUtils.copyProperties(result, orig);
                } catch (IllegalArgumentException e) {
//                logger.error(e);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка конвертации объекта:\n" +
                            "orig = " + orig + "\n clazz = " + clazz.getName(), e);
                }

                for (FieldDescription descr : getFieldDescriptions(clazz)) {
                    if (descr.isCollectionAssignable) {
                        ICollectionFiltrator filtrator = descr.collectionFiltratorConctructor.newInstance();
                        Collection origCollection = (Collection) descr.transformator.evaluateFieldValue(orig, descr.origFieldName);
                        origCollection = filtrator.filtrate(origCollection);
                        Collection value = (Collection) transform(origCollection, descr.annotationTransformationClass);
                        PropertyUtils.setProperty(result, descr.field.getName(), value);
                    } else {
                        Object transformValue = descr.transformator.transform(orig);
                        if (!Object.class.equals(descr.annotationTransformationClass)) {
                            transformValue = transform(transformValue, descr.annotationTransformationClass);
                        }
                        PropertyUtils.setProperty(result, descr.field.getName(), transformValue);
                    }
                }

            } catch (InstantiationException e) {
                throw new RuntimeException("Ошибка конвертации объекта", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка конвертации объекта", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Ошибка конвертации объекта", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Ошибка конвертации объекта", e);
            }


        return result;
    }

    protected static class FieldDescription {
        protected Field field;
        protected String origFieldName;
        protected Class annotationTransformationClass;

        protected boolean isCollectionAssignable;
        protected Constructor<ICollectionFiltrator> collectionFiltratorConctructor;

        protected boolean isAnnotationTransformatorAssignable;

        protected ATransformator transformator;

    }

    private static final ConcurrentHashMap<Class, FieldDescription[]> fieldDescriptions = new ConcurrentHashMap<Class, FieldDescription[]>();

    private static FieldDescription[] getFieldDescriptions(Class clazz) {
        if (clazz == null) {
            return new FieldDescription[0];
        }

        if (fieldDescriptions.containsKey(clazz)) {
            return fieldDescriptions.get(clazz);
        }

        Field[] fields = clazz.getDeclaredFields();
        List<FieldDescription> resultList = new ArrayList<FieldDescription>(fields.length);

        try {
            for (Field field : fields) {
                TransformAnnotation annotation = field.getAnnotation(TransformAnnotation.class);
                if (annotation != null) {
                    FieldDescription descr = new FieldDescription();
                    descr.field = field;
                    descr.origFieldName = annotation.originalField();
                    Constructor constructor = annotation.transformator().getConstructor(String.class);
                    descr.transformator = (ATransformator) constructor.newInstance(descr.origFieldName);
                    descr.annotationTransformationClass = annotation.transformationClass();

                    if (descr.isCollectionAssignable = Collection.class.isAssignableFrom(field.getType())) {
                        descr.collectionFiltratorConctructor = annotation.collectionFiltrator().getConstructor();

                    } else if (descr.isAnnotationTransformatorAssignable = ATransformator.class.isAssignableFrom(annotation.transformator())) {
                        if (annotation.paramNames().length != annotation.paramValues().length)
                            throw new RuntimeException("Количество названий параметров (" + annotation.paramNames().length +
                                    ") должно совпадать с количеством значений параметров ("
                                    + annotation.paramValues().length + ")");
                        Map pars = new HashMap();
                        for (int j = 0; j < annotation.paramNames().length; j++) {
                            String paramName = annotation.paramNames()[j];
                            pars.put(paramName, annotation.paramValues()[j]);
                        }
                        descr.transformator.setParametersMap(pars);
                        descr.transformator.setOperation(annotation.operation());

                    } else {
                        descr.transformator = new DummyTransformator(descr.origFieldName);
                    }
                    resultList.add(descr);
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("Ошибка составления описателей объекта", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Ошибка составления описателей объекта", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Ошибка составления описателей объекта", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Ошибка составления описателей объекта", e);
        }


        FieldDescription[] result = resultList.toArray(new FieldDescription[resultList.size()]);
        // пожалуй, не страшно если FieldDescription[] для одного класса посчитаются несколько раз, главное что-бы не всё время
        fieldDescriptions.put(clazz, result);

        return result;
    }
}