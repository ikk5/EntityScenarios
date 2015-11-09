package org.mousepilots.es.model.impl;

import java.util.Map;
import javax.persistence.metamodel.Type;
import org.mousepilots.es.model.ManagedTypeES;
import org.mousepilots.es.model.MapAttributeES;
import org.mousepilots.es.model.MemberES;

/**
 * @author Nicky Ernste
 * @version 1.0, 9-11-2015
 * @param <T> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map
 */
public class MapAttributeESImpl<T, K, V> extends PluralAttributeESImpl<T, Map<K, V>, V>implements MapAttributeES<T, K, V> {

    private final Class<K> keyJavaType;
    private final Type<K> keyType;

    public MapAttributeESImpl(Class<K> keyJavaType, Type<K> keyType, Type<V> elementType, BindableType bindableType, Class<V> bindableJavaType, String name, PersistentAttributeType persistentAttributeType, MemberES javaMember, int ordinal, boolean readOnly, boolean collection, boolean association, ManagedTypeES declaringType, Class<Map<K, V>> javaType) {
        super(CollectionType.MAP, elementType, bindableType, bindableJavaType, name, persistentAttributeType, javaMember, ordinal, readOnly, collection, association, declaringType, javaType);
        this.keyJavaType = keyJavaType;
        this.keyType = keyType;
    }

    @Override
    public Class<K> getKeyJavaType() {
        return keyJavaType;
    }

    @Override
    public Type<K> getKeyType() {
        return keyType;
    }
}