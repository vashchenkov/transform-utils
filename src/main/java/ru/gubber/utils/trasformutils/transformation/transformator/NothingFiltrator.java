package ru.gubber.utils.trasformutils.transformation.transformator;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: gubber
 * Date: 14.09.2014
 * Time: 7:46
 */
public class NothingFiltrator implements ICollectionFiltrator {

    @Override
    public Collection filtrate(Collection toFiltrate) {
        return toFiltrate;
    }
}