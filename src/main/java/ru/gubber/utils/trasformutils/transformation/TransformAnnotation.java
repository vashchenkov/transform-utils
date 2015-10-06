package ru.gubber.utils.trasformutils.transformation;

import ru.gubber.utils.trasformutils.transformation.transformator.DummyTransformator;
import ru.gubber.utils.trasformutils.transformation.transformator.NothingFiltrator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:37
 * Аннотация, которая описывает правило трансформации данных из
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD
})
public @interface TransformAnnotation {

    String originalField();

    Class transformator() default DummyTransformator.class;

    Class transformationClass() default Object.class;

    /**
     * выражение, по которому, будет производиться преобразование.(используется синтакс jexl).
     * Идентификатор исходного объекта в выражении "foo". Остальные объекты,
     * которые могут быть использованы в выражении передаются через массивы параметров.
     *
     * @return
     */
    String operation() default "";

    String[] paramNames() default {};

    String[] paramValues() default {};

    Class collectionFiltrator() default NothingFiltrator.class;
}