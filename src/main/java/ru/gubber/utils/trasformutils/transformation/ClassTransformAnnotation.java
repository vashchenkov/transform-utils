package ru.gubber.utils.trasformutils.transformation;


import ru.gubber.utils.trasformutils.transformation.transformator.NothingFiltrator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:44
 * Аннотация, которая описывает правило трансформации данных из
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE
})
public @interface ClassTransformAnnotation {

    Class transformator();

    String[] paramNames() default {};

    String[] paramValues() default {};

    Class collectionFiltrator() default NothingFiltrator.class;
}