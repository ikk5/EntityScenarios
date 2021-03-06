package org.mousepilots.es.core.model;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.metamodel.MapAttribute;

/**
 * Instances of the type @{link MapAttributeES} represent persistent
 * {@link Map}-valued attributes. *
 * @param <T> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map
 * @see PluralAttributeES
 * @see MapAttribute
 * @author Roy Cleven
 * @version 1.0, 19-10-2015
 */
public interface MapAttributeES<T,K,V> extends PluralAttributeES<T, java.util.Map<K,V>,V>, MapAttribute<T, K, V> {

    @Override
    public default CollectionType getCollectionType() {
        return CollectionType.MAP;
    }

    @Override
    public TypeES<K> getKeyType();
    
    

    @Override
    public default Map<K, V> createEmpty() {
        return new HashMap<>();
    }

    
}